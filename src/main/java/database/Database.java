package database;

import common.Logger;
import database.engines.DBEngine;
import database.engines.InMemoryDatabase;
import database.engines.PortableDatabase;
import database.types.DBOptions;
import database.types.DBStatus;
import database.types.DBType;
import entities.Node;
import entities.Edge;
import exceptions.InGraphDBException;

import java.util.HashMap;
import java.util.Map;

public class Database implements AutoCloseable {

  private static final Logger logger = Logger.getLogger();

  private final String name;
  private final DBType storageType;
  private final Map<String, Node> nodes;
  private final Map<String, Edge> rels;
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
    rels = new HashMap<String, Edge>();
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

  public Node createNode(String name) throws InGraphDBException {
    synchronized (nodes) {
      if (nodes.containsKey(name))
        throw new InGraphDBException("node already exists.");

      nodes.put(name, new Node(name));
      return nodes.get(name);
    }
  }

  public Node node(String name) throws InGraphDBException {
    Node node = nodes.get(name);
    if (null == node)
      throw new InGraphDBException("node does not exist.");

    return node;
  }

  public Edge createRelationship(String relName) throws InGraphDBException {
    synchronized (rels) {
      if (rels.containsKey(relName))
        throw new InGraphDBException("edge already exists.");

      rels.put(relName, new Edge(this.name, relName));
      return rels.get(relName);
    }
  }

  public Edge relationship(String name) throws InGraphDBException {
    Edge rel = rels.get(name);
    if (null == rel)
      throw new InGraphDBException("relationship does not exist.");

    return rel;
  }

//    public ResultSet query(String cypher) {
//    }

  @Override
  public void close() throws Exception {
    engine.stop();
  }
}
