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

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

public class RoutesTab extends Tab {
    private final OamRestClient client;
    private javafx.scene.control.ListView<PathwayItem> pathwayList;
    private javafx.scene.control.TableView<SegmentRow> segmentTable;

    public RoutesTab() {
        this("http://localhost:8080");
    }

    public RoutesTab(String baseUrl) {
        super("Routes");
        this.client = new OamRestClient(baseUrl);
        setClosable(false);
        setContent(buildContent());
        loadPathways();
    }

    private BorderPane buildContent() {
        // Left: pathways list with refresh
        javafx.scene.control.Button refreshBtn = new javafx.scene.control.Button("Refresh");
        refreshBtn.setOnAction(e -> loadPathways());
        pathwayList = new javafx.scene.control.ListView<>();
        pathwayList.setCellFactory(lv -> new javafx.scene.control.ListCell<>(){
            @Override protected void updateItem(PathwayItem item, boolean empty){
                super.updateItem(item, empty);
                setText(empty || item==null ? null : (item.name==null||item.name.isBlank()? item.id : item.name));
            }
        });
        pathwayList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if(newV!=null){ loadSegments(newV.id); }
            else { segmentTable.getItems().clear(); }
        });
        javafx.scene.layout.VBox left = new javafx.scene.layout.VBox(6, new javafx.scene.control.Label("Pathways"), refreshBtn, pathwayList);
        javafx.scene.layout.VBox.setVgrow(pathwayList, javafx.scene.layout.Priority.ALWAYS);
        left.setPadding(new Insets(8));

        // Right: segments table
        segmentTable = new javafx.scene.control.TableView<>();
        javafx.scene.control.TableColumn<SegmentRow,String> idxCol = new javafx.scene.control.TableColumn<>("#");
        idxCol.setCellValueFactory(c -> c.getValue().indexProperty());
        idxCol.setPrefWidth(50);
        javafx.scene.control.TableColumn<SegmentRow,String> distCol = new javafx.scene.control.TableColumn<>("Distribution");
        distCol.setCellValueFactory(c -> c.getValue().distributionProperty());
        distCol.setPrefWidth(180);
        javafx.scene.control.TableColumn<SegmentRow,String> flowsCol = new javafx.scene.control.TableColumn<>("Flows");
        flowsCol.setCellValueFactory(c -> c.getValue().flowsProperty());
        flowsCol.setPrefWidth(120);
        segmentTable.getColumns().setAll(idxCol, distCol, flowsCol);
        segmentTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        javafx.scene.layout.VBox right = new javafx.scene.layout.VBox(6, new javafx.scene.control.Label("Segments"), segmentTable);
        javafx.scene.layout.VBox.setVgrow(segmentTable, javafx.scene.layout.Priority.ALWAYS);
        right.setPadding(new Insets(8));

        BorderPane pane = new BorderPane();
        pane.setLeft(left);
        pane.setCenter(right);
        return pane;
    }

    private void loadPathways(){
        java.util.List<java.util.Map<String,Object>> list = client.listPathways();
        javafx.collections.ObservableList<PathwayItem> items = javafx.collections.FXCollections.observableArrayList();
        for(java.util.Map<String,Object> m: list){
            try {
                String id = String.valueOf(m.getOrDefault("id",""));
                String name = (m.get("name")==null? id : String.valueOf(m.get("name")));
                String doc = m.get("documentation")==null? "" : String.valueOf(m.get("documentation"));
                items.add(new PathwayItem(id, name, doc));
            } catch (Exception ignored) {}
        }
        pathwayList.setItems(items);
        if(!items.isEmpty()) pathwayList.getSelectionModel().selectFirst();
    }

    private void loadSegments(String pathwayId){
        java.util.Map<Integer, net.fhirfactory.dricats.internals.topology.implementation.relationships.datatypes.ProcessingPathwaySegment> segs = client.getPathwaySegments(pathwayId);
        javafx.collections.ObservableList<SegmentRow> rows = javafx.collections.FXCollections.observableArrayList();
        java.util.List<Integer> keys = new java.util.ArrayList<>(segs.keySet());
        java.util.Collections.sort(keys);
        for(Integer k: keys){
            net.fhirfactory.dricats.internals.topology.implementation.relationships.datatypes.ProcessingPathwaySegment s = segs.get(k);
            String dist = s==null || s.getDistributionPattern()==null ? "" : s.getDistributionPattern().name();
            int count = s==null || s.getProcessingPathwaySegmentPathways()==null ? 0 : s.getProcessingPathwaySegmentPathways().size();
            rows.add(new SegmentRow(String.valueOf(k), dist, String.valueOf(count)));
        }
        segmentTable.setItems(rows);
    }

    private static class PathwayItem {
        final String id; final String name; final String documentation;
        PathwayItem(String id, String name, String documentation){ this.id=id; this.name=name; this.documentation=documentation; }
        @Override public String toString(){ return name==null||name.isBlank()? id : name; }
    }

    public static class SegmentRow {
        private final javafx.beans.property.SimpleStringProperty index = new javafx.beans.property.SimpleStringProperty("");
        private final javafx.beans.property.SimpleStringProperty distribution = new javafx.beans.property.SimpleStringProperty("");
        private final javafx.beans.property.SimpleStringProperty flows = new javafx.beans.property.SimpleStringProperty("");
        public SegmentRow(String index, String distribution, String flows){ this.index.set(index); this.distribution.set(distribution); this.flows.set(flows); }
        public javafx.beans.property.StringProperty indexProperty(){ return index; }
        public javafx.beans.property.StringProperty distributionProperty(){ return distribution; }
        public javafx.beans.property.StringProperty flowsProperty(){ return flows; }
    }
}
