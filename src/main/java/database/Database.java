package database;

import common.Logger;
import database.engines.DBEngine;
import database.engines.InMemoryDatabase;
import database.engines.PortableDatabase;
import database.types.DBOptions;
import database.types.DBStatus;
import database.types.DBType;
import entities.Node;
import entities.Relationship;

import java.util.HashMap;
import java.util.Map;

public class Database implements AutoCloseable {

  private static final Logger logger = Logger.getLogger();

  private final String name;
  private final DBType storageType;
  private final Map<String, Node> nodes;
  private final Map<String, Relationship> rels;
  private DBStatus status;
  private final DBEngine engine;

  public Database(DBOptions options) {
    storageType = options.getStorageType();
    name = options.getName();
    status = DBStatus.OFFLINE;

    switch (storageType) {
      case PORTABLE:
        engine = new PortableDatabase();
        break;

      case IN_MEMORY:
      default:
        engine = new InMemoryDatabase();
        break;
    }

    nodes = new HashMap<String, Node>();
    rels = new HashMap<String, Relationship>();
  }

  public String getName() {
    return this.name;
  }

  public void start() {
    engine.start();

    if (status == DBStatus.OFFLINE) {
      status = DBStatus.RUNNING;
      logger.log("Database: '" + name + "' started.");
      logger.log("\tStorage Type: " + storageType);
    } else {
      logger.log("Database: '" + name + "' already running.");
    }
  }

  public Node node(String name) throws Exception {
    Node node = nodes.get(name);
    if (null == node)
      throw new Exception("node does not exist."); //ToDo: InGraphException

    return node;
  }

  public Relationship relationship(String name) throws Exception {
    Relationship rel = rels.get(name);
    if (null == rel)
      throw new Exception("relationship does not exist."); //ToDo: InGraphException

    return rel;
  }

//    public ResultSet query(String cypher) {
//    }

  @Override
  public void close() throws Exception {
    engine.stop();
  }
}
