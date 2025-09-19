# dricats-platform

Configuration: Single Shared Task Cache with 3 Store Nodes (clustered via JGroups)

Store class split
- Use TaskInfinispanStore in applications that should NOT persist cache contents (non-persistent runtime cache only).
- Use TaskInfinispanPersistentStore in the 3 designated store applications that MUST persist ALL Task entries (via listeners and optional TaskPersistenceService or file-store).

This setup provides one shared cache for Task objects across many applications. Exactly three applications act as stores that persist ALL Task objects, while any number of other applications can read/write without persisting.

Core clustering properties
- -Dtask.store.cluster.name=dricats-task-cluster
- -Dtask.store.node.name=nodeA                # unique per node
- -Dtask.store.jgroups.config=classpath:udp.xml   # optional JGroups XML for cross-host discovery
- -Dtask.store.jgroups.stack=udp                 # optional stack name defined in the XML

Single shared cache
- -Dtask.store.cache.name=TaskResourceCache         # single cache name used by all nodes

Topology
- -Dtask.store.cache.mode=dist  # dist (distributed, recommended) or repl (replicated)
- -Dtask.store.cache.owners=2   # replication factor for distributed mode

Store vs Client roles
- -Dtask.store.role=store|client   # set to 'store' on exactly 3 nodes, 'client' everywhere else (default: client)
- -Dtask.store.persist.bootstrap=true|false  # on store nodes, persist all current cache entries during startup (default: true)

Optional local disk persistence (alternative to custom provider)
- -Dtask.store.persistence=file
- -Dtask.store.file.dir=/var/lib/dricats/task-cache

How it works
- All applications share the same clustered cache via JGroups. Writes from any node are visible to all.
- On nodes with role=store and a TaskPersistenceService CDI bean present, the store registers a clustered cache listener that receives create/modify/remove events from the entire cluster and persists them (ALL objects, regardless of which node wrote them).
- On startup (if enabled), store nodes also bootstrap by persisting the current cache contents.

Usage in code (same API for clients and stores)
- Default cache:
  @Inject TaskInfinispanClient client;
  String key = client.save(task);
  Optional<Task> t = client.get(key);

Notes
- For large clusters, prefer distributed mode (dist) with owners 2–3 to balance performance and redundancy.
- Only the three nodes with -Dtask.store.role=store will persist data. All other nodes can be stateless clients.
