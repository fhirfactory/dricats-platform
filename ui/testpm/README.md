# DRICaTS Middleware OAM UI Test (uitest)

This module provides a lightweight HTTP stub of the OAM (Operations, Administration, and Maintenance) API so the UI can be developed and tested without running the full OAM backend. It uses Apache Camel (camel-main + netty-http) and an in-memory service to return predictable data. It can also be built with an embedded WildFly (Bootable JAR) for environments that require a WildFly runtime inside the produced jar.

Prerequisites
- Java 11 or newer (module targets Java 11)
- Apache Maven 3.8+

How to start (Camel standalone)
- Recommended (targets the module POM directly):
  mvn -f middleware/oam/uitest/pom.xml -DskipTests exec:java@run-uitest

- Alternatively, from the uitest module directory:
  cd middleware/oam/uitest && mvn -DskipTests exec:java@run-uitest

- Note: Using reactor selection (-pl ... exec:java) from the repo root may try to run exec:java on the aggregator (root) project and fail with ClassNotFoundException. The commands above avoid that by targeting this module directly.

- By default the server listens on http://localhost:8080/
  You can change the port by supplying the Camel REST port property:
  mvn -f middleware/oam/uitest/pom.xml -DskipTests exec:java@run-uitest -Dcamel.rest.port=9090

Available endpoints
The REST context path is /. The base resource path is /oam.
- GET /oam/applicationcomponents
- GET /oam/applicationcomponents/{objectId}
- GET /oam/applicationcomponents/{objectId}/subcomponents
- GET /oam/metrics
- GET /oam/metrics/{objectId}

Example requests
- List components:
  curl -s http://localhost:8080/oam/applicationcomponents | jq .

- Get a component:
  curl -s http://localhost:8080/oam/applicationcomponents/comp-1 | jq .

- Get subcomponents of a component:
  curl -s http://localhost:8080/oam/applicationcomponents/comp-1/subcomponents | jq .

- Get metrics (range):
  curl -s "http://localhost:8080/oam/metrics?start=2025-01-01T00:00:00Z&end=2025-01-01T23:59:59Z" | jq .

- Get latest metrics for a component:
  curl -s http://localhost:8080/oam/metrics/comp-1 | jq .

Notes
- This module is named dricats-middleware-oam-uitest (not jgroups). If your issue references a jgroups uitest, this is the UI test stub for OAM available in this repository.
- The Undertow HTTP server and JSON binding (Jackson) are configured via Camel Rest DSL inside the code. No external application server is required.
- API doc path /api-doc is configured in code; generating Swagger/OpenAPI may require adding the Camel OpenAPI dependency if you need interactive docs.

Run with embedded WildFly (Bootable JAR)
- Build the bootable JAR for this module:
  mvn -f middleware/oam/ui/testpm/pom.xml -Pbootable-jar -DskipTests wildfly-jar:package
- Run the embedded WildFly from the produced bootable jar:
  java -jar middleware/oam/ui/testpm/target/dricats-middleware-oam-ui-testpm-bootable.jar
- Notes:
  - This starts an embedded WildFly instance (hollow) suitable for integration testing where a WildFly runtime is required inside the JAR. The UI test service itself still runs via the Camel Main entrypoint when using the standalone mode above.
  - To change the WildFly HTTP port, pass -Djboss.http.port=9090 when running the bootable jar.

Logging
- The UI test stub now emits detailed logs for lifecycle events, REST route invocations, and service operations.
- To see debug logs with slf4j-simple, run with: -Dorg.slf4j.simpleLogger.defaultLogLevel=debug
  Example: mvn -f middleware/oam/uitest/pom.xml -DskipTests exec:java@run-uitest -Dorg.slf4j.simpleLogger.defaultLogLevel=debug
