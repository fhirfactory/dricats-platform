package net.fhirfactory.dricats.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainViewController {
    private static final Logger LOG = LoggerFactory.getLogger(MainViewController.class);
    @FXML
    private TabPane tabPane;

    // Access to included controllers via fx:include fx:id + "Controller" convention
    @FXML private TopologyTabController topologyTabController;
    @FXML private TaskingTabController taskingTabController;
    @FXML private RoutesTabController routesTabController;

    private String baseUrl;

    public void setBaseUrl(String baseUrl){
        LOG.debug(".setBaseUrl(): [Entry] baseUrl={}", baseUrl);
        this.baseUrl = (baseUrl == null || baseUrl.isBlank()) ? "http://localhost:12000" : baseUrl.trim();
        // forward baseUrl to children once injected
        propagateBaseUrl();
        // ensure tabs cannot be closed by user and select first
        if (tabPane != null) {
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            if (!tabPane.getTabs().isEmpty()) {
                tabPane.getSelectionModel().select(0);
            }
        }
        LOG.debug(".setBaseUrl(): [Exit]");
    }

    @FXML
    private void initialize(){
        LOG.debug(".initialize(): [Entry]");
        // If baseUrl was set before initialize, propagate now
        propagateBaseUrl();
        if (tabPane != null) {
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        }
        LOG.debug(".initialize(): [Exit]");
    }

    private void propagateBaseUrl(){
        LOG.debug(".propagateBaseUrl(): [Entry]");
        if (this.baseUrl == null || this.baseUrl.isBlank()) {
            LOG.debug(".propagateBaseUrl(): [Exit] baseUrl is null or blank");
            return;
        }
        if (topologyTabController != null) {
            LOG.trace(".propagateBaseUrl(): propagating to topologyTabController");
            topologyTabController.setBaseUrl(this.baseUrl);
        }
        if (routesTabController != null) {
            LOG.trace(".propagateBaseUrl(): propagating to routesTabController");
            routesTabController.setBaseUrl(this.baseUrl);
        }
        if (taskingTabController != null) {
            // optional for now; keeps API consistent
            try {
                LOG.trace(".propagateBaseUrl(): propagating to taskingTabController");
                taskingTabController.setBaseUrl(this.baseUrl);
            } catch (NoSuchMethodError ignored) { /* older controller without setBaseUrl */ }
        }
        LOG.debug(".propagateBaseUrl(): [Exit]");
    }
}
