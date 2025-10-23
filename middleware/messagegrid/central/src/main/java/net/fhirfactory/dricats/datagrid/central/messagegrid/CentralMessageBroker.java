/*
 * Copyright (c) 2025 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this applications and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.dricats.datagrid.central.messagegrid;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.dricats.internals.events.messages.MessageObject;
import net.fhirfactory.dricats.internals.topics.Topic;
import net.fhirfactory.dricats.datagrid.central.messagegrid.spi.IMessagePersistenceStore;
import net.fhirfactory.dricats.datagrid.central.messagegrid.spi.PendingMessage;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.sql.*;

/**
 * Minimal Kafka broker/persistence support for MessageObject using Topic-derived topic names.
 *
 * This service provides simple publish/subscribe capabilities built on top of Kafka clients.
 * It avoids external framework dependencies and uses Java serialization as MessageObject implements Serializable.
 *
 * Notes:
 * - Bootstrap servers, clientId and groupId are configurable via system properties or environment variables.
 * - This implementation starts a single polling thread for all subscriptions.
 * - To keep changes minimal and tests lightweight, the consumer thread is only started when at least one subscription is registered.
 */
@ApplicationScoped
public class
CentralMessageBroker {
    private static final Logger LOG = LoggerFactory.getLogger(CentralMessageBroker.class);

    public static final String ENV_BOOTSTRAP = "DRICATS_KAFKA_BOOTSTRAP_SERVERS";
    public static final String ENV_CLIENT_ID = "DRICATS_KAFKA_CLIENT_ID";
    public static final String ENV_GROUP_ID = "DRICATS_KAFKA_GROUP_ID";

    // H2 persistence configuration (optional)
    public static final String ENV_PERSIST_ENABLED = "DRICATS_BROKER_PERSIST_ENABLED";
    public static final String ENV_PERSIST_URL = "DRICATS_BROKER_PERSIST_URL";
    public static final String ENV_PERSIST_USER = "DRICATS_BROKER_PERSIST_USER";
    public static final String ENV_PERSIST_PASS = "DRICATS_BROKER_PERSIST_PASS";

    private KafkaProducer<String, byte[]> producer;
    private KafkaConsumer<String, byte[]> consumer;

    private final Map<String, List<Consumer<MessageObject>>> subscribers = new ConcurrentHashMap<>();
    private ExecutorService consumerExecutor;
    private volatile boolean running;

    // persistence
    private boolean persistenceEnabled;
    private String jdbcUrl;
    private String jdbcUser;
    private String jdbcPass;
    private ExecutorService replayExecutor;

    @Inject
    private Instance<IMessagePersistenceStore> persistenceStoreInstance;
    private IMessagePersistenceStore persistenceStore;

    @PostConstruct
    public void start() {
        LOG.info("KafkaMessageBroker.start(): Initializing Kafka producer/consumer configuration");

        // Resolve persistence configuration via SPI
        this.persistenceStore = (persistenceStoreInstance != null && persistenceStoreInstance.isResolvable()) ? persistenceStoreInstance.get() : null;
        if (this.persistenceStore != null && this.persistenceStore.enabled()) {
            try {
                // start() is idempotent in implementation
                this.persistenceStore.start();
                this.persistenceEnabled = true;
                LOG.info("KafkaMessageBroker: Persistence store enabled: {}", persistenceStore.getClass().getName());
            } catch (Exception e) {
                LOG.warn("KafkaMessageBroker: Failed to initialize persistence store. Continuing without persistence.", e);
                this.persistenceEnabled = false;
            }
        } else {
            this.persistenceEnabled = false;
            LOG.info("KafkaMessageBroker: Persistence disabled or no store available");
        }

        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, resolveBootstrap());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        producerProps.put(ProducerConfig.CLIENT_ID_CONFIG, resolveClientId());
        this.producer = new KafkaProducer<>(producerProps);
        LOG.info("KafkaMessageBroker: Producer created with client.id={} and bootstrap.servers={}",
                producerProps.getProperty(ProducerConfig.CLIENT_ID_CONFIG),
                producerProps.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, resolveBootstrap());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, resolveGroupId());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        this.consumer = new KafkaConsumer<>(consumerProps);
        this.consumerExecutor = Executors.newSingleThreadExecutor(r -> new Thread(r, "KafkaMessageBroker-Consumer"));
        this.running = false; // will start when first subscription is added

        // Start replay of pending messages if persistence is enabled
        if (persistenceEnabled) {
            this.replayExecutor = Executors.newSingleThreadExecutor(r -> new Thread(r, "KafkaMessageBroker-Replay"));
            this.replayExecutor.submit(this::replayPendingFromStore);
        }
    }

    @PreDestroy
    public void stop() {
        LOG.info("KafkaMessageBroker.stop(): Shutting down Kafka producer/consumer");
        running = false;
        if (consumerExecutor != null) {
            consumerExecutor.shutdownNow();
        }
        if (replayExecutor != null) {
            replayExecutor.shutdownNow();
        }
        if (consumer != null) {
            try { consumer.wakeup(); } catch (Exception ignore) {}
            try { consumer.close(); } catch (Exception ignore) {}
        }
        if (producer != null) {
            try { producer.flush(); } catch (Exception ignore) {}
            try { producer.close(); } catch (Exception ignore) {}
        }
        if (persistenceStore != null) {
            try { persistenceStore.stop(); } catch (Exception ignore) {}
        }
    }

    /**
     * Publish a MessageObject to the Kafka topic derived from the Topic instance.
     */
    public void publish(Topic topic, MessageObject message) {
        String topicName = resolveTopicName(topic);
        if (topicName == null || topicName.isBlank()) {
            throw new IllegalArgumentException("Topic name cannot be null or blank");
        }
        if (message == null) {
            throw new IllegalArgumentException("MessageObject cannot be null");
        }
        byte[] payload = serialize(message);
        String key = (message.getId() != null ? message.getId().getValue() : null);

        // Persist before sending (best-effort) so we can retry later if needed
        String pid = null;
        if (persistenceEnabled && persistenceStore != null) {
            try {
                pid = persistenceStore.persist(topicName, key, payload);
            } catch (Exception e) {
                LOG.warn("Failed to persist message prior to publish; proceeding to send to Kafka. topic={} id={}", topicName, key, e);
            }
        }

        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topicName, key, payload);
        String finalPid = pid;
        LOG.debug("Publishing message id={} to topic={}", key, topicName);
        producer.send(record, (md, ex) -> {
            if (ex != null) {
                LOG.error("Failed to publish message id={} to topic={}", key, topicName, ex);
                // Keep persisted row for replay if enabled
            } else if (md != null) {
                LOG.debug("Published message id={} to topic={} partition={} offset={}", key, topicName, md.partition(), md.offset());
                if (persistenceEnabled && finalPid != null && persistenceStore != null) {
                    try { persistenceStore.delete(finalPid); } catch (Exception delEx) {
                        LOG.warn("Failed to delete pending persisted message pid={} after successful send", finalPid, delEx);
                    }
                }
            }
        });
    }

    /**
     * Subscribe to a Topic. The given handler will be invoked for each MessageObject received.
     * The underlying consumer will be started (if not already) and subscribed to the topic.
     */
    public synchronized void subscribe(Topic topic, Consumer<MessageObject> handler) {
        String topicName = resolveTopicName(topic);
        if (topicName == null || topicName.isBlank()) {
            throw new IllegalArgumentException("Topic name cannot be null or blank");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }
        subscribers.computeIfAbsent(topicName, t -> new ArrayList<>()).add(handler);
        // Update consumer subscription list
        Set<String> topicSet = subscribers.keySet();
        if (!topicSet.isEmpty()) {
            consumer.subscribe(new ArrayList<>(topicSet));
            if (!running) {
                startConsumerLoop();
            }
        }
    }

    private void startConsumerLoop() {
        running = true;
        consumerExecutor.submit(() -> {
            LOG.info("KafkaMessageBroker: Consumer loop started; subscribed to {} topics", subscribers.keySet().size());
            try {
                while (running) {
                    ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofSeconds(1));
                    for (ConsumerRecord<String, byte[]> rec : records) {
                        String topic = rec.topic();
                        byte[] value = rec.value();
                        try {
                            MessageObject msg = deserialize(value);
                            List<Consumer<MessageObject>> handlers = subscribers.getOrDefault(topic, Collections.emptyList());
                            for (Consumer<MessageObject> h : handlers) {
                                try { h.accept(msg); } catch (Exception handlerEx) {
                                    LOG.error("Subscriber handler threw exception for topic={}", topic, handlerEx);
                                }
                            }
                        } catch (Exception ex) {
                            LOG.error("Failed to deserialize MessageObject from topic={}", topic, ex);
                        }
                    }
                }
            } catch (org.apache.kafka.common.errors.WakeupException we) {
                LOG.debug("Kafka consumer wakeup/close");
            } catch (Exception ex) {
                LOG.error("Kafka consumer loop error", ex);
            } finally {
                try { consumer.close(); } catch (Exception ignore) {}
                LOG.info("KafkaMessageBroker: Consumer loop stopped");
            }
        });
    }

    // Helpers
    protected String resolveTopicName(Topic topic) {
        if (topic == null || topic.getTopicName() == null) {
            return null;
        }
        // Use the common name textual representation of the QualifiedName
        return topic.getTopicName().getCommonName().getValue();
    }

    protected String resolveBootstrap() {
        String fromEnv = System.getenv(ENV_BOOTSTRAP);
        String fromProp = System.getProperty("dricats.kafka.bootstrap.servers");
        return firstNonBlank(fromProp, fromEnv, "localhost:9092");
    }

    protected String resolveClientId() {
        String fromEnv = System.getenv(ENV_CLIENT_ID);
        String fromProp = System.getProperty("dricats.kafka.client.id");
        return firstNonBlank(fromProp, fromEnv, "dricats-central");
    }

    protected String resolveGroupId() {
        String fromEnv = System.getenv(ENV_GROUP_ID);
        String fromProp = System.getProperty("dricats.kafka.group.id");
        return firstNonBlank(fromProp, fromEnv, "dricats-central-consumers");
    }

    // --- Persistence helpers ---
    protected boolean resolvePersistenceEnabled() {
        String fromProp = System.getProperty("dricats.broker.persist.enabled");
        String fromEnv = System.getenv(ENV_PERSIST_ENABLED);
        String val = firstNonBlank(fromProp, fromEnv, "true");
        return Boolean.parseBoolean(val);
    }

    protected String resolveJdbcUrl() {
        String fromProp = System.getProperty("dricats.broker.persist.url");
        String fromEnv = System.getenv(ENV_PERSIST_URL);
        return firstNonBlank(fromProp, fromEnv, "jdbc:h2:file:./data/dricats-broker;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE");
    }

    protected void initPersistence() throws SQLException {
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS pending_messages (" +
                    "pid VARCHAR(100) PRIMARY KEY, " +
                    "topic VARCHAR(255) NOT NULL, " +
                    "key_str VARCHAR(255), " +
                    "payload BLOB NOT NULL, " +
                    "ts BIGINT NOT NULL)");
            // Optional index to speed up reads by ts
            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_pending_ts ON pending_messages(ts)");
        }
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass);
    }

    protected void savePending(String pid, String topic, String key, byte[] payload) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(
                "MERGE INTO pending_messages(pid, topic, key_str, payload, ts) KEY(pid) VALUES(?,?,?,?,?)")) {
            ps.setString(1, pid);
            ps.setString(2, topic);
            ps.setString(3, key);
            ps.setBytes(4, payload);
            ps.setLong(5, System.currentTimeMillis());
            ps.executeUpdate();
        }
    }

    protected void deletePending(String pid) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(
                "DELETE FROM pending_messages WHERE pid=?")) {
            ps.setString(1, pid);
            ps.executeUpdate();
        }
    }

    protected void replayPendingFromStore() {
        LOG.info("KafkaMessageBroker: Starting pending message replay loop (SPI)");
        int emptyCycles = 0;
        while (!Thread.currentThread().isInterrupted()) {
            int processed = 0;
            try {
                if (persistenceStore == null) {
                    break;
                }
                List<PendingMessage> batch = persistenceStore.loadBatch(100);
                for (PendingMessage pm : batch) {
                    ProducerRecord<String, byte[]> rec = new ProducerRecord<>(pm.getTopic(), pm.getKey(), pm.getPayload());
                    final String pid = pm.getPid();
                    try {
                        producer.send(rec, (md, ex) -> {
                            if (ex == null) {
                                try { persistenceStore.delete(pid); } catch (Exception delEx) {
                                    LOG.warn("Replay(SPI): failed to delete pid={}", pid, delEx);
                                }
                            } else {
                                LOG.warn("Replay(SPI): send failed for pid={} topic={} - will retry later", pid, pm.getTopic(), ex);
                            }
                        });
                        processed++;
                    } catch (Exception sendEx) {
                        LOG.warn("Replay(SPI): immediate send threw for pid={} topic={} - will retry later", pid, pm.getTopic(), sendEx);
                    }
                }
            } catch (Exception e) {
                LOG.error("Replay(SPI): error loading pending messages", e);
            }

            if (processed == 0) {
                emptyCycles++;
            } else {
                emptyCycles = 0;
            }
            try {
                Thread.sleep(emptyCycles < 10 ? 500L : 2000L);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        LOG.info("KafkaMessageBroker: Pending message replay loop (SPI) stopped");
    }

    private String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return null;
    }

    // Serialization helpers using Java built-in serialization (MessageObject implements Serializable)
    protected byte[] serialize(MessageObject message) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(message);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to serialize MessageObject", e);
        }
    }

    protected MessageObject deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInputStream ois = new ObjectInputStream(bis)) {
            Object obj = ois.readObject();
            return (MessageObject) obj;
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to deserialize MessageObject", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found during MessageObject deserialization", e);
        }
    }
}
