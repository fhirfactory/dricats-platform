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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.fhirfactory.dricats.internals.common.naming.datatypes.DistinguishedNameEntry;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.datatypes.ComponentMessagingStatistics;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.ui.restclient.MainRESTClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class AdministrationUI extends Application {
    private static final Logger LOG = LoggerFactory.getLogger(AdministrationUI.class);

    private MainRESTClient client;
    private TreeView<ApplicationComponent> treeView;
    private TableView<KVRow> metricsTable;
    private TableView<KVRow> detailsTable;
    private TreeView<String> uniqueNameTree;
    private Button refreshMetricsBtn;

    @Override
    public void start(Stage stage) {
        LOG.debug(".start(): [Entry]");
        // Prompt for server URL before showing main window
        TextInputDialog urlDialog = new TextInputDialog("http://localhost:12000");
        urlDialog.setTitle("Connect to Server");
        urlDialog.setHeaderText("Enter OAM Server Base URL");
        urlDialog.setContentText("Base URL:");
        java.util.Optional<String> urlResult = urlDialog.showAndWait();
        if (urlResult.isEmpty() || urlResult.get() == null || urlResult.get().isBlank()) {
            LOG.info("[UI] No server URL provided. Exiting application.");
            Platform.exit();
            return;
        }
        String baseUrl = urlResult.get().trim();
        client = new MainRESTClient(baseUrl);

        // Load FXML-based UI
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/net/fhirfactory/dricats/ui/MainView.fxml")
            );
            javafx.scene.Parent root = loader.load();
            MainViewController controller = loader.getController();
            if (controller != null) {
                controller.setBaseUrl(baseUrl);
            }
            stage.setTitle("DRICaTS UI");
            stage.setScene(new Scene(root, 1100, 600));
            stage.show();
        } catch (Exception e) {
            LOG.error("[UI] Failed to load FXML, falling back to programmatic UI", e);
            // Fallback: Build multi-tab UI (OAM, tasking, routes)
            TabPane tabPane = new TabPane();
            tabPane.getTabs().addAll(
                    new TopologyTab(baseUrl),
                    new TaskingTab(),
                    new RoutesTab(baseUrl)
            );
            stage.setTitle("DRICaTS UI");
            stage.setScene(new Scene(tabPane, 1100, 600));
            stage.show();
        }
        LOG.debug(".start(): [Exit]");
        return;
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

    private String formatMetrics(ApplicationComponentMetricsData md) {
        LOG.debug(".formatMetrics(): [Entry] md={}", md);
        StringBuilder sb = new StringBuilder();
        sb.append("Component: ").append(Objects.toString(md.getParticipantName(), ""));
        if (md.getComponentType() != null) sb.append(" (type=").append(md.getComponentType()).append(")");
        sb.append('\n');
        Instant startup = md.getComponentStartupInstant();
        Instant last = md.getLastActivityInstant();
        if (startup != null) sb.append("Startup: ").append(startup).append('\n');
        if (last != null) sb.append("Last Activity: ").append(last).append('\n');
        if (md.getComponentStatus() != null) sb.append("Status: ").append(md.getComponentStatus()).append('\n');
        sb.append('\n');
        ComponentMessagingStatistics ms = md.getMessagingStatistics();
        if (ms != null) {
            sb.append("Messaging:\n");
            sb.append("  Ingress: ").append(ms.getIngresMessageCount()).append('\n');
            sb.append("  Egress Attempts: ").append(ms.getEgressMessageAttemptCount()).append('\n');
            sb.append("  Egress Success: ").append(ms.getEgressMessageSuccessCount()).append('\n');
            sb.append("  Egress Failures: ").append(ms.getEgressMessageFailureCount()).append('\n');
            sb.append("  Internal Sent: ").append(ms.getInternalDistributedMessageCount()).append('\n');
            sb.append("  Internal Received: ").append(ms.getInternalReceivedMessageCount()).append('\n');
        }
        LOG.debug(".formatMetrics(): [Exit]");
        return sb.toString();
    }

    private void loadRoots() {
        LOG.debug(".loadRoots(): [Entry]");
        TreeItem<ApplicationComponent> hiddenRoot = new TreeItem<>();
        treeView.setRoot(hiddenRoot);
        List<ApplicationComponent> roots = null;
        try {
            roots = client.listComponents();
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
            LOG.trace(".loadRoots(): adding component={}", s);
            if (s != null) {
                hiddenRoot.getChildren().add(createTreeItem(s));
            }
        }
        LOG.debug(".loadRoots(): [Exit]");
    }

    private TreeItem<ApplicationComponent> createTreeItem(ApplicationComponent s) {
        LOG.debug(".createTreeItem(): [Entry] s={}", s);
        TreeItem<ApplicationComponent> item = new TreeItem<>(s);
        // Add a dummy child to show expandable arrow; real children loaded on demand
        item.getChildren().add(new TreeItem<>());
        item.expandedProperty().addListener((obs, o, n) -> {
            if (n) loadChildrenIfNeeded(item);
        });
        LOG.debug(".createTreeItem(): [Exit]");
        return item;
    }

    private void loadChildrenIfNeeded(TreeItem<ApplicationComponent> parentItem) {
        LOG.debug(".loadChildrenIfNeeded(): [Entry] parentItem={}", parentItem);
        if (parentItem == null || parentItem.getValue() == null) {
            LOG.debug(".loadChildrenIfNeeded(): [Exit] parentItem or value is null");
            return;
        }
        // If already loaded (no placeholder), skip
        if (!hasPlaceholder(parentItem)) {
            LOG.debug(".loadChildrenIfNeeded(): [Exit] already loaded");
            return;
        }
        parentItem.getChildren().clear();

        String id = MainRESTClient.resolveKey(parentItem.getValue());
        List<ApplicationComponent> kids = client.listSubcomponents(id);
        if (kids.isEmpty()) {
            LOG.debug(".loadChildrenIfNeeded(): [Exit] no children");
            // keep no children
            return;
        }
        LOG.trace(".loadChildrenIfNeeded(): kids={}", kids);
        for (ApplicationComponent child : kids) {
            parentItem.getChildren().add(createTreeItem(child));
        }
        LOG.debug(".loadChildrenIfNeeded(): [Exit]");
    }

    private boolean hasPlaceholder(TreeItem<ApplicationComponent> item) {
        LOG.debug(".hasPlaceholder(): [Entry]");
        boolean has = item.getChildren().size() == 1 && item.getChildren().get(0).getValue() == null;
        LOG.debug(".hasPlaceholder(): [Exit] has={}", has);
        return has;
    }

    @Override
    public void stop() throws Exception {
        LOG.debug(".stop(): [Entry]");
        super.stop();
        Platform.exit();
        LOG.debug(".stop(): [Exit]");
    }


    public static void main(String[] args) {
        LOG.debug(".main(): [Entry]");
        launch(args);
        LOG.debug(".main(): [Exit]");
    }
}
