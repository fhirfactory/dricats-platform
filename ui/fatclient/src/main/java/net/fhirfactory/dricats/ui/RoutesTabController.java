package net.fhirfactory.dricats.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoutesTabController {
    private static final Logger LOG = LoggerFactory.getLogger(RoutesTabController.class);
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
        LOG.debug(".setBaseUrl(): [Entry] baseUrl={}", baseUrl);
        this.baseUrl = baseUrl;
        // TODO: wire up real behavior; placeholder for FXML-based layout
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> {
                LOG.debug(".setBaseUrl().onAction(): [Entry]");
                // placeholder: no-op
                LOG.debug(".setBaseUrl().onAction(): [Exit]");
            });
        }
        LOG.debug(".setBaseUrl(): [Exit]");
    }
}
