package entities;

import common.Logger;
import exceptions.NodeException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The internal node representation. this class is a managing container for all documents within
 * a named node.
 */
public final class NodeInternal {

  private static final Logger logger = Logger.getLogger();
  private final Map<UUID, NodeDocument> documents;
  private final Map<UUID, ReentrantLock> locks;

  public NodeInternal() {
    documents = new ConcurrentHashMap<>();
    locks = new HashMap<>();
  }

  public void insert(UUID uuid, NodeDocument insertDoc) throws NodeException {
    synchronized (documents) {
      if (!documents.containsKey(uuid)) {

        documents.put(uuid, insertDoc);
      } else {
        throw new NodeException("Document with key, " + uuid + ", already exists.");
      }
    }
  }

  public void update(UUID updateUUID, NodeDocument updateProps) {
    documents.replace(updateUUID, updateProps);
  }

  public NodeDocument delete(UUID deleteUUID) {
    NodeDocument deleteDoc;
    synchronized (documents) {
      deleteDoc = documents.get(deleteUUID);
      documents.remove(deleteUUID);
    }

    return deleteDoc;
  }

  public NodeDocument getDocument(UUID uuid) {
    return documents.get(uuid);
  }

  public void addRelationship(String relName, Edge edgeDef) {

  }

  public void releaseLock(UUID uuid) {
    if (locks.get(uuid) != null) {
      locks.get(uuid).unlock();
      logger.fine("Released lock on " + uuid);
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
        logger.fine("Took lock on " + uuid);
      } else {
        throw new NodeException("Unable to acquire lock.");
      }
    } catch (InterruptedException e) {
      throw new NodeException("Unable to acquire lock.");
    }
  }
}
