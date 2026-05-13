# DRICaTS UI JavaFX Client

This module provides a JavaFX desktop application to browse DRICaTS OAM (Operations, Administration, and Maintenance) topology and view live metrics from the OAM REST API.

It is intended to be used against a running OAM backend or the Test Presentation Manager (TestPM) stub.

## Prerequisites
- Java 21 (or newer)
- Apache Maven 3.8+
- A running OAM REST endpoint.

## Build
From the repository root, build this module and its dependencies:
```bash
mvn -pl ui/fatclient -am -DskipTests package
```

This produces a runnable "fat jar" in:
`ui/fatclient/target/dricats-ui-fatclient-1.0.0-SNAPSHOT.jar`

## Run the UI

### Option A: Run the jar
After building, run:
```bash
java -jar ui/fatclient/target/dricats-ui-fatclient-1.0.0-SNAPSHOT.jar
```

### Option B: Run from your IDE
- Set the main class to: `net.fhirfactory.dricats.ui.AdministrationUILauncher`
- Ensure you have JavaFX runtime available (the project uses JavaFX 20.0.2).

## Connecting to a Backend
When the app starts, it will prompt for the OAM Server Base URL.
- Default: `http://localhost:12000` (Typical for TestPM)

## Starting the Test Presentation Manager (TestPM)
If you don't have a full OAM backend running, you can use the `ui-testpm` module.

From the repository root:
```bash
mvn -pl ui/testpm -am -DskipTests package
java -jar ui/testpm/target/dricats-ui-testpm-bootable.jar
```

## Logging
To see debug/verbose logs, run with:
```bash
java -Dorg.slf4j.simpleLogger.defaultLogLevel=debug -jar ui/fatclient/target/dricats-ui-fatclient-1.0.0-SNAPSHOT.jar
```

