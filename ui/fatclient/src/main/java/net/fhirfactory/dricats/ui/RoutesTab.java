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
import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.pathways.Pathway;
import net.fhirfactory.dricats.internals.pathways.PathwayElement;
import net.fhirfactory.dricats.internals.pathways.PathwayRoute;
import net.fhirfactory.dricats.internals.pathways.PathwayRouteSegment;
import net.fhirfactory.dricats.internals.pathways.valuesets.PathwayRouteSelectionCriteriaEnum;
import net.fhirfactory.dricats.ui.restclient.MainRESTClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RoutesTab extends Tab {
    private static final Logger LOG = LoggerFactory.getLogger(RoutesTab.class);

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
        this("http://localhost:12101");
        LOG.debug("RoutesTab(): [Entry/Exit]");
    }

    public RoutesTab(String baseUrl) {
        super("Routes");
        LOG.debug("RoutesTab(): [Entry] baseUrl={}", baseUrl);
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
        LOG.debug("RoutesTab(): [Exit]");
    }

    private BorderPane buildContent() {
        LOG.debug(".buildContent(): [Entry]");
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
        LOG.debug(".buildContent(): [Exit]");
        return pane;
    }

    private void onTreeSelectionChanged(Object value){
        LOG.debug(".onTreeSelectionChanged(): [Entry] value={}", value);
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
        LOG.debug(".onTreeSelectionChanged(): [Exit]");
    }

    private void loadPathways(){
        LOG.debug(".loadPathways(): [Entry]");
        List<Pathway> pathwayList = client.listPathways();
        LOG.info(".loadPathways(): Retrieved List, size -> {}", pathwayList.size());
        javafx.scene.control.TreeItem<Object> root = new javafx.scene.control.TreeItem<>("root");
        root.setExpanded(true);
        for(Pathway currentPathway: pathwayList){
            try {
                LOG.debug(".loadPathways(): Processing entry: {}", currentPathway);
                String id = currentPathway.resolveElementInstanceKey();
                String name = currentPathway.getShortName();
                String doc = currentPathway.getDocumentation();
                LOG.info(".loadPathways(): Pathway id={}, name={}", id, name);

                PathwayItem p = new PathwayItem(id, name, doc);
                javafx.scene.control.TreeItem<Object> pItem = new javafx.scene.control.TreeItem<>(p);
                // add segment children
                java.util.Map<Integer, PathwayRoute> segs = client.getPathwaySegments(id);
                if (segs != null) {
                    java.util.List<Integer> keys = new java.util.ArrayList<>(segs.keySet());
                    java.util.Collections.sort(keys);
                    for(Integer k: keys){
                        PathwayRoute seg = segs.get(k);
                        if(seg!=null){
                            SegmentNode sn = new SegmentNode(id, k, seg);
                            pItem.getChildren().add(new javafx.scene.control.TreeItem<>(sn));
                        }
                    }
                }
                pItem.setExpanded(true);
                root.getChildren().add(pItem);
            } catch (Exception e) {
                LOG.error(".loadPathways(): Failed to process pathway entry", e);
            }
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
        LOG.debug(".loadPathways(): [Exit]");
    }

    private void populateSegmentDetails(String pathwayId, Integer index, PathwayRoute seg){
        LOG.debug(".populateSegmentDetails(): [Entry] pathwayId={}, index={}, seg={}", pathwayId, index, seg);
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
            List<Integer> keys = new ArrayList<>(seg.getRouteSegmentSequence().keySet());
            Collections.sort(keys);
            for(Integer k: keys){
                LOG.trace(".populateSegmentDetails(): key={}", k);
                ElementReference pathId = seg.getRouteSegmentSequence().get(k);
                if(pathId==null){ continue; }
                PathwayRouteSegment path = client.getPathById(pathId.getElementInstanceId().resolveKey());
                if(path==null || path.getPathwayElementSequence()==null){ continue; }
                List<Integer> eKeys = new ArrayList<>(path.getPathwayElementSequence().keySet());
                Collections.sort(eKeys);
                for(Integer ek: eKeys){
                    LOG.trace(".populateSegmentDetails(): eKey={}", ek);
                    ElementReference flowId = path.getPathwayElementSequence().get(ek);
                    if(flowId==null){ continue; }
                    if(flowId.getElementInstanceId() == null){continue;}
                    PathwayElement flow = client.getFlowById(flowId.getElementInstanceId());
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
        LOG.debug(".populateSegmentDetails(): [Exit]");
    }

    private void clearSegmentDetails(){
        LOG.debug(".clearSegmentDetails(): [Entry]");
        segIndexValue.setText("");
        segDistributionValue.setText("");
        segFlowsCountValue.setText("");
        flowsTable.getItems().clear();
        showFlowDetails(null);
        LOG.debug(".clearSegmentDetails(): [Exit]");
    }

    private void showPathwayDetails(net.fhirfactory.dricats.internals.pathways.Pathway p){
        LOG.debug(".showPathwayDetails(): [Entry] p={}", p);
        if(p==null){
            clearPathwayDetails();
            LOG.debug(".showPathwayDetails(): [Exit] p is null");
            return;
        }
        pwIdValue.setText(idToString(p.getIdentifier()));
        pwNameValue.setText(nz(p.getShortName()));
        pwDocValue.setText(nz(p.getDocumentation()));
        pwDistributionValue.setText(p.getRouteSelectionCriteria()==null? "" : p.getRouteSelectionCriteria().name());
        int routesCount = (p.getPossiblePathwayRoutes()==null)? 0 : p.getPossiblePathwayRoutes().size();
        pwRoutesCountValue.setText(String.valueOf(routesCount));
        LOG.debug(".showPathwayDetails(): [Exit]");
    }

    private void clearPathwayDetails(){
        LOG.debug(".clearPathwayDetails(): [Entry]");
        pwIdValue.setText("");
        pwNameValue.setText("");
        pwDocValue.setText("");
        pwDistributionValue.setText("");
        pwRoutesCountValue.setText("");
        LOG.debug(".clearPathwayDetails(): [Exit]");
    }

    private void showFlowDetails(PathwayElement flow){
        LOG.debug(".showFlowDetails(): [Entry] flow={}", flow);
        if(flow==null){
            flowNameValue.setText("");
            flowDocValue.setText("");
            flowSourceValue.setText("");
            flowTargetValue.setText("");
            flowServiceValue.setText("");
            LOG.debug(".showFlowDetails(): [Exit] flow is null");
            return;
        }
        flowNameValue.setText(nz(flow.getShortName()));
        flowDocValue.setText(nz(flow.getDocumentation()));
        flowSourceValue.setText(idToString(flow.getSource().getElementIdentifier()));
        flowTargetValue.setText(idToString(flow.getTarget().getElementIdentifier()));
        if(flow.getUtilisedApplicationService() == null){
            flowServiceValue.setText("");
        } else {
            flowServiceValue.setText(idToString(flow.getUtilisedApplicationService().getElementIdentifier()));
        }
        LOG.debug(".showFlowDetails(): [Exit]");
    }

    private static String idToString(ElementIdentifier id){
        LOG.debug(".idToString(): [Entry] id={}", id);
        if(id==null){
            LOG.debug(".idToString(): [Exit] id is null");
            return "";
        }
        try {
            String s = String.valueOf(id.getIdentifierValue().getCommonName().getName());
            LOG.debug(".idToString(): [Exit] s={}", s);
            return s;
        } catch (Throwable t){
            String s = String.valueOf(id);
            LOG.debug(".idToString(): [Exit] fallback s={}", s);
            return s;
        }
    }

    private static String nz(String s){
        LOG.debug(".nz(): [Entry/Exit] s={}", s);
        return s==null? "" : s;
    }

    private String selectedPathwayId(){
        LOG.debug(".selectedPathwayId(): [Entry]");
        javafx.scene.control.TreeItem<Object> item = treeView.getSelectionModel().getSelectedItem();
        if(item==null){
            LOG.debug(".selectedPathwayId(): [Exit] no selection");
            return null;
        }
        Object v = item.getValue();
        if(v instanceof SegmentNode s){
            LOG.debug(".selectedPathwayId(): [Exit] pathwayId={}", s.pathwayId);
            return s.pathwayId;
        }
        if(v instanceof PathwayItem p){
            LOG.debug(".selectedPathwayId(): [Exit] id={}", p.id);
            return p.id;
        }
        LOG.debug(".selectedPathwayId(): [Exit] unknown type");
        return null;
    }

    private void onCreatePathway(){
        LOG.debug(".onCreatePathway(): [Entry]");
        javafx.scene.control.TextInputDialog nameDlg = new javafx.scene.control.TextInputDialog("");
        nameDlg.setTitle("New Pathway");
        nameDlg.setHeaderText("Enter a name for the new Pathway");
        nameDlg.setContentText("Name:");
        java.util.Optional<String> nameRes = nameDlg.showAndWait();
        if(nameRes.isEmpty() || nameRes.get().isBlank()){
            LOG.debug(".onCreatePathway(): [Exit] no name");
            return;
        }
        String name = nameRes.get().trim();
        LOG.trace(".onCreatePathway(): name={}", name);

        javafx.scene.control.TextInputDialog docDlg = new javafx.scene.control.TextInputDialog("");
        docDlg.setTitle("New Pathway");
        docDlg.setHeaderText("Optional documentation");
        docDlg.setContentText("Documentation:");
        String documentation = docDlg.showAndWait().orElse("");
        LOG.trace(".onCreatePathway(): documentation={}", documentation);

        Pathway p = new Pathway();
        p.setShortName(name);
        p.setDocumentation(documentation);
        // default selection criteria
        p.setRouteSelectionCriteria(PathwayRouteSelectionCriteriaEnum.DISTRIBUTE_RANDOM);
        DistinguishedName qn = new DistinguishedName();
        qn.appendUnqualifiedName(new RelativeDistinguishedName("Pathway", name));
        ElementIdentifier id = new ElementIdentifier(qn);
        p.setIdentifier(id);
        Pathway created = client.createPathway(p);
        LOG.trace(".onCreatePathway(): created={}", created);
        loadPathways();
        // Try to select created pathway
        if(created!=null){ selectPathway(created.resolveElementInstanceKey()); }
        LOG.debug(".onCreatePathway(): [Exit]");
    }

    private void onCreateRoute(){
        LOG.debug(".onCreateRoute(): [Entry]");
        String pwId = selectedPathwayId();
        if(pwId==null){
            LOG.debug(".onCreateRoute(): [Exit] no pathway selected");
            return;
        }
        LOG.trace(".onCreateRoute(): pwId={}", pwId);
        javafx.scene.control.TextInputDialog segDlg = new javafx.scene.control.TextInputDialog("StepA-StepB-StepC");
        segDlg.setTitle("New Pathway Route");
        segDlg.setHeaderText("Enter a label for the new Pathway Route (e.g., A-B-C)");
        segDlg.setContentText("Label:");
        java.util.Optional<String> res = segDlg.showAndWait();
        if(res.isEmpty() || res.get().isBlank()){
            LOG.debug(".onCreateRoute(): [Exit] no label");
            return;
        }
        String label = res.get().trim();
        LOG.trace(".onCreateRoute(): label={}", label);
        // Build route with ID based on parent pathway qualified name
        Pathway parent = client.getPathway(pwId);
        if(parent==null || parent.getIdentifier()==null){
            LOG.debug(".onCreateRoute(): [Exit] parent or identifier null");
            return;
        }
        DistinguishedName rq = new DistinguishedName(parent.getIdentifier().getIdentifierValue());
        rq.appendUnqualifiedName(new RelativeDistinguishedName("Segment", label));
        PathwayRoute route = new PathwayRoute();
        route.setShortName(label);
        ElementIdentifier id = new ElementIdentifier(rq);
        route.setIdentifier(id);
        PathwayRoute created = client.createPathwayRoute(pwId, route);
        LOG.trace(".onCreateRoute(): created={}", created);
        loadPathways();
        if(created!=null){ selectPathway(pwId); }
        LOG.debug(".onCreateRoute(): [Exit]");
    }

    private void onCreateSegment(){
        LOG.debug(".onCreateSegment(): [Entry]");
        javafx.scene.control.TreeItem<Object> item = treeView.getSelectionModel().getSelectedItem();
        if(item==null || !(item.getValue() instanceof SegmentNode)) {
            LOG.debug(".onCreateSegment(): [Exit] no route selected");
            return;
        }
        SegmentNode sn = (SegmentNode) item.getValue();
        LOG.trace(".onCreateSegment(): sn={}", sn);
        PathwayRoute route = sn.segment;
        java.util.List<PathwayElement> allElements = client.listPathwayElements();
        SegmentEditorDialog dlg = new SegmentEditorDialog(allElements, null);
        java.util.Optional<SegmentEditorDialog.Result> res = dlg.showAndWait();
        if(res.isEmpty()){
            LOG.debug(".onCreateSegment(): [Exit] cancelled");
            return;
        }
        SegmentEditorDialog.Result r = res.get();
        LOG.trace(".onCreateSegment(): result={}", r);
        // Build segment
        DistinguishedName base = new DistinguishedName(route.getIdentifier().getIdentifierValue());
        String label = r.label==null||r.label.isBlank()? ("Path-"+System.currentTimeMillis()) : r.label.trim();
        base.appendUnqualifiedName(new RelativeDistinguishedName("Path", label));
        PathwayRouteSegment seg = new PathwayRouteSegment();
        seg.setShortName(label);
        ElementIdentifier id = new ElementIdentifier(base);
        seg.setIdentifier(id);
        int i=1;
        for(PathwayElement pe : r.ordered){
            LOG.trace(".onCreateSegment(): adding pe={}", pe);
            if(pe!=null && pe.getIdentifier()!=null){ seg.getPathwayElementSequence().put(i++, pe.getReference()); }
        }
        PathwayRouteSegment saved = client.updatePathwayRouteSegment(seg);
        if(saved==null){
            LOG.debug(".onCreateSegment(): [Exit] save failed");
            return;
        }
        // Update parent route to include this segment
        if(route.getRouteSegmentSequence()==null){ route.setRouteSegmentSequence(new java.util.LinkedHashMap<>()); }
        int next = 1;
        if(!route.getRouteSegmentSequence().isEmpty()){
            next = new java.util.ArrayList<>(route.getRouteSegmentSequence().keySet()).stream().max(Integer::compareTo).orElse(0) + 1;
        }
        route.getRouteSegmentSequence().put(next, saved.getReference());
        client.updatePathwayRoute(route);
        loadPathways();
        selectPathway(sn.pathwayId);
        LOG.debug(".onCreateSegment(): [Exit]");
    }

    private void onEditSegment(){
        LOG.debug(".onEditSegment(): [Entry]");
        javafx.scene.control.TreeItem<Object> item = treeView.getSelectionModel().getSelectedItem();
        if(item==null || !(item.getValue() instanceof SegmentNode)) {
            LOG.debug(".onEditSegment(): [Exit] no segment selected");
            return;
        }
        SegmentNode sn = (SegmentNode) item.getValue();
        LOG.trace(".onEditSegment(): sn={}", sn);
        PathwayRoute route = sn.segment;
        if(route.getRouteSegmentSequence()==null || route.getRouteSegmentSequence().isEmpty()){
            LOG.debug(".onEditSegment(): [Exit] route sequence empty");
            return;
        }
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
            if(c.isEmpty()){
                LOG.debug(".onEditSegment(): [Exit] no choice");
                return;
            }
            chosenKey = c.get();
        }
        LOG.trace(".onEditSegment(): chosenKey={}", chosenKey);
        ElementReference segId = route.getRouteSegmentSequence().get(chosenKey);
        PathwayRouteSegment existing = client.getPathById(segId.getElementInstanceId());
        java.util.List<PathwayElement> allElements = client.listPathwayElements();
        SegmentEditorDialog dlg = new SegmentEditorDialog(allElements, existing);
        java.util.Optional<SegmentEditorDialog.Result> res = dlg.showAndWait();
        if(res.isEmpty()){
            LOG.debug(".onEditSegment(): [Exit] cancelled");
            return;
        }
        SegmentEditorDialog.Result r = res.get();
        LOG.trace(".onEditSegment(): result={}", r);
        // Update existing object (may change name/doc/elements; keep same ObjectID unless label changed)
        DistinguishedName qn = new DistinguishedName(existing.getIdentifier().getIdentifierValue());
        if(r.label!=null && !r.label.isBlank()){
            // Rebuild tail to use provided label
            // Remove last component by reconstructing from route base and appending Path/label
            DistinguishedName base = new DistinguishedName(route.getIdentifier().getIdentifierValue());
            base.appendUnqualifiedName(new RelativeDistinguishedName("Path", r.label.trim()));
            ElementIdentifier newIdentifier = new ElementIdentifier(base);
            existing.setIdentifier(newIdentifier);
        }
        existing.getPathwayElementSequence().clear();
        int i=1;
        for(PathwayElement pe : r.ordered){
            LOG.trace(".onEditSegment(): adding pe={}", pe);
            if(pe!=null && pe.getElementInstanceId()!=null){ existing.getPathwayElementSequence().put(i++, pe.getReference()); }
        }
        PathwayRouteSegment saved = client.updatePathwayRouteSegment(existing);
        if(saved==null){
            LOG.debug(".onEditSegment(): [Exit] save failed");
            return;
        }
        // If ID changed, update reference in route
        String newKey = saved.resolveElementInstanceKey();
        String oldKey = segId.getElementInstanceId().resolveKey();
        if(newKey!=null && oldKey!=null && !newKey.equals(oldKey)){
            LOG.trace(".onEditSegment(): key changed, updating route");
            route.getRouteSegmentSequence().put(chosenKey, saved.getReference());
            client.updatePathwayRoute(route);
        }
        loadPathways();
        selectPathway(sn.pathwayId);
        LOG.debug(".onEditSegment(): [Exit]");
    }

    private void onDeleteSelected(){
        LOG.debug(".onDeleteSelected(): [Entry]");
        javafx.scene.control.TreeItem<Object> item = treeView.getSelectionModel().getSelectedItem();
        if(item==null){
            LOG.debug(".onDeleteSelected(): [Exit] no selection");
            return;
        }
        Object v = item.getValue();
        LOG.trace(".onDeleteSelected(): v={}", v);
        if(v instanceof PathwayItem p){
            client.deletePathway(p.id);
            loadPathways();
        } else if (v instanceof SegmentNode s){
            String key = s.segment.resolveElementInstanceKey();
            if(key!=null){ client.deletePathwayRoute(key); }
            loadPathways();
            selectPathway(s.pathwayId);
        }
        LOG.debug(".onDeleteSelected(): [Exit]");
    }

    private void selectPathway(String id){
        LOG.debug(".selectPathway(): [Entry] id={}", id);
        if(treeView.getRoot()==null){
            LOG.debug(".selectPathway(): [Exit] no root");
            return;
        }
        for(javafx.scene.control.TreeItem<Object> pItem : treeView.getRoot().getChildren()){
            Object v = pItem.getValue();
            if(v instanceof PathwayItem p && p.id.equals(id)){
                LOG.trace(".selectPathway(): selecting pItem={}", pItem);
                treeView.getSelectionModel().select(pItem);
                treeView.scrollTo(treeView.getRow(pItem));
                LOG.debug(".selectPathway(): [Exit]");
                return;
            }
        }
        LOG.debug(".selectPathway(): [Exit] not found");
    }

    // Model wrappers for tree/table
    private static class PathwayItem {
        final String id; final String name; final String documentation;
        PathwayItem(String id, String name, String documentation){
            LOG.debug("PathwayItem(): [Entry]");
            this.id=id; this.name=name; this.documentation=documentation;
            LOG.debug("PathwayItem(): [Exit]");
        }
        @Override public String toString(){ return name==null||name.isBlank()? id : name; }
    }

    private static class SegmentNode {
        final String pathwayId; final Integer index; final PathwayRoute segment;
        SegmentNode(String pathwayId, Integer index, PathwayRoute segment){
            LOG.debug("SegmentNode(): [Entry]");
            this.pathwayId=pathwayId; this.index=index; this.segment=segment;
            LOG.debug("SegmentNode(): [Exit]");
        }
        @Override public String toString(){ return "Pathway Route "+index; }
    }

    public static class FlowRow {
        final PathwayElement flow;
        private final javafx.beans.property.SimpleStringProperty name = new javafx.beans.property.SimpleStringProperty("");
        private final javafx.beans.property.SimpleStringProperty source = new javafx.beans.property.SimpleStringProperty("");
        private final javafx.beans.property.SimpleStringProperty target = new javafx.beans.property.SimpleStringProperty("");
        private final javafx.beans.property.SimpleStringProperty service = new javafx.beans.property.SimpleStringProperty("");
        FlowRow(PathwayElement flow) {
            LOG.debug("FlowRow(): [Entry] flow={}", flow);
            this.flow = flow;
            this.name.set(nz(flow.getShortName()));
            this.source.set(idToString(flow.getSource().getElementIdentifier()));
            this.target.set(idToString(flow.getTarget().getElementIdentifier()));
            if (flow.getUtilisedApplicationService() != null) {
                this.service.set(idToString(flow.getUtilisedApplicationService().getElementIdentifier()));
            } else {
                this.service.set("");
            }
            LOG.debug("FlowRow(): [Exit]");
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
                LOG.debug("SegmentEditorDialog.Result(): [Entry]");
                this.label = label; this.ordered = ordered;
                LOG.debug("SegmentEditorDialog.Result(): [Exit]");
            }
        }

        SegmentEditorDialog(java.util.List<PathwayElement> allElements, PathwayRouteSegment existing){
            LOG.debug("SegmentEditorDialog(): [Entry] existing={}", existing);
            setTitle(existing==null? "New Pathway Route Segment" : "Edit Pathway Route Segment");
            getDialogPane().getButtonTypes().addAll(javafx.scene.control.ButtonType.OK, javafx.scene.control.ButtonType.CANCEL);

            // Render items as names
            availableList.setCellFactory(lv -> new javafx.scene.control.ListCell<>(){
                @Override protected void updateItem(PathwayElement item, boolean empty){
                    super.updateItem(item, empty);
                    if(empty || item==null){ setText(null); } else { setText(nz(item.getShortName())); }
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
                    String last = existing.getIdentifier().getIdentifierValue().getCommonName().getName();
                    labelField.setText(last);
                } catch (Exception ignored) {}
                // Preselect elements in order
                if(existing.getPathwayElementSequence()!=null){
                    java.util.List<Integer> keys = new java.util.ArrayList<>(existing.getPathwayElementSequence().keySet());
                    java.util.Collections.sort(keys);
                    for(Integer k: keys){
                        // lookup by id among allElements
                        ElementReference oid = existing.getPathwayElementSequence().get(k);
                        if(oid==null) continue;
                        for(PathwayElement pe: allElements){
                            try{
                                String a = pe.resolveElementInstanceKey();
                                String b = oid.getElementInstanceId().resolveKey();
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
            LOG.debug(".moveSelected(): [Entry] delta={}", delta);
            int idx = selectedList.getSelectionModel().getSelectedIndex();
            if(idx<0) {
                LOG.debug(".moveSelected(): [Exit] no selection");
                return;
            }
            int newIdx = idx + delta;
            if(newIdx<0 || newIdx>= selectedList.getItems().size()) {
                LOG.debug(".moveSelected(): [Exit] out of bounds");
                return;
            }
            PathwayElement item = selectedList.getItems().remove(idx);
            selectedList.getItems().add(newIdx, item);
            selectedList.getSelectionModel().select(newIdx);
            LOG.debug(".moveSelected(): [Exit]");
        }
    }
}
