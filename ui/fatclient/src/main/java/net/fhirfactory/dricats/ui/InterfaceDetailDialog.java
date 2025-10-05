/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.oam.topology.InterfaceComponentSummary;
import net.fhirfactory.dricats.internals.pathways.PathwayElement;
import net.fhirfactory.dricats.internals.pubsub.content.ContentFilter;
import net.fhirfactory.dricats.ui.restclient.MainRESTClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialog to display a PathwayElement related to an InterfaceComponentSummary
 * and allow CRUD on ingressContentFilter and egressContentFilter attributes.
 *
 * Minimal implementation: create/delete filters as objects; inner fields are not edited here.
 */
public class InterfaceDetailDialog extends Dialog<Void> {
    private static final Logger LOG = LoggerFactory.getLogger(InterfaceDetailDialog.class);

    private final MainRESTClient client;
    private final InterfaceComponentSummary iface;

    // Interface details
    private final Label ifaceIdValue = new Label("");
    private final Label ifaceNameValue = new Label("");
    private final Label ifaceDocValue = new Label("");
    private final Button viewAssociatedBtn = new Button("View Associated Pathway Elements");

    // PathwayElement details (if any exists/created)
    private Label idValue = new Label("");
    private Label nameValue = new Label("");
    private Label docValue = new Label("");
    private Label sourceValue = new Label("");
    private Label targetValue = new Label("");

    private Label ingressStatus = new Label("None");
    private Button ingressCreateBtn = new Button("Create");
    private Button ingressDeleteBtn = new Button("Delete");

    private Label egressStatus = new Label("None");
    private Button egressCreateBtn = new Button("Create");
    private Button egressDeleteBtn = new Button("Delete");

    private PathwayElement current;

    public InterfaceDetailDialog(MainRESTClient client, InterfaceComponentSummary iface){
        this.client = client;
        this.iface = iface;
        setTitle("Interface Details");
        setResizable(true);
        // Prefer a reasonable default size but allow shrinking down
        getDialogPane().setPrefWidth(720);
        getDialogPane().setPrefHeight(540);
        getDialogPane().setMinWidth(380);
        getDialogPane().setMaxWidth(Double.MAX_VALUE);
        getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        getDialogPane().setContent(buildContent());
        if (getDialogPane().getScene() != null) {
            getDialogPane().getScene().getWindow().setOnCloseRequest(e -> close());
        }
        // Pre-populate interface labels
        try {
            ifaceIdValue.setText(idToString(iface==null? null : iface.getObjectID()));
            ifaceNameValue.setText(nz(iface==null? null : iface.getName()));
            ifaceDocValue.setText(nz(iface==null? null : iface.getDocumentation()));
        } catch (Exception ignored) {}
        loadPathwayElement();
    }

