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
package net.fhirfactory.dricats.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.fhirfactory.dricats.internals.common.naming.datatypes.DistinguishedNameEntry;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.datatypes.ComponentMessagingStatistics;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.ui.restclient.MainRESTClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class AdministrationUI extends Application {
    private static final Logger LOG = LoggerFactory.getLogger(AdministrationUI.class);

    private MainRESTClient client;
    private TreeView<ApplicationComponent> treeView;
    private TableView<KVRow> metricsTable;
    private TableView<KVRow> detailsTable;
    private TreeView<String> uniqueNameTree;
    private Button refreshMetricsBtn;

    @Override
    public void start(Stage stage) {
        // Prompt for server URL before showing main window
        TextInputDialog urlDialog = new TextInputDialog("http://localhost:12000");
        urlDialog.setTitle("Connect to Server");
        urlDialog.setHeaderText("Enter OAM Server Base URL");
        urlDialog.setContentText("Base URL:");
        java.util.Optional<String> urlResult = urlDialog.showAndWait();
        if (urlResult.isEmpty() || urlResult.get() == null || urlResult.get().isBlank()) {
            LOG.info("[UI] No server URL provided. Exiting application.");
            Platform.exit();
            return;
        }
        String baseUrl = urlResult.get().trim();
        client = new MainRESTClient(baseUrl);

        // Load FXML-based UI
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/net/fhirfactory/dricats/ui/MainView.fxml")
            );
            javafx.scene.Parent root = loader.load();
            MainViewController controller = loader.getController();
            if (controller != null) {
                controller.setBaseUrl(baseUrl);
            }
            stage.setTitle("DRICaTS UI");
            stage.setScene(new Scene(root, 1100, 600));
            stage.show();
        } catch (Exception e) {
            LOG.error("[UI] Failed to load FXML, falling back to programmatic UI", e);
            // Fallback: Build multi-tab UI (OAM, tasking, routes)
            TabPane tabPane = new TabPane();
            tabPane.getTabs().addAll(
                    new TopologyTab(baseUrl),
                    new TaskingTab(),
                    new RoutesTab(baseUrl)
            );
            stage.setTitle("DRICaTS UI");
            stage.setScene(new Scene(tabPane, 1100, 600));
            stage.show();
        }
        return;
    }

    private void refreshDetails(ApplicationComponent summary) {
        if (detailsTable == null) return;
        javafx.collections.ObservableList<KVRow> rows = javafx.collections.FXCollections.observableArrayList();
        if (summary == null) {
            rows.add(new KVRow("Info", "No component selected."));
            detailsTable.setItems(rows);
            return;
        }
        try { rows.add(new KVRow("Name", Objects.toString(summary.getName(), ""))); } catch (Exception ignored) {}
        try {
            if (summary.getObjectId() != null && summary.getObjectId().getKeyValue() != null)
                rows.add(new KVRow("ID", summary.getObjectId().getKeyValue()));
        } catch (Exception ignored) {}
        try {
            if (summary.getObjectId().getName() != null)
                rows.add(new KVRow("CommonName", Objects.toString(summary.getObjectId().getName().getValue(), "")));
        } catch (Exception ignored) {}
        try { rows.add(new KVRow("Element Type", Objects.toString(summary.getElementType(), ""))); } catch (Exception ignored) {}
        try { rows.add(new KVRow("Specialization", Objects.toString(summary.getSpecialization(), ""))); } catch (Exception ignored) {}
        try { rows.add(new KVRow("Documentation", Objects.toString(summary.getDocumentation(), ""))); } catch (Exception ignored) {}
        try {
            if (summary.getParent() != null)
                rows.add(new KVRow("Parent", Objects.toString(summary.getParent().getLocalObjectId().getName().getValue(), "")));
        } catch (Exception ignored) {}
        try {
            int count = summary.getSubComponents() == null ? 0 : summary.getSubComponents().size();
            rows.add(new KVRow("Subcomponents", Integer.toString(count)));
        } catch (Exception ignored) {}
        try {
            if (summary.getComponentStatus() != null) {
                rows.add(new KVRow("Status", Objects.toString(summary.getComponentStatus().getComponentStatus(), "")));
                String desc = summary.getComponentStatus().getComponentStatusDescription();
                if (desc != null && !desc.isBlank()) rows.add(new KVRow("Status Description", desc));
                if (summary.getComponentStatus().getStartupInstant() != null)
                    rows.add(new KVRow("Startup", Objects.toString(summary.getComponentStatus().getStartupInstant(), "")));
                if (summary.getComponentStatus().getLastActivityInstant() != null)
                    rows.add(new KVRow("Last Activity", Objects.toString(summary.getComponentStatus().getLastActivityInstant(), "")));
                if (summary.getComponentStatus().getLastHeartbeatInstant() != null)
                    rows.add(new KVRow("Last Heartbeat", Objects.toString(summary.getComponentStatus().getLastHeartbeatInstant(), "")));
            }
        } catch (Exception ignored) {}
        detailsTable.setItems(rows);
    }

    private void refreshUniqueName(ApplicationComponent summary) {
        if (uniqueNameTree == null) return;
        TreeItem<String> root = new TreeItem<>("UniqueName");
        root.setExpanded(true);
        try {
            if (summary == null || summary.getObjectId() == null || summary.getObjectId().getFullyDistinguishedName() == null) {
                root.getChildren().add(new TreeItem<>("No selection"));
            } else {
                java.util.Map<Integer, DistinguishedNameEntry> entries =
                        summary.getObjectId().getFullyDistinguishedName().getUnqualifiedNameEntries();
                if (entries == null || entries.isEmpty()) {
                    root.getChildren().add(new TreeItem<>("<empty>"));
                } else {
                    java.util.List<Integer> keys = new java.util.ArrayList<>(entries.keySet());
                    java.util.Collections.sort(keys);
                    for (Integer k : keys) {
                        DistinguishedNameEntry e = entries.get(k);
                        String qual = e == null ? "" : java.util.Objects.toString(e.getQualifier(), "");
                        String val = e == null ? "" : java.util.Objects.toString(e.getValue(), "");
                        TreeItem<String> child = new TreeItem<>(qual + " = " + val);
                        root.getChildren().add(child);
                    }
                }
            }
        } catch (Exception ignored) {
            root.getChildren().add(new TreeItem<>("<error reading UniqueName>"));
        }
        uniqueNameTree.setRoot(root);
    }

    private void refreshMetrics(ApplicationComponent summary) {
        if (metricsTable == null) return;
        javafx.collections.ObservableList<KVRow> rows = javafx.collections.FXCollections.observableArrayList();
        if (summary == null) {
            rows.add(new KVRow("Info", "No component selected."));
            metricsTable.setItems(rows);
            return;
        }
        String id = MainRESTClient.resolveKey(summary);
        ApplicationComponentMetricsData md = client.getLatestMetrics(id);
        if (md == null) {
            rows.add(new KVRow("Info", "No metrics available for: " + Objects.toString(summary.getName(), id)));
            metricsTable.setItems(rows);
            return;
        }
        try { rows.add(new KVRow("Component", Objects.toString(md.getParticipantName(), ""))); } catch (Exception ignored) {}
        try { if (md.getComponentType() != null) rows.add(new KVRow("Type", Objects.toString(md.getComponentType(), ""))); } catch (Exception ignored) {}
        try { if (md.getComponentStartupInstant() != null) rows.add(new KVRow("Startup", Objects.toString(md.getComponentStartupInstant(), ""))); } catch (Exception ignored) {}
        try { if (md.getLastActivityInstant() != null) rows.add(new KVRow("Last Activity", Objects.toString(md.getLastActivityInstant(), ""))); } catch (Exception ignored) {}
        try { if (md.getComponentStatus() != null) rows.add(new KVRow("Status", Objects.toString(md.getComponentStatus(), ""))); } catch (Exception ignored) {}
        ComponentMessagingStatistics ms = md.getMessagingStatistics();
        if (ms != null) {
            rows.add(new KVRow("Messaging", ""));
            try { rows.add(new KVRow("Ingress", Objects.toString(ms.getIngresMessageCount(), "0"))); } catch (Exception ignored) {}
            try { rows.add(new KVRow("Egress Attempts", Objects.toString(ms.getEgressMessageAttemptCount(), "0"))); } catch (Exception ignored) {}
            try { rows.add(new KVRow("Egress Success", Objects.toString(ms.getEgressMessageSuccessCount(), "0"))); } catch (Exception ignored) {}
            try { rows.add(new KVRow("Egress Failures", Objects.toString(ms.getEgressMessageFailureCount(), "0"))); } catch (Exception ignored) {}
            try { rows.add(new KVRow("Internal Sent", Objects.toString(ms.getInternalDistributedMessageCount(), "0"))); } catch (Exception ignored) {}
            try { rows.add(new KVRow("Internal Received", Objects.toString(ms.getInternalReceivedMessageCount(), "0"))); } catch (Exception ignored) {}
        }
        metricsTable.setItems(rows);
    }

    private String formatMetrics(ApplicationComponentMetricsData md) {
        StringBuilder sb = new StringBuilder();
        sb.append("Component: ").append(Objects.toString(md.getParticipantName(), ""));
        if (md.getComponentType() != null) sb.append(" (type=").append(md.getComponentType()).append(")");
        sb.append('\n');
        Instant startup = md.getComponentStartupInstant();
        Instant last = md.getLastActivityInstant();
        if (startup != null) sb.append("Startup: ").append(startup).append('\n');
        if (last != null) sb.append("Last Activity: ").append(last).append('\n');
        if (md.getComponentStatus() != null) sb.append("Status: ").append(md.getComponentStatus()).append('\n');
        sb.append('\n');
        ComponentMessagingStatistics ms = md.getMessagingStatistics();
        if (ms != null) {
            sb.append("Messaging:\n");
            sb.append("  Ingress: ").append(ms.getIngresMessageCount()).append('\n');
            sb.append("  Egress Attempts: ").append(ms.getEgressMessageAttemptCount()).append('\n');
            sb.append("  Egress Success: ").append(ms.getEgressMessageSuccessCount()).append('\n');
            sb.append("  Egress Failures: ").append(ms.getEgressMessageFailureCount()).append('\n');
            sb.append("  Internal Sent: ").append(ms.getInternalDistributedMessageCount()).append('\n');
            sb.append("  Internal Received: ").append(ms.getInternalReceivedMessageCount()).append('\n');
        }
        return sb.toString();
    }

    private void loadRoots() {
        TreeItem<ApplicationComponent> hiddenRoot = new TreeItem<>();
        treeView.setRoot(hiddenRoot);
        List<ApplicationComponent> roots = null;
        try {
            roots = client.listComponents();
        } catch (Exception e) {
            LOG.warn("[UI] Failed to fetch component list: {}", e.toString());
        }
        if (roots == null) {
            LOG.info("[UI] No components returned (null list)");
            return;
        }
        LOG.info("[UI] Loaded {} components", roots.size());
        hiddenRoot.getChildren().clear();
        for (ApplicationComponent s : roots) {
            if (s != null) {
                hiddenRoot.getChildren().add(createTreeItem(s));
            }
        }
    }

    private TreeItem<ApplicationComponent> createTreeItem(ApplicationComponent s) {
        TreeItem<ApplicationComponent> item = new TreeItem<>(s);
        // Add a dummy child to show expandable arrow; real children loaded on demand
        item.getChildren().add(new TreeItem<>());
        item.expandedProperty().addListener((obs, o, n) -> {
            if (n) loadChildrenIfNeeded(item);
        });
        return item;
    }

    private void loadChildrenIfNeeded(TreeItem<ApplicationComponent> parentItem) {
        if (parentItem == null || parentItem.getValue() == null) return;
        // If already loaded (no placeholder), skip
        if (!hasPlaceholder(parentItem)) return;
        parentItem.getChildren().clear();

        String id = MainRESTClient.resolveKey(parentItem.getValue());
        List<ApplicationComponent> kids = client.listSubcomponents(id);
        if (kids.isEmpty()) {
            // keep no children
            return;
        }
        for (ApplicationComponent child : kids) {
            parentItem.getChildren().add(createTreeItem(child));
        }
    }

    private boolean hasPlaceholder(TreeItem<ApplicationComponent> item) {
        return item.getChildren().size() == 1 && item.getChildren().get(0).getValue() == null;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
    }

    public static class KVRow {
        private final javafx.beans.property.SimpleStringProperty key = new javafx.beans.property.SimpleStringProperty();
        private final javafx.beans.property.SimpleStringProperty value = new javafx.beans.property.SimpleStringProperty();
        public KVRow(String key, String value) { this.key.set(key); this.value.set(value); }
        public String getKey() { return key.get(); }
        public void setKey(String k) { key.set(k); }
        public javafx.beans.property.StringProperty keyProperty() { return key; }
        public String getValue() { return value.get(); }
        public void setValue(String v) { value.set(v); }
        public javafx.beans.property.StringProperty valueProperty() { return value; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
