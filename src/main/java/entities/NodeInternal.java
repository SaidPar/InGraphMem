package entities;

import exceptions.NodeException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public final class NodeInternal {

  private final Map<UUID, Document> documents;
  private final Map<UUID, ReentrantLock> locks;

  public NodeInternal() {
    documents = new ConcurrentHashMap<>();
    locks = new HashMap<>();
  }

  public void insert(UUID uuid, Document insertDoc) throws NodeException {
    synchronized (documents) {
      if (!documents.containsKey(uuid)) {
        documents.put(uuid, insertDoc);
      } else {
        throw new NodeException("Document with key, " + uuid + ", already exists.");
      }
    }
  }

  public void update(UUID updateUUID, Document updateProps) {
    documents.replace(updateUUID, updateProps);
  }

  public Document delete(UUID deleteUUID) {
    Document deleteDoc;
    synchronized (documents) {
      deleteDoc = documents.get(deleteUUID);
      documents.remove(deleteUUID);
    }

    return deleteDoc;
  }

  public Document getDocument(UUID uuid) {
    return documents.get(uuid);
  }

  public void releaseLock(UUID uuid) {
    if (locks.get(uuid) != null) {
      locks.get(uuid).unlock();
      // ToDo: Logger fine level
      System.out.println(Thread.currentThread().getName() + ": released lock on " + uuid);
    }

  }

  public void takeLock(UUID uuid, Long timeOutSecs) throws NodeException {

    ReentrantLock lock;
    synchronized (locks) {
      lock = locks.get(uuid);
      if (null == lock) {
        lock = new ReentrantLock();
        locks.put(uuid, lock);
      }
    }

    try {
      if (lock.tryLock() || lock.tryLock(timeOutSecs, TimeUnit.SECONDS)) {
        // ToDo: Logger fine level
        System.out.println(Thread.currentThread().getName() + ": took lock on " + uuid);
      } else {
        throw new NodeException("Unable to acquire lock.");
      }
    } catch (InterruptedException e) {
      throw new NodeException("Unable to acquire lock.");
    }
  }
}
