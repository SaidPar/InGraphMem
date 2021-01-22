package entities;

import exceptions.NodeException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class NodeInternal {

  // <Unique Identifier, Properties>
  // ToDo: make thread safe resource
  private final Map<UUID, Document> documents;

  public NodeInternal() {
    documents = new ConcurrentHashMap<>();
  }

  public void insert(UUID uuid, Document insertDoc) throws NodeException {
    synchronized(documents) {
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
    synchronized(documents) {
      deleteDoc = documents.get(deleteUUID);
      documents.remove(deleteUUID);
    }

    return deleteDoc;
  }

  public Document getDocument(UUID uuid) {
    return documents.get(uuid);
  }
}
