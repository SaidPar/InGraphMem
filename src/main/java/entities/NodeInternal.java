package entities;

import exceptions.NodeException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class NodeInternal {

  // <Unique Identifier, Properties>
  // ToDo: make thread safe resource
  private final Map<UUID, Properties> documents;

  public NodeInternal() {
    documents = new ConcurrentHashMap<>();
  }

  public UUID insert(Properties insertDoc) throws NodeException {
    // create unique identifier
    UUID newUUID = UUID.randomUUID();
    synchronized (documents) {
      if (!documents.containsKey(newUUID)) {
        documents.put(UUID.randomUUID(), insertDoc);
      } else {
        throw new NodeException("Document with key, " + newUUID + ", already exists.");
      }
    }

    return newUUID;
  }

  public void update(UUID updateUUID, Properties updateProps) {
    documents.replace(updateUUID, updateProps);
  }

  public void delete(UUID deleteUUID) {
    documents.remove(deleteUUID);
  }

  public Properties getDocument(UUID uuid) {
    return documents.get(uuid);
  }
}
