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
package net.fhirfactory.dricats.middleware.oam.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.datatypes.ComponentMessagingStatistics;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class MainApp extends Application {
    private static final Logger LOG = LoggerFactory.getLogger(MainApp.class);

    private OamRestClient client;
    private TextField baseUrlField;
    private TreeView<ApplicationComponentSummary> treeView;
    private TextArea metricsArea;
    private Button refreshMetricsBtn;

    @Override
    public void start(Stage stage) {
        baseUrlField = new TextField("http://localhost:8080");
        client = new OamRestClient(baseUrlField.getText());

        Button reload = new Button("Reload");
        reload.setOnAction(e -> {
            client.setBaseUrl(baseUrlField.getText());
            LOG.info("[UI] Reload requested. Base URL set to: {}", client.getBaseUrl());
            loadRoots();
            refreshMetrics(null);
        });

        HBox top = new HBox(8, new Label("Base URL:"), baseUrlField, reload);

        treeView = new TreeView<>();
        treeView.setShowRoot(false);
        treeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(ApplicationComponentSummary item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String label = item.getName();
                    if (label == null || label.isBlank()) {
                        label = OamRestClient.resolveKey(item);
                    }
                    setText(label);
                }
            }
        });

        // Load children on select and show metrics
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                ApplicationComponentSummary s = newSel.getValue();
                String id = s == null ? "" : OamRestClient.resolveKey(s);
                LOG.info("[UI] Tree selection changed to: {}", id);
                loadChildrenIfNeeded(newSel);
                refreshMetrics(newSel.getValue());
            } else {
                LOG.info("[UI] Tree selection cleared");
                refreshMetrics(null);
            }
        });

        // Metrics panel
        metricsArea = new TextArea();
        metricsArea.setEditable(false);
        metricsArea.setWrapText(true);
        refreshMetricsBtn = new Button("Refresh Metrics");
        refreshMetricsBtn.setOnAction(e -> {
            TreeItem<ApplicationComponentSummary> sel = treeView.getSelectionModel().getSelectedItem();
            refreshMetrics(sel == null ? null : sel.getValue());
        });
        VBox right = new VBox(8, new Label("Metrics (latest)"), refreshMetricsBtn, metricsArea);
        VBox.setVgrow(metricsArea, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(treeView);
        root.setRight(right);

        stage.setTitle("DRICaTS OAM UI");
        stage.setScene(new Scene(root, 1100, 600));
        stage.show();

        loadRoots();
    }

    private void refreshMetrics(ApplicationComponentSummary summary) {
        if (summary == null) {
            metricsArea.setText("No component selected.");
            return;
        }
        String id = OamRestClient.resolveKey(summary);
        ApplicationComponentMetricsData md = client.getLatestMetrics(id);
        if (md == null) {
            metricsArea.setText("No metrics available for: " + Objects.toString(summary.getName(), id));
            return;
        }
        metricsArea.setText(formatMetrics(md));
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
        TreeItem<ApplicationComponentSummary> hiddenRoot = new TreeItem<>();
        treeView.setRoot(hiddenRoot);
        List<ApplicationComponentSummary> roots = client.listComponents().getElementList();
        for (ApplicationComponentSummary s : roots) {
            hiddenRoot.getChildren().add(createTreeItem(s));
        }
    }

    private TreeItem<ApplicationComponentSummary> createTreeItem(ApplicationComponentSummary s) {
        TreeItem<ApplicationComponentSummary> item = new TreeItem<>(s);
        // Add a dummy child to show expandable arrow; real children loaded on demand
        item.getChildren().add(new TreeItem<>());
        item.expandedProperty().addListener((obs, o, n) -> {
            if (n) loadChildrenIfNeeded(item);
        });
        return item;
    }

    private void loadChildrenIfNeeded(TreeItem<ApplicationComponentSummary> parentItem) {
        if (parentItem == null || parentItem.getValue() == null) return;
        // If already loaded (no placeholder), skip
        if (!hasPlaceholder(parentItem)) return;
        parentItem.getChildren().clear();

        String id = OamRestClient.resolveKey(parentItem.getValue());
        List<ApplicationComponentSummary> kids = client.listSubcomponents(id);
        if (kids.isEmpty()) {
            // keep no children
            return;
        }
        for (ApplicationComponentSummary child : kids) {
            parentItem.getChildren().add(createTreeItem(child));
        }
    }

    private boolean hasPlaceholder(TreeItem<ApplicationComponentSummary> item) {
        return item.getChildren().size() == 1 && item.getChildren().get(0).getValue() == null;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
