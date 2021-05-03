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
