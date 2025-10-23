package net.fhirfactory.dricats.datagrid.satellite.messagegrid;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
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

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Kafka client for interacting with the central broker from satellite nodes.
 * Provides lightweight publish/subscribe APIs compatible with CentralMessageBroker.
 * Uses the same configuration keys and Java serialization for payloads.
 */
@ApplicationScoped
public class CentralMessageBrokerClient {
    private static final Logger LOG = LoggerFactory.getLogger(CentralMessageBrokerClient.class);

    // Configuration environment variables (same names as central broker)
    public static final String ENV_BOOTSTRAP = "DRICATS_KAFKA_BOOTSTRAP_SERVERS";
    public static final String ENV_CLIENT_ID = "DRICATS_KAFKA_CLIENT_ID";
    public static final String ENV_GROUP_ID = "DRICATS_KAFKA_GROUP_ID";

    private KafkaProducer<String, byte[]> producer;
    private KafkaConsumer<String, byte[]> consumer;

    private final Map<String, List<Consumer<MessageObject>>> subscribers = new ConcurrentHashMap<>();
    private ExecutorService consumerExecutor;
    private volatile boolean running;

    @PostConstruct
    public void start() {
        LOG.info("CentralMessageBrokerClient.start(): Initializing Kafka clients");
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, resolveBootstrap());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        // Use a distinct default client id to help tracing satellite clients
        producerProps.put(ProducerConfig.CLIENT_ID_CONFIG, resolveClientId("dricats-satellite"));
        this.producer = new KafkaProducer<>(producerProps);

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, resolveBootstrap());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, resolveGroupId("dricats-satellite-consumers"));
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        this.consumer = new KafkaConsumer<>(consumerProps);
        this.consumerExecutor = Executors.newSingleThreadExecutor(r -> new Thread(r, "CentralMessageBrokerClient-Consumer"));
        this.running = false;
        LOG.info("CentralMessageBrokerClient: initialized (bootstrap={})", producerProps.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
    }

    @PreDestroy
    public void stop() {
        LOG.info("CentralMessageBrokerClient.stop(): Shutting down Kafka clients");
        running = false;
        if (consumerExecutor != null) {
            consumerExecutor.shutdownNow();
        }
        if (consumer != null) {
            try { consumer.wakeup(); } catch (Exception ignore) {}
            try { consumer.close(); } catch (Exception ignore) {}
        }
        if (producer != null) {
            try { producer.flush(); } catch (Exception ignore) {}
            try { producer.close(); } catch (Exception ignore) {}
        }
    }

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
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topicName, key, payload);
        LOG.debug("CentralMessageBrokerClient: Publishing id={} to topic={}", key, topicName);
        producer.send(record, (md, ex) -> {
            if (ex != null) {
                LOG.error("CentralMessageBrokerClient: Failed to publish id={} to topic={}", key, topicName, ex);
            } else if (md != null) {
                LOG.trace("CentralMessageBrokerClient: Published id={} to topic={} partition={} offset={}", key, topicName, md.partition(), md.offset());
            }
        });
    }

    public synchronized void subscribe(Topic topic, Consumer<MessageObject> handler) {
        String topicName = resolveTopicName(topic);
        if (topicName == null || topicName.isBlank()) {
            throw new IllegalArgumentException("Topic name cannot be null or blank");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }
        subscribers.computeIfAbsent(topicName, t -> new ArrayList<>()).add(handler);
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
            LOG.info("CentralMessageBrokerClient: Consumer loop started; topics={}", subscribers.keySet());
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
                                    LOG.error("CentralMessageBrokerClient: subscriber threw for topic={}", topic, handlerEx);
                                }
                            }
                        } catch (Exception ex) {
                            LOG.error("CentralMessageBrokerClient: failed to deserialize MessageObject for topic={}", topic, ex);
                        }
                    }
                }
            } catch (org.apache.kafka.common.errors.WakeupException we) {
                LOG.debug("CentralMessageBrokerClient: consumer wakeup");
            } catch (Exception ex) {
                LOG.error("CentralMessageBrokerClient: consumer loop error", ex);
            } finally {
                try { consumer.close(); } catch (Exception ignore) {}
                LOG.info("CentralMessageBrokerClient: Consumer loop stopped");
            }
        });
    }

    // --- Helpers ---
    protected String resolveTopicName(Topic topic) {
        if (topic == null || topic.getTopicName() == null) {
            return null;
        }
        return topic.getTopicName().getCommonName().getValue();
    }

    protected String resolveBootstrap() {
        String fromEnv = System.getenv(ENV_BOOTSTRAP);
        String fromProp = System.getProperty("dricats.kafka.bootstrap.servers");
        return firstNonBlank(fromProp, fromEnv, "localhost:9092");
    }

    protected String resolveClientId(String def) {
        String fromEnv = System.getenv(ENV_CLIENT_ID);
        String fromProp = System.getProperty("dricats.kafka.client.id");
        return firstNonBlank(fromProp, fromEnv, def);
    }

    protected String resolveGroupId(String def) {
        String fromEnv = System.getenv(ENV_GROUP_ID);
        String fromProp = System.getProperty("dricats.kafka.group.id");
        return firstNonBlank(fromProp, fromEnv, def);
    }

    protected String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return null;
    }

    protected byte[] serialize(MessageObject message) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(message);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize MessageObject", e);
        }
    }

    protected MessageObject deserialize(byte[] bytes) {
        try {
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                return (MessageObject) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deserialize MessageObject", e);
        }
    }
}
