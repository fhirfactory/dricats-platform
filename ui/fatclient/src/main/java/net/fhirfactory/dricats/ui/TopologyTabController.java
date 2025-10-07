package net.fhirfactory.dricats.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.datatypes.ComponentMessagingStatistics;
import net.fhirfactory.dricats.internals.oam.topology.base.ApplicationComponentSummary;
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

    // New interface details tables
    @FXML private TableView<KVRow> ingressInterfaceTable;
    @FXML private TableColumn<KVRow, String> ingressAttrCol;
    @FXML private TableColumn<KVRow, String> ingressValCol;

    @FXML private TableView<KVRow> egressInterfaceTable;
    @FXML private TableColumn<KVRow, String> egressAttrCol;
    @FXML private TableColumn<KVRow, String> egressValCol;

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
                    } else if (item instanceof ApplicationComponentSummary) {
                        ApplicationComponentSummary acs = (ApplicationComponentSummary) item;
                        String label = acs.getName();
                        if (label == null || label.isBlank()) {
                            label = net.fhirfactory.dricats.ui.restclient.MainRESTClient.resolveKey(acs);
                        }
                        setText(label);
                        setStyle("");
                    } else if (item instanceof net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary) {
                        net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary ifs = (net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary) item;
                        String label = ifs.getName();
                        if (label == null || label.isBlank()) {
                            label = ifs.resolveKey();
                        }
                        setText("[IN] " + label);
                        setStyle("-fx-text-fill: #2a7fff;");
                    } else if (item instanceof net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary) {
                        net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary ifs = (net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary) item;
                        String label = ifs.getName();
                        if (label == null || label.isBlank()) {
                            label = ifs.resolveKey();
                        }
                        setText("[OUT] " + label);
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
                    if (v instanceof ApplicationComponentSummary s) {
                        String id = net.fhirfactory.dricats.ui.restclient.MainRESTClient.resolveKey(s);
                        LOG.info("[UI] Tree selection changed to: {}", id);
                        loadChildrenIfNeeded(newSel);
                        refreshDetails(s);
                        refreshUniqueName(s);
                        refreshMetrics(s);
                        refreshInterfaceDetails(s);
                    } else if (v instanceof net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary ifIn) {
                        LOG.info("[UI] Ingress Interface selected: {}", ifIn.resolveKey());
                    } else if (v instanceof net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary ifOut) {
                        LOG.info("[UI] Egress Interface selected: {}", ifOut.resolveKey());
                    } else {
                        LOG.info("[UI] Tree selection is other node");
                    }
                } else {
                    LOG.info("[UI] Tree selection cleared");
                    refreshDetails(null);
                    refreshUniqueName(null);
                    refreshMetrics(null);
                    refreshInterfaceDetails(null);
                }
            });

            // Double-click handler to show ContentSubscriptions/Filters for interfaces
            treeView.setOnMouseClicked((MouseEvent event) -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    TreeItem<Object> sel = treeView.getSelectionModel().getSelectedItem();
                    if (sel != null) {
                        Object val = sel.getValue();
                        if (val instanceof net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary ingress) {
                            showIngressSubscriptionsDialog(ingress);
                            event.consume();
                        } else if (val instanceof net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary egress) {
                            showEgressSubscriptionsDialog(egress);
                            event.consume();
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

        // Configure interfaces tables
        if (ingressInterfaceTable != null) {
            if (ingressAttrCol != null) ingressAttrCol.setCellValueFactory(cell -> cell.getValue().keyProperty());
            if (ingressValCol != null) ingressValCol.setCellValueFactory(cell -> cell.getValue().valueProperty());
            ingressInterfaceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        }
        if (egressInterfaceTable != null) {
            if (egressAttrCol != null) egressAttrCol.setCellValueFactory(cell -> cell.getValue().keyProperty());
            if (egressValCol != null) egressValCol.setCellValueFactory(cell -> cell.getValue().valueProperty());
            egressInterfaceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        }

        // Buttons and menu actions
        if (refreshMetricsBtn != null) {
            refreshMetricsBtn.setOnAction(e -> {
                TreeItem<Object> sel = treeView.getSelectionModel().getSelectedItem();
                Object v = sel==null? null : sel.getValue();
                refreshMetrics(v instanceof ApplicationComponentSummary s ? s : null);
            });
        }
        if (refreshMenuItem != null) {
            refreshMenuItem.setOnAction(e -> {
                LOG.info("[UI] Refresh requested.");
                loadRoots();
                TreeItem<Object> sel = treeView.getSelectionModel().getSelectedItem();
                if (sel != null && sel.getValue() instanceof ApplicationComponentSummary s) {
                    refreshDetails(s);
                    refreshUniqueName(s);
                    refreshMetrics(s);
                    refreshInterfaceDetails(s);
                } else {
                    refreshDetails(null);
                    refreshUniqueName(null);
                    refreshMetrics(null);
                    refreshInterfaceDetails(null);
                }
            });
        }
    }
    
    private void showIngressSubscriptionsDialog(net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary ingress) {
        String name = null;
        try { name = ingress.getName(); } catch (Exception ignored) {}
        if (name == null || name.isBlank()) {
            try { name = ingress.resolveKey(); } catch (Exception ignored) {}
        }
        String title = "Supported Content Subscriptions";
        String header = (name == null || name.isBlank()) ? "Ingress Interface" : name;
        java.util.List<net.fhirfactory.dricats.internals.pubsub.content.ContentSubscription> subs = null;
        try { subs = ingress.getSubscriptionFilters(); } catch (Exception ignored) {}

        StringBuilder sb = new StringBuilder();
        if (subs == null || subs.isEmpty()) {
            sb.append("No ContentSubscriptions defined for this ingress interface.");
        } else {
            int i = 1;
            for (net.fhirfactory.dricats.internals.pubsub.content.ContentSubscription cs : subs) {
                if (cs == null) { continue; }
                sb.append("#").append(i++).append("\n");
                try { sb.append("  Source: ").append(java.util.Objects.toString(cs.getInternalEventSource().getComponentIdMask().prettyPrint(), "")).append("\n"); } catch (Exception ignored) {}
                try { sb.append("  Target: ").append(java.util.Objects.toString(cs.getInternalEventTarget().getComponentIdMask().prettyPrint(), "")).append("\n"); } catch (Exception ignored) {}
                try { 
                    java.util.List<net.fhirfactory.dricats.internals.pubsub.TopicSubscription> topics = cs.getEventTopicSubscriptions();
                    if (topics != null && !topics.isEmpty()) {
                        sb.append("  Topics:");
                        for (net.fhirfactory.dricats.internals.pubsub.TopicSubscription t : topics) {
                            sb.append("\n    - ").append(t.prettyPrint());
                        }
                        sb.append("\n");
                    }
                } catch (Exception ignored) {}
                try { sb.append("  Origin: ").append(java.util.Objects.toString(cs.getEventOrigin(), "")).append("\n"); } catch (Exception ignored) {}
                try { sb.append("  Final Destination: ").append(java.util.Objects.toString(cs.getEventFinalDestination(), "")).append("\n"); } catch (Exception ignored) {}
                try { sb.append("  Temporal Window: ").append(java.util.Objects.toString(cs.getTemporalWindow().prettyPrint(), "")).append("\n"); } catch (Exception ignored) {}
                sb.append("\n");
            }
        }

        Alert dlg = new Alert(Alert.AlertType.INFORMATION);
        dlg.setTitle(title);
        dlg.setHeaderText(header);
        // Use a resizable dialog and let it size to content
        javafx.scene.control.TextArea ta = new javafx.scene.control.TextArea(sb.toString());
        ta.setWrapText(true);
        ta.setEditable(false);
        dlg.getDialogPane().setContent(ta);
        dlg.setResizable(true);
        dlg.getDialogPane().setPrefSize(javafx.scene.layout.Region.USE_COMPUTED_SIZE, javafx.scene.layout.Region.USE_COMPUTED_SIZE);
        dlg.setOnShown(e -> {
            javafx.stage.Window w = dlg.getDialogPane().getScene().getWindow();
            if (w instanceof javafx.stage.Stage st) {
                st.sizeToScene();
            }
        });
        dlg.showAndWait();
    }

    private void showEgressSubscriptionsDialog(net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary egress) {
        String name = null;
        try { name = egress.getName(); } catch (Exception ignored) {}
        if (name == null || name.isBlank()) {
            try { name = egress.resolveKey(); } catch (Exception ignored) {}
        }
        String title = "Supported Content Filters";
        String header = (name == null || name.isBlank()) ? "Egress Interface" : name;
        java.util.List<net.fhirfactory.dricats.internals.pubsub.content.ContentFilter> subs = null;
        try { subs = egress.getSubscriptionFilters(); } catch (Exception ignored) {}

        // Build table rows
        javafx.collections.ObservableList<ContentFilterRow> rows = javafx.collections.FXCollections.observableArrayList();
        if (subs != null) {
            int idx = 1;
            for (net.fhirfactory.dricats.internals.pubsub.content.ContentFilter cf : subs) {
                if (cf == null) { continue; }
                String source = ""; try { source = java.util.Objects.toString(cf.getInternalEventSource().getComponentIdMask().prettyPrint(), ""); } catch (Exception ignored) {}
                String target = ""; try { target = java.util.Objects.toString(cf.getInternalEventTarget().getComponentIdMask().prettyPrint(), ""); } catch (Exception ignored) {}
                String topicsStr = "";
                try {
                    java.util.List<net.fhirfactory.dricats.internals.pubsub.TopicSubscription> topics = cf.getEventTopicSubscriptions();
                    if (topics != null && !topics.isEmpty()) {
                        java.util.List<String> topicTexts = new java.util.ArrayList<>();
                        for (net.fhirfactory.dricats.internals.pubsub.TopicSubscription t : topics) {
                            if (t != null) topicTexts.add(t.prettyPrint());
                        }
                        topicsStr = String.join("; ", topicTexts);
                    }
                } catch (Exception ignored) {}
                String origin = ""; try { origin = java.util.Objects.toString(cf.getEventOrigin(), ""); } catch (Exception ignored) {}
                String finalDest = ""; try { finalDest = java.util.Objects.toString(cf.getEventFinalDestination(), ""); } catch (Exception ignored) {}
                String temporal = ""; try { temporal = java.util.Objects.toString(cf.getTemporalWindow().prettyPrint(), ""); } catch (Exception ignored) {}
                String mediaTypes = "";
                try {
                    java.util.List<jakarta.ws.rs.core.MediaType> mts = cf.getSupportedMediaTypes();
                    if (mts != null && !mts.isEmpty()) {
                        java.util.List<String> mtTexts = new java.util.ArrayList<>();
                        for (jakarta.ws.rs.core.MediaType mt : mts) {
                            if (mt != null) mtTexts.add(java.util.Objects.toString(mt, ""));
                        }
                        mediaTypes = String.join("; ", mtTexts);
                    }
                } catch (Exception ignored) {}
                rows.add(new ContentFilterRow(Integer.toString(idx++), source, target, topicsStr, origin, finalDest, temporal, mediaTypes));
            }
        }

        // Create table view
        TableView<ContentFilterRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("No ContentFilters defined for this egress interface."));

        TableColumn<ContentFilterRow, String> colIdx = new TableColumn<>("#");
        colIdx.setPrefWidth(50);
        colIdx.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("index"));

        TableColumn<ContentFilterRow, String> colSource = new TableColumn<>("Source");
        colSource.setPrefWidth(140);
        colSource.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("source"));

        TableColumn<ContentFilterRow, String> colTarget = new TableColumn<>("Target");
        colTarget.setPrefWidth(140);
        colTarget.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("target"));

        TableColumn<ContentFilterRow, String> colTopics = new TableColumn<>("Topics");
        colTopics.setPrefWidth(220);
        colTopics.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("topics"));

        TableColumn<ContentFilterRow, String> colOrigin = new TableColumn<>("Origin");
        colOrigin.setPrefWidth(140);
        colOrigin.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("origin"));

        TableColumn<ContentFilterRow, String> colFinal = new TableColumn<>("Final Dest");
        colFinal.setPrefWidth(140);
        colFinal.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("finalDestination"));

        TableColumn<ContentFilterRow, String> colTemporal = new TableColumn<>("Temporal Window");
        colTemporal.setPrefWidth(160);
        colTemporal.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("temporalWindow"));

        TableColumn<ContentFilterRow, String> colMT = new TableColumn<>("Media Types");
        colMT.setPrefWidth(160);
        colMT.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("mediaTypes"));

        table.getColumns().addAll(colIdx, colSource, colTarget, colTopics, colOrigin, colFinal, colTemporal, colMT);
        table.setItems(rows);

        Alert dlg = new Alert(Alert.AlertType.INFORMATION);
        dlg.setTitle(title);
        dlg.setHeaderText(header);
        dlg.getDialogPane().setContent(table);
        dlg.setResizable(true);
        dlg.getDialogPane().setPrefSize(javafx.scene.layout.Region.USE_COMPUTED_SIZE, javafx.scene.layout.Region.USE_COMPUTED_SIZE);
        dlg.setOnShown(e -> {
            javafx.stage.Window w = dlg.getDialogPane().getScene().getWindow();
            if (w instanceof javafx.stage.Stage st) {
                st.sizeToScene();
            }
        });
        dlg.showAndWait();
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
            java.util.List<? extends net.fhirfactory.dricats.internals.oam.topology.base.InterfaceComponentSummary> ifaces = client == null ? null : client.listInterfaces(id);
            if (ifaces != null) {
                for (Object iface : ifaces) {
                    // Some backends may wrap results in arrays; handle both single and array values
                    if (iface != null && iface.getClass().isArray()) {
                        int len = java.lang.reflect.Array.getLength(iface);
                        for (int i = 0; i < len; i++) {
                            Object elem = java.lang.reflect.Array.get(iface, i);
                            if (elem instanceof net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary ||
                                elem instanceof net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary) {
                                parentItem.getChildren().add(new TreeItem<>(elem));
                            }
                        }
                    } else if (iface instanceof net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary ||
                               iface instanceof net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary) {
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

    private void refreshInterfaceDetails(ApplicationComponentSummary summary) {
        // Prepare empty observable lists
        javafx.collections.ObservableList<KVRow> ingressRows = javafx.collections.FXCollections.observableArrayList();
        javafx.collections.ObservableList<KVRow> egressRows = javafx.collections.FXCollections.observableArrayList();
        if (summary == null) {
            ingressRows.add(new KVRow("Info", "No component selected."));
            egressRows.add(new KVRow("Info", "No component selected."));
            if (ingressInterfaceTable != null) ingressInterfaceTable.setItems(ingressRows);
            if (egressInterfaceTable != null) egressInterfaceTable.setItems(egressRows);
            return;
        }
        String id = net.fhirfactory.dricats.ui.restclient.MainRESTClient.resolveKey(summary);
        java.util.List<? extends net.fhirfactory.dricats.internals.oam.topology.base.InterfaceComponentSummary> interfaces = java.util.Collections.emptyList();
        try {
            interfaces = client == null ? java.util.Collections.emptyList() : client.listInterfaces(id);
        } catch (Exception e) {
            LOG.warn("[UI] Failed to fetch interfaces for {}: {}", id, e.toString());
        }
        java.util.List<net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary> ingressList = new java.util.ArrayList<>();
        java.util.List<net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary> egressList = new java.util.ArrayList<>();
        if (interfaces != null) {
            for (Object o : interfaces) {
                if (o == null) continue;
                if (o.getClass().isArray()) {
                    int len = java.lang.reflect.Array.getLength(o);
                    for (int i = 0; i < len; i++) {
                        Object elem = java.lang.reflect.Array.get(o, i);
                        if (elem instanceof net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary) {
                            ingressList.add((net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary) elem);
                        } else if (elem instanceof net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary) {
                            egressList.add((net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary) elem);
                        }
                    }
                } else if (o instanceof net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary) {
                    ingressList.add((net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary) o);
                } else if (o instanceof net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary) {
                    egressList.add((net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary) o);
                }
            }
        }
        // Populate ingress details (support multiple)
        if (ingressList.isEmpty()) {
            ingressRows.add(new KVRow("Info", "No Ingress Interface present"));
        } else {
            int idx = 1;
            for (net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary ingress : ingressList) {
                // Section header
                String header = ingress.getName();
                if (header == null || header.isBlank()) header = ingress.resolveKey();
                ingressRows.add(new KVRow("Ingress #" + idx, header));
                try { if (ingress.getId() != null && ingress.getId().getValue() != null) ingressRows.add(new KVRow("ID", ingress.getId().getValue())); } catch (Exception ignored) {}
                try { if (ingress.getObjectID() != null) ingressRows.add(new KVRow("ObjectID", java.util.Objects.toString(ingress.getObjectID().getIdToken(), ""))); } catch (Exception ignored) {}
                try { ingressRows.add(new KVRow("Element Type", java.util.Objects.toString(ingress.getElementType(), ""))); } catch (Exception ignored) {}
                try { ingressRows.add(new KVRow("Specialization", java.util.Objects.toString(ingress.getSpecialization(), ""))); } catch (Exception ignored) {}
                try { ingressRows.add(new KVRow("Documentation", java.util.Objects.toString(ingress.getDocumentation(), ""))); } catch (Exception ignored) {}
                try { if (ingress.getParent() != null) ingressRows.add(new KVRow("Parent", java.util.Objects.toString(ingress.getParent().getQualifiedName().getCommonName().getValue(), ""))); } catch (Exception ignored) {}
                try {
                    if (ingress.getComponentStatus() != null) {
                        ingressRows.add(new KVRow("Status", java.util.Objects.toString(ingress.getComponentStatus().getComponentStatus(), "")));
                        if (ingress.getComponentStatus().getStartupInstant() != null)
                            ingressRows.add(new KVRow("Startup", java.util.Objects.toString(ingress.getComponentStatus().getStartupInstant(), "")));
                        if (ingress.getComponentStatus().getLastActivityInstant() != null)
                            ingressRows.add(new KVRow("Last Activity", java.util.Objects.toString(ingress.getComponentStatus().getLastActivityInstant(), "")));
                        if (ingress.getComponentStatus().getLastHeartbeatInstant() != null)
                            ingressRows.add(new KVRow("Last Heartbeat", java.util.Objects.toString(ingress.getComponentStatus().getLastHeartbeatInstant(), "")));
                    }
                } catch (Exception ignored) {}
                idx++;
            }
        }
        // Populate egress details (support multiple)
        if (egressList.isEmpty()) {
            egressRows.add(new KVRow("Info", "No Egress Interface present"));
        } else {
            int idx = 1;
            for (net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary egress : egressList) {
                String header = egress.getName();
                if (header == null || header.isBlank()) header = egress.resolveKey();
                egressRows.add(new KVRow("Egress #" + idx, header));
                try { if (egress.getId() != null && egress.getId().getValue() != null) egressRows.add(new KVRow("ID", egress.getId().getValue())); } catch (Exception ignored) {}
                try { if (egress.getObjectID() != null) egressRows.add(new KVRow("ObjectID", java.util.Objects.toString(egress.getObjectID().getIdToken(), ""))); } catch (Exception ignored) {}
                try { egressRows.add(new KVRow("Element Type", java.util.Objects.toString(egress.getElementType(), ""))); } catch (Exception ignored) {}
                try { egressRows.add(new KVRow("Specialization", java.util.Objects.toString(egress.getSpecialization(), ""))); } catch (Exception ignored) {}
                try { egressRows.add(new KVRow("Documentation", java.util.Objects.toString(egress.getDocumentation(), ""))); } catch (Exception ignored) {}
                try { if (egress.getParent() != null) egressRows.add(new KVRow("Parent", java.util.Objects.toString(egress.getParent().getQualifiedName().getCommonName().getValue(), ""))); } catch (Exception ignored) {}
                try {
                    if (egress.getComponentStatus() != null) {
                        egressRows.add(new KVRow("Status", java.util.Objects.toString(egress.getComponentStatus().getComponentStatus(), "")));
                        if (egress.getComponentStatus().getStartupInstant() != null)
                            egressRows.add(new KVRow("Startup", java.util.Objects.toString(egress.getComponentStatus().getStartupInstant(), "")));
                        if (egress.getComponentStatus().getLastActivityInstant() != null)
                            egressRows.add(new KVRow("Last Activity", java.util.Objects.toString(egress.getComponentStatus().getLastActivityInstant(), "")));
                        if (egress.getComponentStatus().getLastHeartbeatInstant() != null)
                            egressRows.add(new KVRow("Last Heartbeat", java.util.Objects.toString(egress.getComponentStatus().getLastHeartbeatInstant(), "")));
                    }
                } catch (Exception ignored) {}
                idx++;
            }
        }
        if (ingressInterfaceTable != null) ingressInterfaceTable.setItems(ingressRows);
        if (egressInterfaceTable != null) egressInterfaceTable.setItems(egressRows);
    }

    // Row model for Supported Content Filters dialog TableView
    public static class ContentFilterRow {
        private final javafx.beans.property.SimpleStringProperty index;
        private final javafx.beans.property.SimpleStringProperty source;
        private final javafx.beans.property.SimpleStringProperty target;
        private final javafx.beans.property.SimpleStringProperty topics;
        private final javafx.beans.property.SimpleStringProperty origin;
        private final javafx.beans.property.SimpleStringProperty finalDestination;
        private final javafx.beans.property.SimpleStringProperty temporalWindow;
        private final javafx.beans.property.SimpleStringProperty mediaTypes;

        public ContentFilterRow(String index, String source, String target, String topics, String origin,
                                String finalDestination, String temporalWindow, String mediaTypes) {
            this.index = new javafx.beans.property.SimpleStringProperty(index);
            this.source = new javafx.beans.property.SimpleStringProperty(source);
            this.target = new javafx.beans.property.SimpleStringProperty(target);
            this.topics = new javafx.beans.property.SimpleStringProperty(topics);
            this.origin = new javafx.beans.property.SimpleStringProperty(origin);
            this.finalDestination = new javafx.beans.property.SimpleStringProperty(finalDestination);
            this.temporalWindow = new javafx.beans.property.SimpleStringProperty(temporalWindow);
            this.mediaTypes = new javafx.beans.property.SimpleStringProperty(mediaTypes);
        }

        public String getIndex() { return index.get(); }
        public String getSource() { return source.get(); }
        public String getTarget() { return target.get(); }
        public String getTopics() { return topics.get(); }
        public String getOrigin() { return origin.get(); }
        public String getFinalDestination() { return finalDestination.get(); }
        public String getTemporalWindow() { return temporalWindow.get(); }
        public String getMediaTypes() { return mediaTypes.get(); }

        public javafx.beans.property.StringProperty indexProperty() { return index; }
        public javafx.beans.property.StringProperty sourceProperty() { return source; }
        public javafx.beans.property.StringProperty targetProperty() { return target; }
        public javafx.beans.property.StringProperty topicsProperty() { return topics; }
        public javafx.beans.property.StringProperty originProperty() { return origin; }
        public javafx.beans.property.StringProperty finalDestinationProperty() { return finalDestination; }
        public javafx.beans.property.StringProperty temporalWindowProperty() { return temporalWindow; }
        public javafx.beans.property.StringProperty mediaTypesProperty() { return mediaTypes; }
    }
}