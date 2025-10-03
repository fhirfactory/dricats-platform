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

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.pathways.Pathway;
import net.fhirfactory.dricats.internals.pathways.PathwayRoute;
import net.fhirfactory.dricats.internals.pathways.PathwayElement;
import net.fhirfactory.dricats.internals.pathways.PathwayRouteSegment;
import net.fhirfactory.dricats.internals.pathways.valuesets.PathwayRouteSelectionCriteriaEnum;
import net.fhirfactory.dricats.ui.restclient.MainRESTClient;

public class RoutesTab extends Tab {
    private MainRESTClient client;

    // Left: Tree of Pathways -> Segments
    private javafx.scene.control.TreeView<Object> treeView;

    // Right Top: Pathway details
    private javafx.scene.control.Label pwIdValue;
    private javafx.scene.control.Label pwNameValue;
    private javafx.scene.control.Label pwDocValue;
    private javafx.scene.control.Label pwDistributionValue;
    private javafx.scene.control.Label pwRoutesCountValue;

    // Right: Segment details (top-mid)
    private javafx.scene.control.Label segIndexValue;
    private javafx.scene.control.Label segDistributionValue;
    private javafx.scene.control.Label segFlowsCountValue;

    // Middle: Flows table for selected segment
    private javafx.scene.control.TableView<FlowRow> flowsTable;

    // Bottom: Flow details
    private javafx.scene.control.Label flowNameValue;
    private javafx.scene.control.Label flowDocValue;
    private javafx.scene.control.Label flowSourceValue;
    private javafx.scene.control.Label flowTargetValue;
    private javafx.scene.control.Label flowServiceValue;

    public RoutesTab() {
        this("http://localhost:12000");
    }

