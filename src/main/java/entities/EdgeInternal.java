package entities;

import common.Logger;
import database.Database;
import database.Instance;
import exceptions.EdgeException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class EdgeInternal {

  private static final Logger logger = Logger.getLogger();
  private final Map<UUID, EdgeDocument> documents;
  private final Map<UUID, ReentrantLock> locks;
  private final Database database;
  private final String relName;

  public EdgeInternal(String databaseName, String relName) {
    this.relName = relName;

    this.database = Instance.getInstance().getDatabase(databaseName);
    this.documents = new ConcurrentHashMap<>();
    this.locks = new HashMap<>();
  }

  public void insert(UUID uuid, EdgeDocument edgeDoc) throws EdgeException {
    // insert into documents map
    synchronized (documents) {
      if (!documents.containsKey(uuid)) {
        documents.put(uuid, edgeDoc);
      } else {
        throw new EdgeException("Document with key, " + uuid + ", already exists.");
      }
    }

    // Update NodeDocument index-free-adjacency
    // ToDo: Should this be part of edge participant? Rolled into transaction semantics
    try {
      NodeID originNode = edgeDoc.getOrigin();

      // These are direct writes to the document, and skirt the regular transaction model
      //  ToDo: This feels wrong here
      database
        .node(originNode.getNodeName())
        .getDocument(originNode.getUUID())
        .addRelationship(relName, new NodePtr(originNode, Direction.ORIGIN));

      NodeID destNode = edgeDoc.getDestination();
      database
        .node(destNode.getNodeName())
        .getDocument(destNode.getUUID())
        .addRelationship(relName, new NodePtr(destNode, Direction.DESTINATION));

    } catch (Exception e) {
      // ToDo: if we fail here, it's really bad, and we're in an inconsistent state.
      //  remove from documents? Do we need to synchronize entire operation?
      throw new EdgeException(e);
    }
  }

  public void update() {
    // ToDo: update documents map
    // ToDo: update IFA as needed
  }

  public EdgeDocument delete() {
    // ToDo: delete entry from documents map
    // ToDo: delete NodePtr from IFA
    return new EdgeDocument();
  }

  public EdgeDocument getDocument(UUID uuid) {
    return documents.get(uuid);
  }
}
