package entities.transactions;

import database.Instance;
import entities.NodeInternal;
import entities.Document;
import entities.edit_options.UpdateOptions;
import exceptions.NodeException;

import java.util.*;

/**
 * This class holds the interim data during the lifetime of the transaction. This is the only class that
 * speaks directly with NodeInternal
 */
public final class NodeParticipant implements Transactionable, Editable {

  private final String name;
  private final NodeInternal internalNode;

  // Member variables holding interim tx changeset.
  private final Map<UUID, Document> insertDocs;
  private final Map<UUID, Document> updateDocuments;
  private final Set<UUID> deleteDocuments;

  // Member variables  holding original state for rollback.
  private final List<UUID> insertedKeys;
  private final Map<UUID, Document> originalUpdateDocs;
  private final Map<UUID, Document> originalDeleteDocs;

  private final Instance graphInstance;

  public NodeParticipant(String name, NodeInternal internalNode) {
    this.name = name;
    this.internalNode = internalNode;

    insertDocs = new HashMap<>();
    updateDocuments = new HashMap<>();
    deleteDocuments = new HashSet<>();

    insertedKeys = new ArrayList<>();
    originalUpdateDocs = new HashMap<>();
    originalDeleteDocs = new HashMap<>();

    graphInstance = Instance.getInstance();
  }

  @Override
  public void startTransaction() {
    // get snapshot moment ?
  }

  @Override
  public void commitTransaction() {
    // commit changes to internal Node class
    try {
      // ToDo: Take Lock

      // insert commits
      for (var doc : insertDocs.entrySet()) {
        internalNode.insert(doc.getKey(), doc.getValue());
        insertedKeys.add(doc.getKey());
      }

      // update commits
      for (var doc : updateDocuments.entrySet()) {
        UUID uuid = doc.getKey();
        Document updateDoc = doc.getValue();
        internalNode.update(uuid, updateDoc);
      }

      // delete commits
      for (UUID key : deleteDocuments) {
        originalDeleteDocs.put(key, internalNode.delete(key));
      }

      // ToDo: Release Lock
    } catch (Exception e) {
        // ToDo: Hold these operations in member variable, and have level above call abort in case another participant
        //  fails.
        // ToDo: Throw?
    }

    // ToDo: Update & Delete Commit actions
    //  think about undo operation
  }

  @Override
  public void abortTransaction() {
    // throw away change set
  }

  @Override
  public void rollBack() {
    // ToDo: Rollback changes. Needs to be fault tolerant
    try {
      for (UUID uuid : insertedKeys) {
        internalNode.delete(uuid);
      }
    } catch (Exception e) {
      // ToDo: What to do if we fail here? Catastrophic --> strategy for recovery
    }

    try {
      for (var origUpdate : originalUpdateDocs.entrySet()) {
        internalNode.update(origUpdate.getKey(), origUpdate.getValue());
      }
    } catch (Exception e) {
      // ToDo: What to do if we fail here? Catastrophic --> strategy for recovery
    }

    try {
      for (var origDelete : originalDeleteDocs.entrySet()) {
        internalNode.insert(origDelete.getKey(), origDelete.getValue());
      }
    } catch (Exception e) {
      // ToDo: What to do if we fail here? Catastrophic --> strategy for recovery
    }
  }

  @Override
  public List<UUID> insert(List<Document> documents) {
    List<UUID> insertKeys = new ArrayList<>();
    for (Document doc : documents) {
      UUID newUUID = UUID.randomUUID();
      synchronized(insertDocs) {
        if (!insertDocs.containsKey(newUUID))
          insertDocs.put(newUUID, doc);
          insertKeys.add(newUUID);
      }
    }

    return insertKeys;
  }

  @Override
  public Map<UUID, Document> update(Map<UUID, Document> updatePartialDocuments, UpdateOptions opts)
    throws NodeException {

    for (var updateMap : updatePartialDocuments.entrySet()) {
      UUID key = updateMap.getKey();
      Document updatePartial = updateMap.getValue();

      Document original = internalNode.getDocument(key);
      if (null == original) {
        throw new NodeException("Document with key, " + key + ", does not exist.");
      }

      Document updateDoc = new Document(original);

      // Merge Operation - overwrites any property which exists
      for (var prop : updatePartial.getProperties().entrySet()) {
        String propName = prop.getKey();
        Object propVal = prop.getValue();

        updateDoc.addAttribute(propName, propVal);
      }

      originalUpdateDocs.put(key, original);
      updateDocuments.put(key, updateDoc);
    }

    if (opts.canReturnNew())
      return updateDocuments;
    else if (opts.canReturnOld())
      return originalUpdateDocs;
    else
      return null;
  }

  @Override
  public void delete(Set<UUID> deleteKeys) {
    deleteDocuments.addAll(deleteKeys);
  }
}
