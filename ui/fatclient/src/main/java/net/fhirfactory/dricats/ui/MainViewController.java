package net.fhirfactory.dricats.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MainViewController {
    @FXML
    private TabPane tabPane;

    // Access to included controllers via fx:include fx:id + "Controller" convention
    @FXML private TopologyTabController topologyTabController;
    @FXML private TaskingTabController taskingTabController;
    @FXML private RoutesTabController routesTabController;

    private String baseUrl;

    public void setBaseUrl(String baseUrl){
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
    }

    @FXML
    private void initialize(){
        // If baseUrl was set before initialize, propagate now
        propagateBaseUrl();
        if (tabPane != null) {
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        }
    }

    private void propagateBaseUrl(){
        if (this.baseUrl == null || this.baseUrl.isBlank()) {
            return;
        }
        if (topologyTabController != null) {
            topologyTabController.setBaseUrl(this.baseUrl);
        }
        if (routesTabController != null) {
            routesTabController.setBaseUrl(this.baseUrl);
        }
        if (taskingTabController != null) {
            // optional for now; keeps API consistent
            try {
                taskingTabController.setBaseUrl(this.baseUrl);
            } catch (NoSuchMethodError ignored) { /* older controller without setBaseUrl */ }
        }
    }
}
