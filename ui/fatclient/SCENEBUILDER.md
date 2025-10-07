How to open FXML in SceneBuilder for the dricats-ui-fatclient module

This module has been refactored to be FXML‑based. You can open and edit the layouts in SceneBuilder directly from IntelliJ IDEA.

Prerequisites
- IntelliJ IDEA (Community or Ultimate)
- Gluon SceneBuilder installed locally (https://gluonhq.com/products/scene-builder/)
- JDK 17+ installed (project uses Java 17)

Where the FXML files live
- Main window: ui/fatclient/src/main/resources/net/fhirfactory/dricats/ui/MainView.fxml
- Topology tab: ui/fatclient/src/main/resources/net/fhirfactory/dricats/ui/TopologyTab.fxml
- Routes tab: ui/fatclient/src/main/resources/net/fhirfactory/dricats/ui/RoutesTab.fxml (if present)
- Tasking tab: ui/fatclient/src/main/resources/net/fhirfactory/dricats/ui/TaskingTab.fxml (if present)

Controller classes
- MainView.fxml → net.fhirfactory.dricats.ui.MainViewController
- TopologyTab.fxml → net.fhirfactory.dricats.ui.TopologyTabController
- RoutesTab.fxml → net.fhirfactory.dricats.ui.RoutesTabController

Step‑by‑step: configure IntelliJ to use SceneBuilder
1) Open the project in IntelliJ and ensure Maven has imported the ui/fatclient module.
   - Dependencies for JavaFX (javafx-controls, javafx-fxml) are already declared in ui/fatclient/pom.xml.
   - If IDEA shows a Maven import notification, click “Load/Import Changes”.
2) Set the SceneBuilder path:
   - File → Settings → Languages & Frameworks → JavaFX
   - Set “Path to Scene Builder” to your SceneBuilder executable, e.g.:
     • macOS: /Applications/SceneBuilder.app
     • Windows: C:\Program Files\SceneBuilder\SceneBuilder.exe
     • Linux: /opt/scenebuilder/bin/SceneBuilder (or wherever you installed it)
3) Open any FXML file (e.g., MainView.fxml). You should see an “Open in SceneBuilder” button in the editor toolbar or a context menu action when right‑clicking the file.
4) Click “Open in SceneBuilder” and edit the layout visually. Save from SceneBuilder to write back into the same FXML file in src/main/resources.

Notes and tips
- If the “Open in SceneBuilder” action does not appear:
  • Make sure the file extension is .fxml and the file is inside a resources folder (it is under src/main/resources already).
  • Verify Step 2: the SceneBuilder path is correctly configured in IDEA settings.
  • Re‑import Maven (right‑click the root pom.xml → Maven → Reload Project) so controllers are on the project classpath.
- If SceneBuilder warns about missing controller classes:
  • Build the project (Ctrl/Cmd+F9) or run mvn -q -pl ui/fatclient -am package to compile.
  • Confirm that the fx:controller values in FXML match these fully‑qualified class names:
    - net.fhirfactory.dricats.ui.MainViewController
    - net.fhirfactory.dricats.ui.TopologyTabController
    - net.fhirfactory.dricats.ui.RoutesTabController
- Previewing without running the full app:
  • In SceneBuilder, you can set Controller → Controller Class to match the FXML and use Preview to validate layout.
  • Controller code isn’t required for basic preview, but it helps with fx:id injection checks.
- Running the app to see changes live:
  • From IDEA: run net.fhirfactory.dricats.ui.AdministrationUILauncher (or FatClientMainApp) after building.
  • Or use Maven: mvn -q -pl ui/fatclient -am package and run the shaded jar produced by the module.

Troubleshooting
- “Class not found” in SceneBuilder:
  • Ensure the module compiles and that IDEA project SDK is set to JDK 17 (File → Project Structure → Project → SDK and Language level).
  • Re-open the FXML after a successful build.
- Missing JavaFX runtime when running the app:
  • This project uses Maven dependencies for JavaFX modules. Runtime module launching is handled by the shaded jar and/or your run configuration. SceneBuilder itself doesn’t require JavaFX on your system beyond SceneBuilder’s own installation.
- FXML won’t open at all:
  • Try opening SceneBuilder first, then File → Open and navigate to the FXML path listed above.

That’s it! Once configured, you can right‑click any FXML in the ui/fatclient module and select “Open in SceneBuilder” to edit the UI visually.