package entities;

import common.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class EdgeInternal {

  private static final Logger logger = Logger.getLogger();
  private final Map<UUID, Document> documents;
  private final Map<UUID, ReentrantLock> locks;

  public EdgeInternal() {
    documents = new ConcurrentHashMap<>();
    locks = new HashMap<>();
  }

  public void insert() {
    // ToDo: insert into documents map
    // ToDo: update NodeDocument index-free-adjancency
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
}