    public RoutesTab(String baseUrl) {
        super("Routes");
        setClosable(false);
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/net/fhirfactory/dricats/ui/RoutesTab.fxml")
            );
            javafx.scene.Parent root = loader.load();
            RoutesTabController controller = loader.getController();
            if (controller != null) {
                controller.setBaseUrl(baseUrl);
            }
            setContent(root);
        } catch (Exception e) {
            // Fallback to programmatic UI
            this.client = new MainRESTClient(baseUrl);
            setContent(buildContent());
            loadPathways();
        }
    }

    private BorderPane buildContent() {
        // Left: Tree with refresh
        javafx.scene.control.Button refreshBtn = new javafx.scene.control.Button("Refresh");
        refreshBtn.setOnAction(e -> loadPathways());
        treeView = new javafx.scene.control.TreeView<>();
        treeView.setShowRoot(false);
        treeView.setCellFactory(tv -> new javafx.scene.control.TreeCell<>(){
            @Override protected void updateItem(Object value, boolean empty){
                super.updateItem(value, empty);
                if(empty || value==null){ setText(null); return; }
                if(value instanceof PathwayItem p){ setText(p.name==null||p.name.isBlank()? p.id : p.name); }
                else if(value instanceof SegmentNode s){ setText("Pathway Route " + s.index); }
                else { setText(String.valueOf(value)); }
            }
        });
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> onTreeSelectionChanged(newV==null? null : newV.getValue()));
        javafx.scene.control.Button newPwBtn = new javafx.scene.control.Button("New Pathway");
        javafx.scene.control.Button newRouteBtn = new javafx.scene.control.Button("New Route");
        javafx.scene.control.Button newSegBtn = new javafx.scene.control.Button("New Segment");
        javafx.scene.control.Button editSegBtn = new javafx.scene.control.Button("Edit Segment");
        javafx.scene.control.Button deleteBtn = new javafx.scene.control.Button("Delete");
        newPwBtn.setOnAction(e -> onCreatePathway());
        newRouteBtn.setOnAction(e -> onCreateRoute());
        newSegBtn.setOnAction(e -> onCreateSegment());
        editSegBtn.setOnAction(e -> onEditSegment());
        deleteBtn.setOnAction(e -> onDeleteSelected());
        javafx.scene.layout.HBox toolbar = new javafx.scene.layout.HBox(6, newPwBtn, newRouteBtn, newSegBtn, editSegBtn, deleteBtn, refreshBtn);
        javafx.scene.layout.VBox left = new javafx.scene.layout.VBox(6, new javafx.scene.control.Label("Pathways & Pathway Routes"), toolbar, treeView);
        javafx.scene.layout.VBox.setVgrow(treeView, javafx.scene.layout.Priority.ALWAYS);
        left.setPadding(new Insets(8));

        // Right: details area (Pathway top, Segment mid-top, Flows mid, Flow details bottom)
        // Pathway details panel
        pwIdValue = new javafx.scene.control.Label("");
        pwNameValue = new javafx.scene.control.Label("");
        pwDocValue = new javafx.scene.control.Label("");
        pwDocValue.setWrapText(true);
        pwDistributionValue = new javafx.scene.control.Label("");
        pwRoutesCountValue = new javafx.scene.control.Label("");
        javafx.scene.layout.GridPane pwGrid = new javafx.scene.layout.GridPane();
        pwGrid.setHgap(8); pwGrid.setVgap(4); pwGrid.setPadding(new Insets(6));
        pwGrid.add(new javafx.scene.control.Label("Pathway ID:"), 0, 0); pwGrid.add(pwIdValue, 1, 0);
        pwGrid.add(new javafx.scene.control.Label("Name:"), 0, 1); pwGrid.add(pwNameValue, 1, 1);
        pwGrid.add(new javafx.scene.control.Label("Documentation:"), 0, 2); pwGrid.add(pwDocValue, 1, 2);
        pwGrid.add(new javafx.scene.control.Label("Distribution:"), 0, 3); pwGrid.add(pwDistributionValue, 1, 3);
        pwGrid.add(new javafx.scene.control.Label("Routes Count:"), 0, 4); pwGrid.add(pwRoutesCountValue, 1, 4);
        javafx.scene.control.TitledPane pwPane = new javafx.scene.control.TitledPane("Pathway", pwGrid);
        pwPane.setCollapsible(false);

        // Segment details panel
        segIndexValue = new javafx.scene.control.Label("");
        segDistributionValue = new javafx.scene.control.Label("");
        segFlowsCountValue = new javafx.scene.control.Label("");
        javafx.scene.layout.GridPane segGrid = new javafx.scene.layout.GridPane();
        segGrid.setHgap(8); segGrid.setVgap(4); segGrid.setPadding(new Insets(6));
        segGrid.add(new javafx.scene.control.Label("Route Index:"), 0, 0); segGrid.add(segIndexValue, 1, 0);
        segGrid.add(new javafx.scene.control.Label("Distribution:"), 0, 1); segGrid.add(segDistributionValue, 1, 1);
        segGrid.add(new javafx.scene.control.Label("Segments Count:"), 0, 2); segGrid.add(segFlowsCountValue, 1, 2);
        javafx.scene.control.TitledPane segPane = new javafx.scene.control.TitledPane("Pathway Route", segGrid);
        segPane.setCollapsible(false);

        // Flows table
        flowsTable = new javafx.scene.control.TableView<>();
        javafx.scene.control.TableColumn<FlowRow,String> nameCol = new javafx.scene.control.TableColumn<>("Element");
        nameCol.setCellValueFactory(c -> c.getValue().nameProperty());
        nameCol.setPrefWidth(200);
        javafx.scene.control.TableColumn<FlowRow,String> srcCol = new javafx.scene.control.TableColumn<>("Source");
        srcCol.setCellValueFactory(c -> c.getValue().sourceProperty());
        javafx.scene.control.TableColumn<FlowRow,String> tgtCol = new javafx.scene.control.TableColumn<>("Target");
        tgtCol.setCellValueFactory(c -> c.getValue().targetProperty());
        javafx.scene.control.TableColumn<FlowRow,String> svcCol = new javafx.scene.control.TableColumn<>("Service");
        svcCol.setCellValueFactory(c -> c.getValue().serviceProperty());
        flowsTable.getColumns().setAll(nameCol, srcCol, tgtCol, svcCol);
        flowsTable.getSelectionModel().selectedItemProperty().addListener((o,ov,nv) -> showFlowDetails(nv==null? null : nv.flow));
        javafx.scene.control.TitledPane flowsPane = new javafx.scene.control.TitledPane("Pathway Route Segment", flowsTable);
        flowsPane.setCollapsible(false);
        javafx.scene.layout.VBox.setVgrow(flowsPane, javafx.scene.layout.Priority.ALWAYS);

        // Flow details panel
        flowNameValue = new javafx.scene.control.Label("");
        flowDocValue = new javafx.scene.control.Label("");
        flowDocValue.setWrapText(true);
        flowSourceValue = new javafx.scene.control.Label("");
        flowTargetValue = new javafx.scene.control.Label("");
        flowServiceValue = new javafx.scene.control.Label("");
        javafx.scene.layout.GridPane flowGrid = new javafx.scene.layout.GridPane();
        flowGrid.setHgap(8); flowGrid.setVgap(4); flowGrid.setPadding(new Insets(6));
        flowGrid.add(new javafx.scene.control.Label("Name:"), 0, 0); flowGrid.add(flowNameValue, 1, 0);
        flowGrid.add(new javafx.scene.control.Label("Documentation:"), 0, 1); flowGrid.add(flowDocValue, 1, 1);
        flowGrid.add(new javafx.scene.control.Label("Source Interface:"), 0, 2); flowGrid.add(flowSourceValue, 1, 2);
        flowGrid.add(new javafx.scene.control.Label("Target Interface:"), 0, 3); flowGrid.add(flowTargetValue, 1, 3);
        flowGrid.add(new javafx.scene.control.Label("Utilised Service:"), 0, 4); flowGrid.add(flowServiceValue, 1, 4);
        javafx.scene.control.TitledPane flowPane = new javafx.scene.control.TitledPane("Pathway Element", flowGrid);
        flowPane.setCollapsible(false);

        javafx.scene.layout.VBox right = new javafx.scene.layout.VBox(8, pwPane, segPane, flowsPane, flowPane);
        right.setPadding(new Insets(8));
        javafx.scene.layout.VBox.setVgrow(flowsTable, javafx.scene.layout.Priority.ALWAYS);

        BorderPane pane = new BorderPane();
        pane.setLeft(left);
        pane.setCenter(right);
        return pane;
    }

    private void onTreeSelectionChanged(Object value){
        if(value instanceof SegmentNode s){
            // Update pathway pane using the parent pathway id
            net.fhirfactory.dricats.internals.pathways.Pathway p = client.getPathway(s.pathwayId);
            showPathwayDetails(p);
            // Update segment info
            populateSegmentDetails(s.pathwayId, s.index, s.segment);
        } else if (value instanceof PathwayItem pItem){
            net.fhirfactory.dricats.internals.pathways.Pathway p = client.getPathway(pItem.id);
            showPathwayDetails(p);
            clearSegmentDetails();
        } else {
            clearPathwayDetails();
            clearSegmentDetails();
        }
    }

    private void loadPathways(){
        java.util.List<java.util.Map<String,Object>> list = client.listPathways();
        javafx.scene.control.TreeItem<Object> root = new javafx.scene.control.TreeItem<>("root");
        root.setExpanded(true);
        for(java.util.Map<String,Object> m: list){
            try {
                String id = String.valueOf(m.getOrDefault("id",""));
                String name = (m.get("name")==null? id : String.valueOf(m.get("name")));
                String doc = m.get("documentation")==null? "" : String.valueOf(m.get("documentation"));
                PathwayItem p = new PathwayItem(id, name, doc);
                javafx.scene.control.TreeItem<Object> pItem = new javafx.scene.control.TreeItem<>(p);
                // add segment children
                java.util.Map<Integer, PathwayRoute> segs = client.getPathwaySegments(id);
                java.util.List<Integer> keys = new java.util.ArrayList<>(segs.keySet());
                java.util.Collections.sort(keys);
                for(Integer k: keys){
                    PathwayRoute seg = segs.get(k);
                    if(seg!=null){
                        SegmentNode sn = new SegmentNode(id, k, seg);
                        pItem.getChildren().add(new javafx.scene.control.TreeItem<>(sn));
                    }
                }
                pItem.setExpanded(true);
                root.getChildren().add(pItem);
            } catch (Exception ignored) {}
        }
        treeView.setRoot(root);
        // select first segment if present
        if(!root.getChildren().isEmpty()){
            for (javafx.scene.control.TreeItem<Object> pItem : root.getChildren()){
                if(!pItem.getChildren().isEmpty()){
                    treeView.getSelectionModel().select(pItem.getChildren().get(0));
                    break;
                }
            }
        }
    }

    private void populateSegmentDetails(String pathwayId, Integer index, PathwayRoute seg){
        segIndexValue.setText(String.valueOf(index));
        // Distribution pattern is defined at the Pathway level in the refactored model
        net.fhirfactory.dricats.internals.pathways.Pathway pathway = client.getPathway(pathwayId);
        String dist = (pathway==null || pathway.getRouteSelectionCriteria()==null) ? "" : pathway.getRouteSelectionCriteria().name();
        segDistributionValue.setText(dist);
        int flowCount = seg.getRouteSegmentSequence()==null? 0 : seg.getRouteSegmentSequence().size();
        segFlowsCountValue.setText(String.valueOf(flowCount));

        // Populate flows table
        javafx.collections.ObservableList<FlowRow> rows = javafx.collections.FXCollections.observableArrayList();
        if(seg.getRouteSegmentSequence()!=null){
            java.util.List<Integer> keys = new java.util.ArrayList<>(seg.getRouteSegmentSequence().keySet());
            java.util.Collections.sort(keys);
            for(Integer k: keys){
                net.fhirfactory.dricats.internals.common.DistributableObjectId pathId = seg.getRouteSegmentSequence().get(k);
                if(pathId==null){ continue; }
                PathwayRouteSegment path = client.getPathById(pathId);
                if(path==null || path.getPathwayElementSequence()==null){ continue; }
                java.util.List<Integer> eKeys = new java.util.ArrayList<>(path.getPathwayElementSequence().keySet());
                java.util.Collections.sort(eKeys);
                for(Integer ek: eKeys){
                    net.fhirfactory.dricats.internals.common.DistributableObjectId flowId = path.getPathwayElementSequence().get(ek);
                    if(flowId==null){ continue; }
                    PathwayElement flow = client.getFlowById(flowId);
                    if(flow!=null){ rows.add(new FlowRow(flow)); }
                }
            }
        }
        flowsTable.setItems(rows);
        if(!rows.isEmpty()){
            flowsTable.getSelectionModel().selectFirst();
        } else {
            showFlowDetails(null);
        }
    }

    private void clearSegmentDetails(){
        segIndexValue.setText("");
        segDistributionValue.setText("");
        segFlowsCountValue.setText("");
        flowsTable.getItems().clear();
        showFlowDetails(null);
    }

    private void showPathwayDetails(net.fhirfactory.dricats.internals.pathways.Pathway p){
        if(p==null){
            clearPathwayDetails();
            return;
        }
        pwIdValue.setText(idToString(p.getObjectID()));
        pwNameValue.setText(nz(p.getName()));
        pwDocValue.setText(nz(p.getDocumentation()));
        pwDistributionValue.setText(p.getRouteSelectionCriteria()==null? "" : p.getRouteSelectionCriteria().name());
        int routesCount = (p.getPossiblePathwayRoutes()==null)? 0 : p.getPossiblePathwayRoutes().size();
        pwRoutesCountValue.setText(String.valueOf(routesCount));
    }

    private void clearPathwayDetails(){
        pwIdValue.setText("");
        pwNameValue.setText("");
        pwDocValue.setText("");
        pwDistributionValue.setText("");
        pwRoutesCountValue.setText("");
    }

    private void showFlowDetails(PathwayElement flow){
        if(flow==null){
            flowNameValue.setText("");
            flowDocValue.setText("");
            flowSourceValue.setText("");
            flowTargetValue.setText("");
            flowServiceValue.setText("");
            return;
        }
        flowNameValue.setText(nz(flow.getName()));
        flowDocValue.setText(nz(flow.getDocumentation()));
        flowSourceValue.setText(idToString(flow.getSource()));
        flowTargetValue.setText(idToString(flow.getTarget()));
        flowServiceValue.setText(idToString(flow.getUtilisedApplicationService()));
    }

    private static String idToString(DistributableObjectId id){
        if(id==null){ return ""; }
        try { return String.valueOf(id.getIdToken()); } catch (Throwable t){ return String.valueOf(id); }
    }

    private static String nz(String s){ return s==null? "" : s; }

    private String selectedPathwayId(){
        javafx.scene.control.TreeItem<Object> item = treeView.getSelectionModel().getSelectedItem();
        if(item==null){ return null; }
        Object v = item.getValue();
        if(v instanceof SegmentNode s){ return s.pathwayId; }
        if(v instanceof PathwayItem p){ return p.id; }
        return null;
    }

    private static String resolveRestKey(DistributableObjectId id){
        if(id==null){ return null; }
        try{ return id.getQualifiedName().getCommonName().getValue(); } catch (Exception e){ return null; }
    }

    private void onCreatePathway(){
        javafx.scene.control.TextInputDialog nameDlg = new javafx.scene.control.TextInputDialog("");
        nameDlg.setTitle("New Pathway");
        nameDlg.setHeaderText("Enter a name for the new Pathway");
        nameDlg.setContentText("Name:");
        java.util.Optional<String> nameRes = nameDlg.showAndWait();
        if(nameRes.isEmpty() || nameRes.get().isBlank()){ return; }
        String name = nameRes.get().trim();

        javafx.scene.control.TextInputDialog docDlg = new javafx.scene.control.TextInputDialog("");
        docDlg.setTitle("New Pathway");
        docDlg.setHeaderText("Optional documentation");
        docDlg.setContentText("Documentation:");
        String documentation = docDlg.showAndWait().orElse("");

        Pathway p = new Pathway();
        p.setName(name);
        p.setDocumentation(documentation);
        // default selection criteria
        p.setRouteSelectionCriteria(PathwayRouteSelectionCriteriaEnum.DISTRIBUTE_RANDOM);
        QualifiedName qn = new QualifiedName();
        qn.appendUnqualifiedName(new UnqualifiedName("Pathway", name));
        p.setObjectID(new DistributableObjectId(qn));
        Pathway created = client.createPathway(p);
        loadPathways();
        // Try to select created pathway
        if(created!=null){ selectPathway(resolveRestKey(created.getObjectID())); }
    }

    private void onCreateRoute(){
        String pwId = selectedPathwayId();
        if(pwId==null){ return; }
        javafx.scene.control.TextInputDialog segDlg = new javafx.scene.control.TextInputDialog("StepA-StepB-StepC");
        segDlg.setTitle("New Pathway Route");
        segDlg.setHeaderText("Enter a label for the new Pathway Route (e.g., A-B-C)");
        segDlg.setContentText("Label:");
        java.util.Optional<String> res = segDlg.showAndWait();
        if(res.isEmpty() || res.get().isBlank()){ return; }
        String label = res.get().trim();
        // Build route with ID based on parent pathway qualified name
        Pathway parent = client.getPathway(pwId);
        if(parent==null || parent.getObjectID()==null){ return; }
        QualifiedName rq = new QualifiedName(parent.getObjectID().getQualifiedName());
        rq.appendUnqualifiedName(new UnqualifiedName("Segment", label));
        PathwayRoute route = new PathwayRoute();
        route.setObjectID(new DistributableObjectId(rq));
        PathwayRoute created = client.createPathwayRoute(pwId, route);
        loadPathways();
        if(created!=null){ selectPathway(pwId); }
    }

    private void onCreateSegment(){
        javafx.scene.control.TreeItem<Object> item = treeView.getSelectionModel().getSelectedItem();
        if(item==null || !(item.getValue() instanceof SegmentNode)) { return; }
        SegmentNode sn = (SegmentNode) item.getValue();
        PathwayRoute route = sn.segment;
        java.util.List<PathwayElement> allElements = client.listPathwayElements();
        SegmentEditorDialog dlg = new SegmentEditorDialog(allElements, null);
        java.util.Optional<SegmentEditorDialog.Result> res = dlg.showAndWait();
        if(res.isEmpty()){ return; }
        SegmentEditorDialog.Result r = res.get();
        // Build segment
        QualifiedName base = new QualifiedName(route.getObjectID().getQualifiedName());
        String label = r.label==null||r.label.isBlank()? ("Path-"+System.currentTimeMillis()) : r.label.trim();
        base.appendUnqualifiedName(new UnqualifiedName("Path", label));
        PathwayRouteSegment seg = new PathwayRouteSegment();
        seg.setObjectID(new DistributableObjectId(base));
        int i=1;
        for(PathwayElement pe : r.ordered){
            if(pe!=null && pe.getObjectID()!=null){ seg.getPathwayElementSequence().put(i++, pe.getObjectID()); }
        }
        PathwayRouteSegment saved = client.updatePathwayRouteSegment(seg);
        if(saved==null){ return; }
        // Update parent route to include this segment
        if(route.getRouteSegmentSequence()==null){ route.setRouteSegmentSequence(new java.util.LinkedHashMap<>()); }
        int next = 1;
        if(!route.getRouteSegmentSequence().isEmpty()){
            next = new java.util.ArrayList<>(route.getRouteSegmentSequence().keySet()).stream().max(Integer::compareTo).orElse(0) + 1;
        }
        route.getRouteSegmentSequence().put(next, saved.getObjectID());
        client.updatePathwayRoute(route);
        loadPathways();
        selectPathway(sn.pathwayId);
    }

    private void onEditSegment(){
        javafx.scene.control.TreeItem<Object> item = treeView.getSelectionModel().getSelectedItem();
        if(item==null || !(item.getValue() instanceof SegmentNode)) { return; }
        SegmentNode sn = (SegmentNode) item.getValue();
        PathwayRoute route = sn.segment;
        if(route.getRouteSegmentSequence()==null || route.getRouteSegmentSequence().isEmpty()){ return; }
        // Choose which segment
        java.util.List<Integer> keys = new java.util.ArrayList<>(route.getRouteSegmentSequence().keySet());
        java.util.Collections.sort(keys);
        Integer chosenKey = keys.get(0);
        if(keys.size()>1){
            javafx.scene.control.ChoiceDialog<Integer> choice = new javafx.scene.control.ChoiceDialog<>(keys.get(0), keys);
            choice.setTitle("Edit Segment");
            choice.setHeaderText("Select segment index to edit");
            choice.setContentText("Index:");
            java.util.Optional<Integer> c = choice.showAndWait();
            if(c.isEmpty()){ return; }
            chosenKey = c.get();
        }
        DistributableObjectId segId = route.getRouteSegmentSequence().get(chosenKey);
        PathwayRouteSegment existing = client.getPathById(segId);
        java.util.List<PathwayElement> allElements = client.listPathwayElements();
        SegmentEditorDialog dlg = new SegmentEditorDialog(allElements, existing);
        java.util.Optional<SegmentEditorDialog.Result> res = dlg.showAndWait();
        if(res.isEmpty()){ return; }
        SegmentEditorDialog.Result r = res.get();
        // Update existing object (may change name/doc/elements; keep same ObjectID unless label changed)
        QualifiedName qn = new QualifiedName(existing.getObjectID().getQualifiedName());
        if(r.label!=null && !r.label.isBlank()){
            // Rebuild tail to use provided label
            // Remove last component by reconstructing from route base and appending Path/label
            QualifiedName base = new QualifiedName(route.getObjectID().getQualifiedName());
            base.appendUnqualifiedName(new UnqualifiedName("Path", r.label.trim()));
            existing.setObjectID(new DistributableObjectId(base));
        }
        existing.getPathwayElementSequence().clear();
        int i=1; for(PathwayElement pe : r.ordered){ if(pe!=null && pe.getObjectID()!=null){ existing.getPathwayElementSequence().put(i++, pe.getObjectID()); } }
        PathwayRouteSegment saved = client.updatePathwayRouteSegment(existing);
        if(saved==null){ return; }
        // If ID changed, update reference in route
        String newKey = resolveRestKey(saved.getObjectID());
        String oldKey = resolveRestKey(segId);
        if(newKey!=null && oldKey!=null && !newKey.equals(oldKey)){
            route.getRouteSegmentSequence().put(chosenKey, saved.getObjectID());
            client.updatePathwayRoute(route);
        }
        loadPathways();
        selectPathway(sn.pathwayId);
    }

    private void onDeleteSelected(){
        javafx.scene.control.TreeItem<Object> item = treeView.getSelectionModel().getSelectedItem();
        if(item==null){ return; }
        Object v = item.getValue();
        if(v instanceof PathwayItem p){
            client.deletePathway(p.id);
            loadPathways();
        } else if (v instanceof SegmentNode s){
            String key = resolveRestKey(s.segment.getObjectID());
            if(key!=null){ client.deletePathwayRoute(key); }
            loadPathways();
            selectPathway(s.pathwayId);
        }
    }

    private void selectPathway(String id){
        if(treeView.getRoot()==null){ return; }
        for(javafx.scene.control.TreeItem<Object> pItem : treeView.getRoot().getChildren()){
            Object v = pItem.getValue();
            if(v instanceof PathwayItem p && p.id.equals(id)){
                treeView.getSelectionModel().select(pItem);
                treeView.scrollTo(treeView.getRow(pItem));
                return;
            }
        }
    }

    // Model wrappers for tree/table
    private static class PathwayItem {
        final String id; final String name; final String documentation;
        PathwayItem(String id, String name, String documentation){ this.id=id; this.name=name; this.documentation=documentation; }
        @Override public String toString(){ return name==null||name.isBlank()? id : name; }
    }

    private static class SegmentNode {
        final String pathwayId; final Integer index; final PathwayRoute segment;
        SegmentNode(String pathwayId, Integer index, PathwayRoute segment){ this.pathwayId=pathwayId; this.index=index; this.segment=segment; }
        @Override public String toString(){ return "Pathway Route "+index; }
    }

    public static class FlowRow {
        final PathwayElement flow;
        private final javafx.beans.property.SimpleStringProperty name = new javafx.beans.property.SimpleStringProperty("");
        private final javafx.beans.property.SimpleStringProperty source = new javafx.beans.property.SimpleStringProperty("");
        private final javafx.beans.property.SimpleStringProperty target = new javafx.beans.property.SimpleStringProperty("");
        private final javafx.beans.property.SimpleStringProperty service = new javafx.beans.property.SimpleStringProperty("");
        FlowRow(PathwayElement flow){
            this.flow = flow;
            this.name.set(nz(flow.getName()));
            this.source.set(idToString(flow.getSource()));
            this.target.set(idToString(flow.getTarget()));
            this.service.set(idToString(flow.getUtilisedApplicationService()));
        }
        public javafx.beans.property.StringProperty nameProperty(){ return name; }
        public javafx.beans.property.StringProperty sourceProperty(){ return source; }
        public javafx.beans.property.StringProperty targetProperty(){ return target; }
        public javafx.beans.property.StringProperty serviceProperty(){ return service; }
    }

    // Dialog for creating/updating a PathwayRouteSegment
    private static class SegmentEditorDialog extends javafx.scene.control.Dialog<SegmentEditorDialog.Result> {
        private final javafx.scene.control.TextField labelField = new javafx.scene.control.TextField("");
        private final javafx.scene.control.ListView<PathwayElement> availableList = new javafx.scene.control.ListView<>();
        private final javafx.scene.control.ListView<PathwayElement> selectedList = new javafx.scene.control.ListView<>();

        static class Result {
            final String label;
            final java.util.List<PathwayElement> ordered;
            Result(String label, java.util.List<PathwayElement> ordered){
                this.label = label; this.ordered = ordered;
            }
        }

        SegmentEditorDialog(java.util.List<PathwayElement> allElements, PathwayRouteSegment existing){
            setTitle(existing==null? "New Pathway Route Segment" : "Edit Pathway Route Segment");
            getDialogPane().getButtonTypes().addAll(javafx.scene.control.ButtonType.OK, javafx.scene.control.ButtonType.CANCEL);

            // Render items as names
            availableList.setCellFactory(lv -> new javafx.scene.control.ListCell<>(){
                @Override protected void updateItem(PathwayElement item, boolean empty){
                    super.updateItem(item, empty);
                    if(empty || item==null){ setText(null); } else { setText(nz(item.getName())); }
                }
            });
            selectedList.setCellFactory(availableList.getCellFactory());

            availableList.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
            selectedList.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

            availableList.getItems().setAll(allElements);

            javafx.scene.control.Button addBtn = new javafx.scene.control.Button("Add →");
            javafx.scene.control.Button removeBtn = new javafx.scene.control.Button("← Remove");
            javafx.scene.control.Button upBtn = new javafx.scene.control.Button("Up");
            javafx.scene.control.Button downBtn = new javafx.scene.control.Button("Down");

            addBtn.setOnAction(e -> {
                java.util.List<PathwayElement> sel = new java.util.ArrayList<>(availableList.getSelectionModel().getSelectedItems());
                for(PathwayElement pe : sel){ if(!selectedList.getItems().contains(pe)) selectedList.getItems().add(pe); }
            });
            removeBtn.setOnAction(e -> {
                java.util.List<PathwayElement> sel = new java.util.ArrayList<>(selectedList.getSelectionModel().getSelectedItems());
                selectedList.getItems().removeAll(sel);
            });
            upBtn.setOnAction(e -> moveSelected(-1));
            downBtn.setOnAction(e -> moveSelected(1));

            javafx.scene.layout.GridPane top = new javafx.scene.layout.GridPane();
            top.setHgap(6); top.setVgap(6);
            top.add(new javafx.scene.control.Label("Label:"), 0, 0); top.add(labelField, 1, 0);

            javafx.scene.layout.VBox left = new javafx.scene.layout.VBox(6, new javafx.scene.control.Label("Available Elements"), availableList);
            javafx.scene.layout.VBox right = new javafx.scene.layout.VBox(6, new javafx.scene.control.Label("Selected (ordered)"), selectedList,
                    new javafx.scene.layout.HBox(6, upBtn, downBtn));
            javafx.scene.layout.VBox.setVgrow(availableList, javafx.scene.layout.Priority.ALWAYS);
            javafx.scene.layout.VBox.setVgrow(selectedList, javafx.scene.layout.Priority.ALWAYS);
            javafx.scene.layout.VBox centerBtns = new javafx.scene.layout.VBox(6, addBtn, removeBtn);
            centerBtns.setFillWidth(true);
            javafx.scene.layout.BorderPane listsPane = new javafx.scene.layout.BorderPane();
            listsPane.setLeft(left);
            listsPane.setCenter(centerBtns);
            listsPane.setRight(right);
            javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(10, top, listsPane);
            root.setPadding(new Insets(10));
            getDialogPane().setContent(root);

            // Pre-populate if editing
            if(existing!=null){
                // Try to infer label from last segment of QualifiedName
                try {
                    String last = existing.getObjectID().getQualifiedName().getCommonName().getValue();
                    labelField.setText(last);
                } catch (Exception ignored) {}
                // Preselect elements in order
                if(existing.getPathwayElementSequence()!=null){
                    java.util.List<Integer> keys = new java.util.ArrayList<>(existing.getPathwayElementSequence().keySet());
                    java.util.Collections.sort(keys);
                    for(Integer k: keys){
                        // lookup by id among allElements
                        net.fhirfactory.dricats.internals.common.DistributableObjectId oid = existing.getPathwayElementSequence().get(k);
                        if(oid==null) continue;
                        for(PathwayElement pe: allElements){
                            try{
                                String a = resolveRestKey(pe.getObjectID());
                                String b = resolveRestKey(oid);
                                if(a!=null && a.equals(b)){ selectedList.getItems().add(pe); break; }
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }

            setResultConverter(btn -> {
                if(btn == javafx.scene.control.ButtonType.OK){
                    java.util.List<PathwayElement> ordered = new java.util.ArrayList<>(selectedList.getItems());
                    return new Result(labelField.getText(), ordered);
                }
                return null;
            });
        }

        private void moveSelected(int delta){
            int idx = selectedList.getSelectionModel().getSelectedIndex();
            if(idx<0) return;
            int newIdx = idx + delta;
            if(newIdx<0 || newIdx>= selectedList.getItems().size()) return;
            PathwayElement item = selectedList.getItems().remove(idx);
            selectedList.getItems().add(newIdx, item);
            selectedList.getSelectionModel().select(newIdx);
        }
    }
}
