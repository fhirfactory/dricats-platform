package net.fhirfactory.dricats.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainViewController {
    @FXML
    private TabPane tabPane;

    private String baseUrl;

    public void setBaseUrl(String baseUrl){
        this.baseUrl = baseUrl;
        // populate tabs after base URL is known
        initializeTabs();
    }

    private void initializeTabs(){
        if(tabPane == null){
            return;
        }
        tabPane.getTabs().clear();
        if(baseUrl == null || baseUrl.isBlank()){
            // fallback to default
            baseUrl = "http://localhost:12000";
        }
        tabPane.getTabs().addAll(
                new TopologyTab(baseUrl),
                new TaskingTab(),
                new RoutesTab(baseUrl)
        );
        // ensure tabs cannot be closed by user
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        // select first tab by default
        if(!tabPane.getTabs().isEmpty()){
            tabPane.getSelectionModel().select(0);
        }
    }
}
