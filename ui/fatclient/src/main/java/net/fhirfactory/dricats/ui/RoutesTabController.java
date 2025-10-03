package net.fhirfactory.dricats.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RoutesTabController {
    @FXML private Button refreshButton;
    @FXML private TreeView<Object> treeView;
    @FXML private Label segIndexValue;
    @FXML private Label segDistributionValue;
    @FXML private Label segFlowsCountValue;
    @FXML private TableView<?> flowsTable;
    @FXML private TableColumn<?, ?> flowColType;
    @FXML private TableColumn<?, ?> flowColName;
    @FXML private TableColumn<?, ?> flowColId;
    @FXML private Label flowKindValue;
    @FXML private Label flowNameValue;
    @FXML private Label flowIdValue;
    @FXML private Label flowDocValue;

    private String baseUrl;

    public void setBaseUrl(String baseUrl){
        this.baseUrl = baseUrl;
        // TODO: wire up real behavior; placeholder for FXML-based layout
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> {
                // placeholder: no-op
            });
        }
    }
}