    private Pane buildContent(){
        // Ensure long text fields wrap and do not force dialog width
        Label[] wrapLabels = new Label[]{ifaceIdValue, ifaceNameValue, ifaceDocValue, idValue, nameValue, docValue, sourceValue, targetValue};
        for (Label lbl : wrapLabels) {
            lbl.setWrapText(true);
            lbl.setMaxWidth(Double.MAX_VALUE);
        }

        // Interface details section
        GridPane ifaceGrid = new GridPane();
        ifaceGrid.setHgap(8); ifaceGrid.setVgap(4); ifaceGrid.setPadding(new Insets(8));
        // Two-column layout: left labels fixed, right values grow/shrink
        ColumnConstraints ic0 = new ColumnConstraints(); ic0.setHgrow(Priority.NEVER);
        ColumnConstraints ic1 = new ColumnConstraints(); ic1.setHgrow(Priority.ALWAYS); ic1.setFillWidth(true);
        ifaceGrid.getColumnConstraints().setAll(ic0, ic1);
        ifaceGrid.add(new Label("Interface ID:"), 0, 0); ifaceGrid.add(ifaceIdValue, 1, 0);
        ifaceGrid.add(new Label("Name:"), 0, 1); ifaceGrid.add(ifaceNameValue, 1, 1);
        ifaceGrid.add(new Label("Documentation:"), 0, 2); ifaceGrid.add(ifaceDocValue, 1, 2);
        viewAssociatedBtn.setOnAction(e -> showAssociatedElements());
        HBox ifaceActions = new HBox(8, viewAssociatedBtn);
        ifaceGrid.add(ifaceActions, 1, 3);
        TitledPane ifacePane = new TitledPane("Interface Component", ifaceGrid);
        ifacePane.setCollapsible(false);
        ifacePane.setMaxWidth(Double.MAX_VALUE);

        // PathwayElement details (if available)
        GridPane details = new GridPane();
        details.setHgap(8); details.setVgap(4); details.setPadding(new Insets(8));
        ColumnConstraints c0 = new ColumnConstraints(); c0.setHgrow(Priority.NEVER);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setHgrow(Priority.ALWAYS); c1.setFillWidth(true);
        details.getColumnConstraints().setAll(c0, c1);
        details.add(new Label("ID:"), 0, 0); details.add(idValue, 1, 0);
        details.add(new Label("Name:"), 0, 1); details.add(nameValue, 1, 1);
        details.add(new Label("Documentation:"), 0, 2); details.add(docValue, 1, 2);
        details.add(new Label("Source:"), 0, 3); details.add(sourceValue, 1, 3);
        details.add(new Label("Target:"), 0, 4); details.add(targetValue, 1, 4);
        TitledPane pePane = new TitledPane("Pathway Element (if defined)", details);
        pePane.setCollapsible(false);
        pePane.setMaxWidth(Double.MAX_VALUE);

        // Ingress panel
        VBox ingressBox = new VBox(6,
                new Label("Ingress Content Filter"),
                new HBox(6, new Label("Status:"), ingressStatus),
                new HBox(6, ingressCreateBtn, ingressDeleteBtn)
        );
        ingressBox.setPadding(new Insets(8));
        TitledPane ingressPane = new TitledPane("Ingress Filter", ingressBox);
        ingressPane.setCollapsible(false);
        ingressPane.setMaxWidth(Double.MAX_VALUE);

        // Egress panel
        VBox egressBox = new VBox(6,
                new Label("Egress Content Filter"),
                new HBox(6, new Label("Status:"), egressStatus),
                new HBox(6, egressCreateBtn, egressDeleteBtn)
        );
        egressBox.setPadding(new Insets(8));
        TitledPane egressPane = new TitledPane("Egress Filter", egressBox);
        egressPane.setCollapsible(false);
        egressPane.setMaxWidth(Double.MAX_VALUE);

        ingressCreateBtn.setOnAction(e -> onCreateIngress());
        ingressDeleteBtn.setOnAction(e -> onDeleteIngress());

        egressCreateBtn.setOnAction(e -> onCreateEgress());
        egressDeleteBtn.setOnAction(e -> onDeleteEgress());

        VBox root = new VBox(10, ifacePane, pePane, ingressPane, egressPane);
        root.setFillWidth(true);
        root.setPadding(new Insets(10));
        VBox.setVgrow(ingressPane, Priority.NEVER);
        VBox.setVgrow(egressPane, Priority.NEVER);
        return root;
    }

    private void loadPathwayElement(){
        try {
            if (iface == null) { return; }
            // Try to load by the interface's object id common name
            current = client.getFlowById(iface.getObjectID());
            refreshDisplay();
        } catch (Exception e){
            LOG.warn("[UI] Failed to load PathwayElement for interface {}: {}", iface, e.toString());
        }
    }

