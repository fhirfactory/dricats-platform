package net.fhirfactory.dricats.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.datatypes.ComponentMessagingStatistics;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import net.fhirfactory.dricats.ui.restclient.MainRESTClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class TopologyTabController {
    private static final Logger LOG = LoggerFactory.getLogger(TopologyTabController.class);

    // Injected UI controls
    @FXML private TreeView<Object> treeView;
    @FXML private TableView<KVRow> metricsTable;
    @FXML private TableView<KVRow> detailsTable;
    @FXML private TreeView<String> uniqueNameTree;
    @FXML private Button refreshMetricsBtn;
    @FXML private MenuItem refreshMenuItem;

    @FXML private TableColumn<KVRow, String> detailsAttrCol;
    @FXML private TableColumn<KVRow, String> detailsValCol;
    @FXML private TableColumn<KVRow, String> metricsAttrCol;
    @FXML private TableColumn<KVRow, String> metricsValCol;

    private MainRESTClient client;
    private String baseUrl;

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        if (this.baseUrl == null || this.baseUrl.isBlank()) {
            this.baseUrl = "http://localhost:12000";
        }
        this.client = new MainRESTClient(this.baseUrl);
        // initial load
        loadRoots();
    }

    @FXML
    private void initialize() {
        // Tree cell factory for ApplicationComponentSummary
        if (treeView != null) {
            treeView.setShowRoot(false);
            treeView.setCellFactory(tv -> new TreeCell<Object>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else if (item instanceof net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary acs) {
                        String label = acs.getName();
                        if (label == null || label.isBlank()) {
                            label = net.fhirfactory.dricats.ui.restclient.MainRESTClient.resolveKey(acs);
                        }
                        setText(label);
                        setStyle("");
                    } else if (item instanceof net.fhirfactory.dricats.internals.oam.topology.InterfaceComponentSummary ifs) {
                        String label = ifs.getName();
                        if (label == null || label.isBlank()) {
                            label = ifs.resolveKey();
                        }
                        setText(label);
                        setStyle("-fx-text-fill: #2a7fff;");
                    } else {
                        setText(String.valueOf(item));
                        setStyle("");
                    }
                }
            });

            treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                if (newSel != null) {
                    Object v = newSel.getValue();
                    if (v instanceof net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary s) {
                        String id = net.fhirfactory.dricats.ui.restclient.MainRESTClient.resolveKey(s);
                        LOG.info("[UI] Tree selection changed to: {}", id);
                        loadChildrenIfNeeded(newSel);
                        refreshDetails(s);
                        refreshUniqueName(s);
                        refreshMetrics(s);
                    } else if (v instanceof net.fhirfactory.dricats.internals.oam.topology.InterfaceComponentSummary ifs) {
                        // Do not open dialog on selection. Only on double-click (handled by mouse handler).
                        LOG.info("[UI] Interface selected: {}", ifs.resolveKey());
                    } else {
                        LOG.info("[UI] Tree selection is other node");
                    }
                } else {
                    LOG.info("[UI] Tree selection cleared");
                    refreshDetails(null);
                    refreshUniqueName(null);
                    refreshMetrics(null);
                }
            });

            // Open Interface detail dialog only on double-click
            treeView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    TreeItem<Object> sel = treeView.getSelectionModel().getSelectedItem();
                    if (sel != null) {
                        Object v = sel.getValue();
                        if (v instanceof net.fhirfactory.dricats.internals.oam.topology.InterfaceComponentSummary ifs) {
                            LOG.info("[UI] Interface double-clicked: {}", ifs.resolveKey());
                            try {
                                InterfaceDetailDialog dlg = new InterfaceDetailDialog(client, ifs);
                                dlg.initOwner(treeView.getScene()==null? null : treeView.getScene().getWindow());
                                dlg.showAndWait();
                            } catch (Exception ex) {
                                LOG.warn("[UI] Failed to open PathwayElement dialog: {}", ex.toString());
                            }
                        }
                    }
                }
            });
        }

        // Configure details table columns
        if (detailsTable != null) {
            if (detailsAttrCol != null) {
                detailsAttrCol.setCellValueFactory(cell -> cell.getValue().keyProperty());
                detailsAttrCol.setPrefWidth(180);
            }
            if (detailsValCol != null) {
                detailsValCol.setCellValueFactory(cell -> cell.getValue().valueProperty());
                detailsValCol.setPrefWidth(400);
            }
            detailsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        }

        // Unique name tree
        if (uniqueNameTree != null) {
            uniqueNameTree.setShowRoot(true);
            uniqueNameTree.setPrefHeight(120);
        }

        // Configure metrics table columns
        if (metricsTable != null) {
            if (metricsAttrCol != null) {
                metricsAttrCol.setCellValueFactory(cell -> cell.getValue().keyProperty());
                metricsAttrCol.setPrefWidth(220);
            }
            if (metricsValCol != null) {
                metricsValCol.setCellValueFactory(cell -> cell.getValue().valueProperty());
                metricsValCol.setPrefWidth(400);
            }
            metricsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        }

        // Buttons and menu actions
        if (refreshMetricsBtn != null) {
            refreshMetricsBtn.setOnAction(e -> {
                TreeItem<Object> sel = treeView.getSelectionModel().getSelectedItem();
                Object v = sel==null? null : sel.getValue();
                refreshMetrics(v instanceof net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary s ? s : null);
            });
        }
        if (refreshMenuItem != null) {
            refreshMenuItem.setOnAction(e -> {
                LOG.info("[UI] Refresh requested.");
                loadRoots();
                TreeItem<Object> sel = treeView.getSelectionModel().getSelectedItem();
                if (sel != null && sel.getValue() instanceof net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary s) {
                    refreshDetails(s);
                    refreshUniqueName(s);
                    refreshMetrics(s);
                } else {
                    refreshDetails(null);
                    refreshUniqueName(null);
                    refreshMetrics(null);
                }
            });
        }
    }

    private void refreshDetails(ApplicationComponentSummary summary) {
        if (detailsTable == null) return;
        javafx.collections.ObservableList<KVRow> rows = javafx.collections.FXCollections.observableArrayList();
        if (summary == null) {
            rows.add(new KVRow("Info", "No component selected."));
            detailsTable.setItems(rows);
            return;
        }
        try { rows.add(new KVRow("Name", Objects.toString(summary.getName(), ""))); } catch (Exception ignored) {}
        try {
            if (summary.getId() != null && summary.getId().getValue() != null)
                rows.add(new KVRow("ID", summary.getId().getValue()));
        } catch (Exception ignored) {}
        try {
            if (summary.getObjectID() != null)
                rows.add(new KVRow("ObjectID", Objects.toString(summary.getObjectID().getIdToken(), "")));
        } catch (Exception ignored) {}
        try { rows.add(new KVRow("Element Type", Objects.toString(summary.getElementType(), ""))); } catch (Exception ignored) {}
        try { rows.add(new KVRow("Specialization", Objects.toString(summary.getSpecialization(), ""))); } catch (Exception ignored) {}
        try { rows.add(new KVRow("Documentation", Objects.toString(summary.getDocumentation(), ""))); } catch (Exception ignored) {}
        try {
            if (summary.getParent() != null)
                rows.add(new KVRow("Parent", Objects.toString(summary.getParent().getQualifiedName().getCommonName().getValue(), "")));
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

    private void refreshUniqueName(ApplicationComponentSummary summary) {
        if (uniqueNameTree == null) return;
        TreeItem<String> root = new TreeItem<>("UniqueName");
        root.setExpanded(true);
        try {
            if (summary == null || summary.getObjectID() == null || summary.getObjectID().getQualifiedName() == null) {
                root.getChildren().add(new TreeItem<>("No selection"));
            } else {
                java.util.Map<Integer, net.fhirfactory.dricats.internals.common.naming.UnqualifiedNameEntry> entries =
                        summary.getObjectID().getQualifiedName().getUnqualifiedNameEntries();
                if (entries == null || entries.isEmpty()) {
                    root.getChildren().add(new TreeItem<>("<empty>"));
                } else {
                    java.util.List<Integer> keys = new java.util.ArrayList<>(entries.keySet());
                    java.util.Collections.sort(keys);
                    for (Integer k : keys) {
                        net.fhirfactory.dricats.internals.common.naming.UnqualifiedNameEntry e = entries.get(k);
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

    private void refreshMetrics(ApplicationComponentSummary summary) {
        if (metricsTable == null) return;
        javafx.collections.ObservableList<KVRow> rows = javafx.collections.FXCollections.observableArrayList();
        if (summary == null) {
            rows.add(new KVRow("Info", "No component selected."));
            metricsTable.setItems(rows);
            return;
        }
        String id = MainRESTClient.resolveKey(summary);
        ApplicationComponentMetricsData md = client == null ? null : client.getLatestMetrics(id);
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

    private void loadRoots() {
        if (treeView == null) return;
        TreeItem<Object> hiddenRoot = new TreeItem<>();
        treeView.setRoot(hiddenRoot);
        List<ApplicationComponentSummary> roots = null;
        try {
            roots = client == null ? null : client.listComponents().getElementList();
        } catch (Exception e) {
            LOG.warn("[UI] Failed to fetch component list: {}", e.toString());
        }
        if (roots == null) {
            LOG.info("[UI] No components returned (null list)");
            return;
        }
        LOG.info("[UI] Loaded {} components", roots.size());
        hiddenRoot.getChildren().clear();
        for (ApplicationComponentSummary s : roots) {
            if (s != null) {
                hiddenRoot.getChildren().add(createTreeItem(s));
            }
        }
    }

    private TreeItem<Object> createTreeItem(ApplicationComponentSummary s) {
        TreeItem<Object> item = new TreeItem<>(s);
        // placeholder child to show expansion arrow
        item.getChildren().add(new TreeItem<>());
        item.expandedProperty().addListener((obs, was, is) -> {
            if (is) loadChildrenIfNeeded(item);
        });
        return item;
    }

    private void loadChildrenIfNeeded(TreeItem<Object> parentItem) {
        if (parentItem == null || !(parentItem.getValue() instanceof ApplicationComponentSummary)) return;
        if (!hasPlaceholder(parentItem)) return;
        parentItem.getChildren().clear();
        ApplicationComponentSummary s = (ApplicationComponentSummary) parentItem.getValue();
        String id = MainRESTClient.resolveKey(s);
        List<ApplicationComponentSummary> kids = null;
        try {
            kids = client == null ? null : client.listSubcomponents(id);
        } catch (Exception e) {
            LOG.warn("[UI] Failed to fetch subcomponents for {}: {}", id, e.toString());
        }
        if (kids != null) {
            for (ApplicationComponentSummary k : kids) {
                if (k != null) parentItem.getChildren().add(createTreeItem(k));
            }
        }
        // also add interfaces
        try {
            java.util.List<net.fhirfactory.dricats.internals.oam.topology.InterfaceComponentSummary> ifaces = client == null ? null : client.listInterfaces(id);
            if (ifaces != null) {
                for (net.fhirfactory.dricats.internals.oam.topology.InterfaceComponentSummary iface : ifaces) {
                    if (iface != null) parentItem.getChildren().add(new TreeItem<>(iface));
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
