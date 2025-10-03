# DRICaTS Middleware OAM UI (JavaFX)

This module provides a simple JavaFX desktop application to browse DRICaTS OAM (Operations, Administration, and Maintenance) topology and view live metrics from the OAM REST API.

It is intended to be used against a running OAM backend. For local development you can run it against the lightweight OAM UI test stub included in this repository.


Prerequisites
- Java 17 (or newer)
- Apache Maven 3.8+
- A running OAM REST endpoint. For local development you can start the uitest stub (see below).


Build
- From the repository root, build just this module and its dependencies:
  mvn -pl middleware/oam/ui -am -DskipTests package

  This produces a runnable “fat jar” (shaded) in:
  middleware/oam/ui/target/


Run the UI
Option A: Run the shaded jar (recommended)
- After building, run:
  java -jar middleware/oam/ui/target/dricats-middleware-oam-ui-1.0.0-SNAPSHOT-shaded.jar

  Notes:
  - The shaded jar bundles JavaFX dependencies so you don’t need to pass --module-path flags.
  - If your platform JDK has issues launching JavaFX, ensure you’re using a standard OpenJDK 17+ build. Azul Zulu, Liberica, or Temurin are commonly used.

Option B: Run from your IDE
- Set the main class to:
  net.fhirfactory.dricats.ui.Launcher
- Run normally. The app is a standard JavaFX Application (see MainApp).


Pointing the UI at an OAM backend
- When the app starts it defaults to: http://localhost:8080
- You can change the Base URL at the top of the window and click Reload to:
  - Refresh the topology tree
  - Refresh the metrics panel for the selected component


Starting a local OAM stub (uitest)
If you don’t have a full OAM backend running, use the uitest module included here. It provides predictable data over HTTP.

- From the repository root (recommended):
  mvn -f middleware/oam/uitest/pom.xml -DskipTests exec:java@run-uitest

- Alternatively, from the module directory:
  cd middleware/oam/uitest && mvn -DskipTests exec:java@run-uitest

- By default it listens on http://localhost:8080
  To change the port (example 9090):
  mvn -f middleware/oam/uitest/pom.xml -DskipTests exec:java@run-uitest -Dcamel.rest.port=9090

Then set the UI Base URL to match (e.g., http://localhost:9090) and click Reload.


Logging
- The UI now logs API activities (requests to the OAM REST API) including HTTP method, URL, status code, duration, and payload size.
- To see debug/verbose logs with slf4j-simple, run Java with:
  -Dorg.slf4j.simpleLogger.defaultLogLevel=debug
  Example:
  java -Dorg.slf4j.simpleLogger.defaultLogLevel=debug -jar middleware/oam/ui/target/dricats-middleware-oam-ui-1.0.0-SNAPSHOT-shaded.jar
- You can also filter specific loggers:
  -Dorg.slf4j.simpleLogger.log.net.fhirfactory.dricats.middleware.oam.ui=debug

Troubleshooting
- "JavaFX runtime components are missing": Ensure you run the shaded jar built by Maven, or if running non-shaded output, add JavaFX modules on the module-path for your platform. The shaded jar avoids this on most systems.
- "ClassNotFoundException when running from repo root with exec": The UI module does not define an exec:java run profile. Use the shaded jar approach above, or run from your IDE using the Launcher class.


Project layout
- Main JavaFX application: src/main/java/net/fhirfactory/dricats/middleware/oam/ui/MainApp.java
- Entry point (for fat-jar/IDE): src/main/java/net/fhirfactory/dricats/middleware/oam/ui/Launcher.java
- Maven coordinates: artifactId dricats-middleware-oam-ui