    private void refreshDisplay(){
        if(current==null){
            idValue.setText("<none>");
            nameValue.setText(nz(iface==null? null : iface.getName()));
            docValue.setText(nz(iface==null? null : iface.getDocumentation()));
            sourceValue.setText("");
            targetValue.setText("");
            ingressStatus.setText("None");
            egressStatus.setText("None");
            ingressCreateBtn.setDisable(false);
            ingressDeleteBtn.setDisable(true);
            egressCreateBtn.setDisable(false);
            egressDeleteBtn.setDisable(true);
            return;
        }
        idValue.setText(idToString(current.getObjectID()));
        nameValue.setText(nz(current.getName()));
        docValue.setText(nz(current.getDocumentation()));
        sourceValue.setText(idToString(current.getSource()));
        targetValue.setText(idToString(current.getTarget()));
        // Ingress
        if(current.getIngressContentFilter()!=null){
            ingressStatus.setText("Present");
            ingressCreateBtn.setDisable(true);
            ingressDeleteBtn.setDisable(false);
        } else {
            ingressStatus.setText("None");
            ingressCreateBtn.setDisable(false);
            ingressDeleteBtn.setDisable(true);
        }
        // Egress
        if(current.getEgressContentFilter()!=null){
            egressStatus.setText("Present");
            egressCreateBtn.setDisable(true);
            egressDeleteBtn.setDisable(false);
        } else {
            egressStatus.setText("None");
            egressCreateBtn.setDisable(false);
            egressDeleteBtn.setDisable(true);
        }
    }

    private void onCreateIngress(){ ensureCurrentExists(); if(current==null) return; ContentFilter cf = new ContentFilter(); current.setIngressContentFilter(cf); save(); }
    private void onDeleteIngress(){ if(current==null) return; current.setIngressContentFilter(null); save(); }

    private void onCreateEgress(){ ensureCurrentExists(); if(current==null) return; ContentFilter cf = new ContentFilter(); current.setEgressContentFilter(cf); save(); }
    private void onDeleteEgress(){ if(current==null) return; current.setEgressContentFilter(null); save(); }

    private void ensureCurrentExists(){
        if(current!=null) return;
        if(iface==null) return;
        // Create a minimal PathwayElement using the interface's object ID and name
        PathwayElement pe = new PathwayElement();
        try {
            pe.setObjectID(iface.getObjectID());
        } catch (Exception ignored) {}
        pe.setName(iface.getName());
        pe.setDocumentation(iface.getDocumentation());
        PathwayElement created = client.createFlow(pe);
        if(created!=null){ current = created; }
    }

    private void save(){
        if(current==null){ return; }
        PathwayElement updated = client.updateFlow(current);
        if(updated!=null){ current = updated; }
        refreshDisplay();
    }

    private void showAssociatedElements(){
        try {
            String ifaceKey = resolveRestKey(iface==null? null : iface.getObjectID());
            java.util.List<PathwayElement> all = client.listPathwayElements();
            java.util.List<PathwayElement> associated = new java.util.ArrayList<>();
            for(PathwayElement pe : all){
                if(pe==null) continue;
                String src = resolveRestKey(pe.getSource());
                String tgt = resolveRestKey(pe.getTarget());
                if(ifaceKey!=null && (ifaceKey.equals(src) || ifaceKey.equals(tgt))){ associated.add(pe); }
            }
            javafx.scene.control.ListView<String> lv = new javafx.scene.control.ListView<>();
            javafx.collections.ObservableList<String> items = javafx.collections.FXCollections.observableArrayList();
            for(PathwayElement pe : associated){
                String id = idToString(pe.getObjectID());
                String name = pe.getName()==null||pe.getName().isBlank()? id : pe.getName();
                items.add(name + " (" + id + ")");
            }
            if(items.isEmpty()){
                items.add("<No associated Pathway Elements found>");
            }
            lv.setItems(items);
            Dialog<Void> dlg = new Dialog<>();
            dlg.setTitle("Associated Pathway Elements");
            dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dlg.getDialogPane().setContent(new VBox(8, new Label("Elements referencing this interface as source or target:"), lv));
            dlg.showAndWait();
        } catch (Exception e){
            LOG.warn("[UI] Failed to show associated elements: {}", e.toString());
        }
    }

    private static String resolveRestKey(DistributableObjectId id){
        if(id==null){ return null; }
        try { return id.getQualifiedName().getCommonName().getValue(); } catch (Exception e){ return null; }
    }

    private static String idToString(DistributableObjectId id){
        if(id==null){ return ""; }
        try { return String.valueOf(id.getIdToken()); } catch (Throwable t){ return String.valueOf(id); }
    }
    private static String nz(String s){ return s==null? "" : s; }
}
