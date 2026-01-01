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

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.fhirfactory.dricats.internals.common.naming.datatypes.DistinguishedNameEntry;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.datatypes.ComponentMessagingStatistics;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.EgressApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.IngresApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPInterfaceBase;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.ui.restclient.MainRESTClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class TopologyTab extends Tab {
    private static final Logger LOG = LoggerFactory.getLogger(TopologyTab.class);

    // Fallback UI fields (used only if FXML fails to load)
    private MainRESTClient client;
    private TreeView<Object> treeView;
    private TableView<KVRow> metricsTable;
    private TableView<KVRow> detailsTable;
    private TreeView<String> uniqueNameTree;
    private Button refreshMetricsBtn;

    public TopologyTab(String baseUrl) {
        super("Topology");
        setClosable(false);
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/net/fhirfactory/dricats/ui/TopologyTab.fxml")
            );
            javafx.scene.Parent root = loader.load();
            TopologyTabController controller = loader.getController();
            if (controller != null) {
                controller.setBaseUrl(baseUrl);
            }
            setContent(root);
        } catch (Exception e) {
            LOG.error("[UI] Failed to load TopologyTab.fxml, falling back to programmatic UI", e);
            // Fallback to programmatic UI only if needed
            setContent(buildContentFallback(baseUrl));
        }
    }

    private BorderPane buildContentFallback(String baseUrl) {
        // initialize client for fallback mode
        this.client = new MainRESTClient(baseUrl);
        MenuBar menuBar = new MenuBar();
        Menu actionsMenu = new Menu("Actions");
        MenuItem refreshItem = new MenuItem("Refresh");
        refreshItem.setOnAction(e -> {
            LOG.info("[UI] Refresh requested.");
            loadRoots();
            TreeItem<Object> sel = treeView.getSelectionModel().getSelectedItem();
            if (sel != null && sel.getValue() instanceof ApplicationComponent s) {
                refreshDetails(s);
                refreshUniqueName(s);
                refreshMetrics(s);
            } else {
                refreshDetails(null);
                refreshUniqueName(null);
                refreshMetrics(null);
            }
        });
        actionsMenu.getItems().add(refreshItem);
        menuBar.getMenus().add(actionsMenu);

        treeView = new TreeView<>();
        treeView.setShowRoot(false);
        treeView.setCellFactory(tv -> new TreeCell<Object>() {
            @Override
            protected void updateItem(Object value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                if (value instanceof ApplicationComponent acs) {
                    String label = acs.getName();
                    if (label == null || label.isBlank()) {
                        label = MainRESTClient.resolveKey(acs);
                    }
                    setText(label);
                    setStyle("");
                } else if (value instanceof IngresApplicationInterface ifIn) {
                    String label = ifIn.getName();
                    if (label == null || label.isBlank()) {
                        label = ifIn.resolveKey();
                    }
                    setText("[IN] " + label);
                    setStyle("-fx-text-fill: #2a7fff;");
                } else if (value instanceof EgressApplicationInterface ifOut) {
                    String label = ifOut.getName();
                    if (label == null || label.isBlank()) {
                        label = ifOut.resolveKey();
                    }
                    setText("[OUT] " + label);
                    setStyle("-fx-text-fill: #2a7fff;");
                } else {
                    setText(String.valueOf(value));
                    setStyle("");
                }
            }
        });

        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                Object v = newSel.getValue();
                if (v instanceof ApplicationComponent s) {
                    String id = MainRESTClient.resolveKey(s);
                    LOG.info("[UI] Tree selection changed to: {}", id);
                    loadChildrenIfNeeded(newSel);
                    refreshDetails(s);
                    refreshUniqueName(s);
                    refreshMetrics(s);
                } else {
                    LOG.info("[UI] Tree selection is interface or other node");
                }
            } else {
                LOG.info("[UI] Tree selection cleared");
                refreshDetails(null);
                refreshUniqueName(null);
                refreshMetrics(null);
            }
        });

        detailsTable = new TableView<>();
        TableColumn<KVRow, String> dAttrCol = new TableColumn<>("Attribute");
        dAttrCol.setCellValueFactory(cell -> cell.getValue().keyProperty());
        dAttrCol.setPrefWidth(180);
        TableColumn<KVRow, String> dValCol = new TableColumn<>("Value");
        dValCol.setCellValueFactory(cell -> cell.getValue().valueProperty());
        dValCol.setPrefWidth(400);
        detailsTable.getColumns().setAll(dAttrCol, dValCol);
        detailsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        uniqueNameTree = new TreeView<>();
        uniqueNameTree.setShowRoot(true);
        uniqueNameTree.setPrefHeight(200);

        metricsTable = new TableView<>();
        TableColumn<KVRow, String> mAttrCol = new TableColumn<>("Metric");
        mAttrCol.setCellValueFactory(cell -> cell.getValue().keyProperty());
        mAttrCol.setPrefWidth(220);
        TableColumn<KVRow, String> mValCol = new TableColumn<>("Value");
        mValCol.setCellValueFactory(cell -> cell.getValue().valueProperty());
        mValCol.setPrefWidth(400);
        metricsTable.getColumns().setAll(mAttrCol, mValCol);
        metricsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        refreshMetricsBtn = new Button("Refresh Metrics");
        refreshMetricsBtn.setOnAction(e -> {
            TreeItem<Object> sel = treeView.getSelectionModel().getSelectedItem();
            Object v = sel == null ? null : sel.getValue();
            refreshMetrics(v instanceof ApplicationComponent s ? s : null);
        });
        VBox right = new VBox(8,
                new Label("Component Details"),
                detailsTable,
                new Label("UniqueName"),
                uniqueNameTree,
                new Separator(),
                new Label("Metrics (latest)"),
                refreshMetricsBtn,
                metricsTable
        );
        VBox.setVgrow(metricsTable, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(treeView);
        root.setRight(right);
        return root;
    }

    private void refreshDetails(ApplicationComponent applicationComponent) {
        if (detailsTable == null) return;
        javafx.collections.ObservableList<KVRow> rows = javafx.collections.FXCollections.observableArrayList();
        if (applicationComponent == null) {
            rows.add(new KVRow("Info", "No component selected."));
            detailsTable.setItems(rows);
            return;
        }
        try {
            rows.add(new KVRow("Name", Objects.toString(applicationComponent.getName(), "")));
        } catch (Exception ignored) {
        }
        try {
            if (applicationComponent.getObjectId() != null && applicationComponent.getObjectId().getKeyValue() != null)
                rows.add(new KVRow("ID", applicationComponent.getObjectId().getKeyValue()));
        } catch (Exception ignored) {
        }
        try {
            if (applicationComponent.getObjectId() != null)
                rows.add(new KVRow("ObjectID", Objects.toString(applicationComponent.getObjectId().getName().getValue(), "")));
        } catch (Exception ignored) {
        }
        try {
            rows.add(new KVRow("Element Type", Objects.toString(applicationComponent.getElementType(), "")));
        } catch (Exception ignored) {
        }
        try {
            rows.add(new KVRow("Specialization", Objects.toString(applicationComponent.getSpecialization(), "")));
        } catch (Exception ignored) {
        }
        try {
            rows.add(new KVRow("Documentation", Objects.toString(applicationComponent.getDocumentation(), "")));
        } catch (Exception ignored) {
        }
        try {
            if (applicationComponent.getParent() != null)
                rows.add(new KVRow("Parent", Objects.toString(applicationComponent.getParent().getLocalObjectId().getFullyDistinguishedName().getCommonName().getValue(), "")));
        } catch (Exception ignored) {
        }
        try {
            int count = applicationComponent.getSubComponents() == null ? 0 : applicationComponent.getSubComponents().size();
            rows.add(new KVRow("Subcomponents", Integer.toString(count)));
        } catch (Exception ignored) {
        }
        try {
            if (applicationComponent.getComponentStatus() != null) {
                rows.add(new KVRow("Status", Objects.toString(applicationComponent.getComponentStatus().getComponentStatus(), "")));
                String desc = applicationComponent.getComponentStatus().getComponentStatusDescription();
                if (desc != null && !desc.isBlank()) rows.add(new KVRow("Status Description", desc));
                if (applicationComponent.getComponentStatus().getStartupInstant() != null)
                    rows.add(new KVRow("Startup", Objects.toString(applicationComponent.getComponentStatus().getStartupInstant(), "")));
                if (applicationComponent.getComponentStatus().getLastActivityInstant() != null)
                    rows.add(new KVRow("Last Activity", Objects.toString(applicationComponent.getComponentStatus().getLastActivityInstant(), "")));
                if (applicationComponent.getComponentStatus().getLastHeartbeatInstant() != null)
                    rows.add(new KVRow("Last Heartbeat", Objects.toString(applicationComponent.getComponentStatus().getLastHeartbeatInstant(), "")));
            }
        } catch (Exception ignored) {
        }
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

    private void refreshMetrics(ApplicationComponent applicationComponent) {
        if (metricsTable == null) return;
        javafx.collections.ObservableList<KVRow> rows = javafx.collections.FXCollections.observableArrayList();
        if (applicationComponent == null) {
            rows.add(new KVRow("Info", "No component selected."));
            metricsTable.setItems(rows);
            return;
        }
        String id = applicationComponent.resolveKey();
        ApplicationComponentMetricsData md = client.getLatestMetrics(id);
        if (md == null) {
            rows.add(new KVRow("Info", "No metrics available for: " + Objects.toString(applicationComponent.getName(), id)));
            metricsTable.setItems(rows);
            return;
        }
        try {
            rows.add(new KVRow("Component", Objects.toString(md.getParticipantName(), "")));
        } catch (Exception ignored) {
        }
        try {
            if (md.getComponentType() != null) rows.add(new KVRow("Type", Objects.toString(md.getComponentType(), "")));
        } catch (Exception ignored) {
        }
        try {
            if (md.getComponentStartupInstant() != null)
                rows.add(new KVRow("Startup", Objects.toString(md.getComponentStartupInstant(), "")));
        } catch (Exception ignored) {
        }
        try {
            if (md.getLastActivityInstant() != null)
                rows.add(new KVRow("Last Activity", Objects.toString(md.getLastActivityInstant(), "")));
        } catch (Exception ignored) {
        }
        try {
            if (md.getComponentStatus() != null)
                rows.add(new KVRow("Status", Objects.toString(md.getComponentStatus(), "")));
        } catch (Exception ignored) {
        }
        ComponentMessagingStatistics ms = md.getMessagingStatistics();
        if (ms != null) {
            rows.add(new KVRow("Messaging", ""));
            try {
                rows.add(new KVRow("Ingress", Objects.toString(ms.getIngresMessageCount(), "0")));
            } catch (Exception ignored) {
            }
            try {
                rows.add(new KVRow("Egress Attempts", Objects.toString(ms.getEgressMessageAttemptCount(), "0")));
            } catch (Exception ignored) {
            }
            try {
                rows.add(new KVRow("Egress Success", Objects.toString(ms.getEgressMessageSuccessCount(), "0")));
            } catch (Exception ignored) {
            }
            try {
                rows.add(new KVRow("Egress Failures", Objects.toString(ms.getEgressMessageFailureCount(), "0")));
            } catch (Exception ignored) {
            }
            try {
                rows.add(new KVRow("Internal Sent", Objects.toString(ms.getInternalDistributedMessageCount(), "0")));
            } catch (Exception ignored) {
            }
            try {
                rows.add(new KVRow("Internal Received", Objects.toString(ms.getInternalReceivedMessageCount(), "0")));
            } catch (Exception ignored) {
            }
        }
        metricsTable.setItems(rows);
    }

    private void loadRoots() {
        TreeItem<Object> hiddenRoot = new TreeItem<>();
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

    private TreeItem<Object> createTreeItem(ApplicationComponent s) {
        TreeItem<Object> item = new TreeItem<>(s);
        item.getChildren().add(new TreeItem<>());
        item.expandedProperty().addListener((obs, o, n) -> {
            if (n) loadChildrenIfNeeded(item);
        });
        return item;
    }

    private void loadChildrenIfNeeded(TreeItem<Object> parentItem) {
        if (parentItem == null || !(parentItem.getValue() instanceof ApplicationComponent)) return;
        if (!hasPlaceholder(parentItem)) return;
        parentItem.getChildren().clear();

        ApplicationComponent s = (ApplicationComponent) parentItem.getValue();
        String id = MainRESTClient.resolveKey(s);
        List<ApplicationComponent> kids = client.listSubcomponents(id);
        for (ApplicationComponent child : kids) {
            if (child != null) parentItem.getChildren().add(createTreeItem(child));
        }
        // add interface nodes
        try {
            List<? extends WUPInterfaceBase> ifaces = client.listInterfaces(id);
            if (ifaces != null) {
                for (Object iface : ifaces) {
                    if (iface != null && iface.getClass().isArray()) {
                        int len = java.lang.reflect.Array.getLength(iface);
                        for (int i = 0; i < len; i++) {
                            Object elem = java.lang.reflect.Array.get(iface, i);
                            if (elem instanceof IngresApplicationInterface ||
                                    elem instanceof EgressApplicationInterface) {
                                parentItem.getChildren().add(new TreeItem<>(elem));
                            }
                        }
                    } else if (iface instanceof IngresApplicationInterface ||
                            iface instanceof EgressApplicationInterface) {
                        parentItem.getChildren().add(new TreeItem<>(iface));
                    }
                }
            }
        } catch (Exception e) {
            LOG.warn("[UI] Failed to fetch interfaces for {}: {}", id, e.toString());
        }
    }

    private boolean hasPlaceholder(TreeItem<Object> item) {
        return item.getChildren().size() == 1 && item.getChildren().get(0).getValue() == null;
    }
}
