package net.fhirfactory.dricats.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.fhirfactory.dricats.internals.data.valuesets.MimeTypeEnum;
import net.fhirfactory.dricats.internals.pubsub.content.ContentFilter;
import net.fhirfactory.dricats.internals.pubsub.content.ContentSubscription;
import net.fhirfactory.dricats.internals.pubsub.topics.TopicFilter;
import net.fhirfactory.dricats.internals.pubsub.topics.TopicSubscription;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.EgressApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.IngresApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPInterfaceBase;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
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
        LOG.debug(".setBaseUrl(): [Entry] baseUrl={}", baseUrl);
        this.baseUrl = baseUrl;
        if (this.baseUrl == null || this.baseUrl.isBlank()) {
            this.baseUrl = "http://localhost:12000";
        }
        this.client = new MainRESTClient(this.baseUrl);
        // initial load
        loadRoots();
        LOG.debug(".setBaseUrl(): [Exit]");
    }

    @FXML
    private void initialize() {
        LOG.debug(".initialize(): [Entry]");
        // Tree cell factory for ApplicationComponent
        if (treeView != null) {
            treeView.setShowRoot(false);
            treeView.setCellFactory(tv -> new TreeCell<Object>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else if (item instanceof ApplicationComponent) {
                        ApplicationComponent acs = (ApplicationComponent) item;
                        String label = acs.getShortName();
                        if (label == null || label.isBlank()) {
                            label = net.fhirfactory.dricats.ui.restclient.MainRESTClient.resolveKey(acs);
                        }
                        setText(label);
                        setStyle("");
                    } else if (item instanceof IngresApplicationInterface) {
                        IngresApplicationInterface ifs = (IngresApplicationInterface) item;
                        String label = ifs.getShortName();
                        if (label == null || label.isBlank()) {
                            label = ifs.resolveElementInstanceKey();
                        }
                        setText("[IN] " + label);
                        setStyle("-fx-text-fill: #2a7fff;");
                    } else if (item instanceof EgressApplicationInterface) {
                        EgressApplicationInterface ifs = (EgressApplicationInterface) item;
                        String label = ifs.getShortName();
                        if (label == null || label.isBlank()) {
                            label = ifs.resolveElementInstanceKey();
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
                    if (v instanceof ApplicationComponent s) {
                        String id = net.fhirfactory.dricats.ui.restclient.MainRESTClient.resolveKey(s);
                        LOG.info("[UI] Tree selection changed to: {}", id);
                        loadChildrenIfNeeded(newSel);
                        refreshDetails(s);
                        refreshUniqueName(s);
                        refreshMetrics(s);
                        refreshInterfaceDetails(s);
                    } else if (v instanceof IngresApplicationInterface ifIn) {
                        LOG.info("[UI] Ingress Interface selected: {}", ifIn.resolveElementInstanceKey());
                    } else if (v instanceof EgressApplicationInterface ifOut) {
                        LOG.info("[UI] Egress Interface selected: {}", ifOut.resolveElementInstanceKey());
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
                        if (val instanceof IngresApplicationInterface ingress) {
                            showIngressSubscriptionsDialog(ingress);
                            event.consume();
                        } else if (val instanceof EgressApplicationInterface egress) {
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
                refreshMetrics(v instanceof ApplicationComponent s ? s : null);
            });
        }
        if (refreshMenuItem != null) {
            refreshMenuItem.setOnAction(e -> {
                LOG.info("[UI] Refresh requested.");
                loadRoots();
                TreeItem<Object> sel = treeView.getSelectionModel().getSelectedItem();
                if (sel != null && sel.getValue() instanceof ApplicationComponent s) {
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
        LOG.debug(".initialize(): [Exit]");
    }
    
    private void showIngressSubscriptionsDialog(IngresApplicationInterface ingress) {
        LOG.debug(".showIngressSubscriptionsDialog(): [Entry] ingress={}", ingress);
        String name = null;
        try { name = ingress.getShortName(); } catch (Exception ignored) {}
        if (name == null || name.isBlank()) {
            try { name = ingress.resolveElementInstanceKey(); } catch (Exception ignored) {}
        }
        String title = "Supported Content Subscriptions";
        String header = (name == null || name.isBlank()) ? "Ingress Interface" : name;
        java.util.List<ContentSubscription> subs = null;
        try { subs = ingress.getSubscriptions().getContentSubscriptions(); } catch (Exception ignored) {}

        javafx.collections.ObservableList<IngressSubscriptionRow> rows = javafx.collections.FXCollections.observableArrayList();
        if (subs == null || subs.isEmpty()) {
            rows.add(new IngressSubscriptionRow("N/A", "Info", "No ContentSubscriptions defined for this ingress interface."));
        } else {
            int i = 1;
            for (ContentSubscription cs : subs) {
                if (cs == null) { continue; }
                String subLabel = "Subscription #" + i;
                try {
                    String source = java.util.Objects.toString(cs.getContentSubscriptionMask().getInternalEventSource().getComponentIdMask().prettyPrint(), "");
                    rows.add(new IngressSubscriptionRow(subLabel, "Source", source));
                } catch (Exception ignored) {}
                try {
                    String target = java.util.Objects.toString(cs.getContentSubscriptionMask().getInternalEventTarget().getComponentIdMask().prettyPrint(), "");
                    rows.add(new IngressSubscriptionRow(subLabel, "Target", target));
                } catch (Exception ignored) {}
                try {
                    java.util.List<TopicSubscription> topics = cs.getContentSubscriptionMask().getEventTopicSubscriptions();
                    if (topics != null && !topics.isEmpty()) {
                        StringBuilder topicSb = new StringBuilder();
                        for (int tIdx = 0; tIdx < topics.size(); tIdx++) {
                            topicSb.append(topics.get(tIdx).prettyPrint());
                            if (tIdx < topics.size() - 1) {
                                topicSb.append(", ");
                            }
                        }
                        rows.add(new IngressSubscriptionRow(subLabel, "Topics", topicSb.toString()));
                    }
                } catch (Exception ignored) {}
                try {
                    String origin = java.util.Objects.toString(cs.getContentSubscriptionMask().getEventOrigin(), "");
                    if (!origin.isBlank()) rows.add(new IngressSubscriptionRow(subLabel, "Origin", origin));
                } catch (Exception ignored) {}
                try {
                    String finalDest = java.util.Objects.toString(cs.getContentSubscriptionMask().getEventFinalDestination(), "");
                    if (!finalDest.isBlank()) rows.add(new IngressSubscriptionRow(subLabel, "Final Destination", finalDest));
                } catch (Exception ignored) {}
                try {
                    String tempWindow = java.util.Objects.toString(cs.getContentSubscriptionMask().getTemporalWindow().prettyPrint(), "");
                    if (!tempWindow.isBlank()) rows.add(new IngressSubscriptionRow(subLabel, "Temporal Window", tempWindow));
                } catch (Exception ignored) {}
                i++;
            }
        }

        Alert dlg = new Alert(Alert.AlertType.INFORMATION);
        dlg.setTitle(title);
        dlg.setHeaderText(header);

        TableView<IngressSubscriptionRow> table = new TableView<>();
        TableColumn<IngressSubscriptionRow, String> colSub = new TableColumn<>("Subscription");
        colSub.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("subscription"));
        colSub.setPrefWidth(150);

        TableColumn<IngressSubscriptionRow, String> colAttr = new TableColumn<>("Attribute");
        colAttr.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("attribute"));
        colAttr.setPrefWidth(150);

        TableColumn<IngressSubscriptionRow, String> colVal = new TableColumn<>("Value");
        colVal.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("value"));
        colVal.setPrefWidth(400);

        table.getColumns().addAll(colSub, colAttr, colVal);
        table.setItems(rows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        dlg.getDialogPane().setContent(table);
        dlg.setResizable(true);
        dlg.getDialogPane().setPrefSize(800, 600);
        dlg.showAndWait();
        LOG.debug(".showIngressSubscriptionsDialog(): [Exit]");
    }

    private void showEgressSubscriptionsDialog(EgressApplicationInterface egress) {
        LOG.debug(".showEgressSubscriptionsDialog(): [Entry] egress={}", egress);
        String name = null;
        try { name = egress.getShortName(); } catch (Exception ignored) {}
        if (name == null || name.isBlank()) {
            try { name = egress.resolveElementInstanceKey(); } catch (Exception ignored) {}
        }
        String title = "Supported Content Filters";
        String header = (name == null || name.isBlank()) ? "Egress Interface" : name;
        java.util.List<ContentFilter> subs = null;
        try { subs = egress.getPublishedContent().getContentFilters(); } catch (Exception ignored) {}

        // Build table rows
        javafx.collections.ObservableList<ContentFilterRow> rows = javafx.collections.FXCollections.observableArrayList();
        if (subs != null) {
            int idx = 1;
            for (ContentFilter cf : subs) {
                if (cf == null) { continue; }
                String source = ""; try { source = java.util.Objects.toString(cf.getContentFilterMask().getInternalEventSource().getComponentIdMask().prettyPrint(), ""); } catch (Exception ignored) {}
                String target = ""; try { target = java.util.Objects.toString(cf.getContentFilterMask().getInternalEventTarget().getComponentIdMask().prettyPrint(), ""); } catch (Exception ignored) {}
                String topicsStr = "";
                try {
                    java.util.List<TopicFilter> topics = cf.getContentFilterMask().getEventTopicFilters();;
                    if (topics != null && !topics.isEmpty()) {
                        java.util.List<String> topicTexts = new java.util.ArrayList<>();
                        for (TopicFilter t : topics) {
                            if (t != null) topicTexts.add(t.prettyPrint());
                        }
                        topicsStr = String.join("; ", topicTexts);
                    }
                } catch (Exception ignored) {}
                String origin = ""; try { origin = java.util.Objects.toString(cf.getContentFilterMask().getEventOrigin(), ""); } catch (Exception ignored) {}
                String finalDest = ""; try { finalDest = java.util.Objects.toString(cf.getContentFilterMask().getEventFinalDestination(), ""); } catch (Exception ignored) {}
                String temporal = ""; try { temporal = java.util.Objects.toString(cf.getContentFilterMask().getTemporalWindow().prettyPrint(), ""); } catch (Exception ignored) {}
                String mediaTypes = "";
                try {
                    java.util.List<MimeTypeEnum> mts = cf.getContentFilterMask().getSupportedMediaTypes();
                    if (mts != null && !mts.isEmpty()) {
                        java.util.List<String> mtTexts = new java.util.ArrayList<>();
                        for (MimeTypeEnum mt : mts) {
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
        LOG.debug(".showEgressSubscriptionsDialog(): [Exit]");
    }

    private void refreshDetails(ApplicationComponent summary) {
        LOG.debug(".refreshDetails(): [Entry]");
        if (detailsTable == null) {
            LOG.debug(".refreshDetails(): [Exit] detailsTable is null");
            return;
        }
        detailsTable.setItems(TopologyUIUtilities.getDetailsRows(summary));
        LOG.debug(".refreshDetails(): [Exit]");
    }

    private void refreshUniqueName(ApplicationComponent summary) {
        LOG.debug(".refreshUniqueName(): [Entry]");
        if (uniqueNameTree == null) {
            LOG.debug(".refreshUniqueName(): [Exit] uniqueNameTree is null");
            return;
        }
        uniqueNameTree.setRoot(TopologyUIUtilities.getUniqueNameTreeRoot(summary));
        LOG.debug(".refreshUniqueName(): [Exit]");
    }

    private void refreshMetrics(ApplicationComponent summary) {
        LOG.debug(".refreshMetrics(): [Entry]");
        if (metricsTable == null) {
            LOG.debug(".refreshMetrics(): [Exit] metricsTable is null");
            return;
        }
        metricsTable.setItems(TopologyUIUtilities.getMetricsRows(summary, client));
        LOG.debug(".refreshMetrics(): [Exit]");
    }

    private void loadRoots() {
        LOG.debug(".loadRoots(): [Entry]");
        if (treeView == null) {
            LOG.debug(".loadRoots(): [Exit] treeView is null");
            return;
        }
        TreeItem<Object> hiddenRoot = new TreeItem<>();
        treeView.setRoot(hiddenRoot);
        List<ApplicationComponent> roots = null;
        try {
            roots = client == null ? null : client.listComponents();
        } catch (Exception e) {
            LOG.warn("[UI] Failed to fetch component list: {}", e.toString());
        }
        if (roots == null) {
            LOG.info("[UI] No components returned (null list)");
            LOG.debug(".loadRoots(): [Exit] roots is null");
            return;
        }
        LOG.info("[UI] Loaded {} components", roots.size());
        hiddenRoot.getChildren().clear();
        for (ApplicationComponent s : roots) {
            LOG.trace(".loadRoots(): processing root component={}", s);
            if (s != null) {
                hiddenRoot.getChildren().add(createTreeItem(s));
            }
        }
        LOG.debug(".loadRoots(): [Exit]");
    }

    private TreeItem<Object> createTreeItem(ApplicationComponent s) {
        LOG.debug(".createTreeItem(): [Entry] s={}", s);
        TreeItem<Object> item = new TreeItem<>(s);
        // placeholder child to show expansion arrow
        item.getChildren().add(new TreeItem<>());
        item.expandedProperty().addListener((obs, was, is) -> {
            if (is) loadChildrenIfNeeded(item);
        });
        LOG.debug(".createTreeItem(): [Exit]");
        return item;
    }

    private void loadChildrenIfNeeded(TreeItem<Object> parentItem) {
        LOG.debug(".loadChildrenIfNeeded(): [Entry] parentItem={}", parentItem);
        if (parentItem == null || !(parentItem.getValue() instanceof ApplicationComponent)) {
            LOG.debug(".loadChildrenIfNeeded(): [Exit] parentItem or value mismatch");
            return;
        }
        if (!hasPlaceholder(parentItem)) {
            LOG.debug(".loadChildrenIfNeeded(): [Exit] already loaded");
            return;
        }
        parentItem.getChildren().clear();
        ApplicationComponent s = (ApplicationComponent) parentItem.getValue();
        String id = MainRESTClient.resolveKey(s);
        List<ApplicationComponent> kids = null;
        try {
            kids = client == null ? null : client.listSubcomponents(id);
        } catch (Exception e) {
            LOG.warn("[UI] Failed to fetch subcomponents for {}: {}", id, e.toString());
        }
        if (kids != null) {
            LOG.trace(".loadChildrenIfNeeded(): kids={}", kids);
            for (ApplicationComponent k : kids) {
                if (k != null) parentItem.getChildren().add(createTreeItem(k));
            }
        }
        // also add interfaces
        try {
            java.util.List<? extends WUPInterfaceBase> ifaces = client == null ? null : client.listInterfaces(id);
            if (ifaces != null) {
                LOG.trace(".loadChildrenIfNeeded(): ifaces={}", ifaces);
                for (Object iface : ifaces) {
                    // Some backends may wrap results in arrays; handle both single and array values
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
        LOG.debug(".loadChildrenIfNeeded(): [Exit]");
    }

    private boolean hasPlaceholder(TreeItem<Object> item) {
        LOG.debug(".hasPlaceholder(): [Entry]");
        boolean has = item.getChildren().size() == 1 && item.getChildren().get(0).getValue() == null;
        LOG.debug(".hasPlaceholder(): [Exit] has={}", has);
        return has;
    }

    private void refreshInterfaceDetails(ApplicationComponent summary) {
        LOG.debug(".refreshInterfaceDetails(): [Entry]");
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
        List<? extends WUPInterfaceBase> interfaces = java.util.Collections.emptyList();
        try {
            interfaces = client == null ? java.util.Collections.emptyList() : client.listInterfaces(id);
        } catch (Exception e) {
            LOG.warn("[UI] Failed to fetch interfaces for {}: {}", id, e.toString());
        }
        java.util.List<IngresApplicationInterface> ingressList = new java.util.ArrayList<>();
        java.util.List<EgressApplicationInterface> egressList = new java.util.ArrayList<>();
        if (interfaces != null) {
            for (Object o : interfaces) {
                if (o == null) continue;
                if (o.getClass().isArray()) {
                    int len = java.lang.reflect.Array.getLength(o);
                    for (int i = 0; i < len; i++) {
                        Object elem = java.lang.reflect.Array.get(o, i);
                        if (elem instanceof IngresApplicationInterface) {
                            ingressList.add((IngresApplicationInterface) elem);
                        } else if (elem instanceof EgressApplicationInterface) {
                            egressList.add((EgressApplicationInterface) elem);
                        }
                    }
                } else if (o instanceof IngresApplicationInterface) {
                    ingressList.add((IngresApplicationInterface) o);
                } else if (o instanceof EgressApplicationInterface) {
                    egressList.add((EgressApplicationInterface) o);
                }
            }
        }
        // Populate ingress details (support multiple)
        if (ingressList.isEmpty()) {
            ingressRows.add(new KVRow("Info", "No Ingress Interface present"));
        } else {
            int idx = 1;
            for (IngresApplicationInterface ingress : ingressList) {
                // Section header
                String header = ingress.getShortName();
                if (header == null || header.isBlank()) header = ingress.resolveElementInstanceKey();
                ingressRows.add(new KVRow("Ingress #" + idx, header));
                try { if (ingress.getElementInstanceId() != null && ingress.getElementInstanceId().resolveKey() != null) ingressRows.add(new KVRow("ID", ingress.getElementInstanceId().resolveKey())); } catch (Exception ignored) {}
                try { if (ingress.getShortName() != null) ingressRows.add(new KVRow("Name", java.util.Objects.toString(ingress.getShortName(), ""))); } catch (Exception ignored) {}
                try { ingressRows.add(new KVRow("Element Type", java.util.Objects.toString(ingress.getElementType(), ""))); } catch (Exception ignored) {}
                try { ingressRows.add(new KVRow("Specialization", java.util.Objects.toString(ingress.getSpecialization(), ""))); } catch (Exception ignored) {}
                try { ingressRows.add(new KVRow("Documentation", java.util.Objects.toString(ingress.getDocumentation(), ""))); } catch (Exception ignored) {}
                try { if (ingress.getOwner() != null) ingressRows.add(new KVRow("Parent", java.util.Objects.toString(ingress.getOwner().getElementIdentifier().getIdentifierValue().getCommonName(), ""))); } catch (Exception ignored) {}
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
            for (EgressApplicationInterface egress : egressList) {
                String header = egress.getShortName();
                if (header == null || header.isBlank()) header = egress.resolveElementInstanceKey();
                egressRows.add(new KVRow("Egress #" + idx, header));
                try {
                    if (egress.getElementInstanceId() != null && egress.getElementInstanceId().resolveKey() != null) {
                        egressRows.add(new KVRow("ID", egress.getElementInstanceId().resolveKey()));
                    }
                } catch (Exception ignored) {}
                try {
                    if (egress.getShortName() != null) {
                        egressRows.add(new KVRow("Name", Objects.toString(egress.getShortName(), "")));
                    }
                } catch (Exception ignored) {}
                try { egressRows.add(new KVRow("Element Type", java.util.Objects.toString(egress.getElementType(), ""))); } catch (Exception ignored) {}
                try { egressRows.add(new KVRow("Specialization", java.util.Objects.toString(egress.getSpecialization(), ""))); } catch (Exception ignored) {}
                try { egressRows.add(new KVRow("Documentation", java.util.Objects.toString(egress.getDocumentation(), ""))); } catch (Exception ignored) {}
                try { if (egress.getOwner() != null) egressRows.add(new KVRow("Parent", java.util.Objects.toString(egress.getOwner().getElementIdentifier().getIdentifierValue().getCommonName().getName(), ""))); } catch (Exception ignored) {}
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
        LOG.debug(".refreshInterfaceDetails(): [Exit]");
    }

    // Row model for Ingress Subscription dialog TableView
    public static class IngressSubscriptionRow {
        private final javafx.beans.property.SimpleStringProperty subscription;
        private final javafx.beans.property.SimpleStringProperty attribute;
        private final javafx.beans.property.SimpleStringProperty value;

        public IngressSubscriptionRow(String subscription, String attribute, String value) {
            LOG.debug("IngressSubscriptionRow(): [Entry]");
            this.subscription = new javafx.beans.property.SimpleStringProperty(subscription);
            this.attribute = new javafx.beans.property.SimpleStringProperty(attribute);
            this.value = new javafx.beans.property.SimpleStringProperty(value);
            LOG.debug("IngressSubscriptionRow(): [Exit]");
        }

        public String getSubscription() { return subscription.get(); }
        public String getAttribute() { return attribute.get(); }
        public String getValue() { return value.get(); }

        public javafx.beans.property.StringProperty subscriptionProperty() { return subscription; }
        public javafx.beans.property.StringProperty attributeProperty() { return attribute; }
        public javafx.beans.property.StringProperty valueProperty() { return value; }
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
            LOG.debug("ContentFilterRow(): [Entry]");
            this.index = new javafx.beans.property.SimpleStringProperty(index);
            this.source = new javafx.beans.property.SimpleStringProperty(source);
            this.target = new javafx.beans.property.SimpleStringProperty(target);
            this.topics = new javafx.beans.property.SimpleStringProperty(topics);
            this.origin = new javafx.beans.property.SimpleStringProperty(origin);
            this.finalDestination = new javafx.beans.property.SimpleStringProperty(finalDestination);
            this.temporalWindow = new javafx.beans.property.SimpleStringProperty(temporalWindow);
            this.mediaTypes = new javafx.beans.property.SimpleStringProperty(mediaTypes);
            LOG.debug("ContentFilterRow(): [Exit]");
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