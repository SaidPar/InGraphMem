package entities.transactions.participants;

import database.Instance;
import entities.NodeDocument;
import entities.NodeInternal;
import entities.NodePtr;
import entities.edit_options.UpdateOptions;
import entities.transactions.Transactionable;
import exceptions.NodeException;

import java.util.*;

/**
 * This class holds the interim data during the lifetime of the transaction. This is the only class that
 * speaks directly with NodeInternal
 */
public final class NodeParticipant implements Transactionable, EditableNode {

  private final String name;
  private final NodeInternal internalNode;

  // Member variables holding interim tx changeset.
  private final Map<UUID, NodeDocument> insertDocs;
  private final Map<UUID, NodeDocument> updateDocuments;
  private final Set<UUID> deleteDocuments;

  // Member variables  holding original state for rollback.
  private final List<UUID> insertedKeys;
  private final Map<UUID, NodeDocument> originalUpdateDocs;
  private final Map<UUID, NodeDocument> originalDeleteDocs;

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

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Transactionable
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public void startTransaction() {
    // get snapshot moment ?
  }

  @Override
  public void commitTransaction() throws NodeException {
    // commit changes to internal Node class
    try {
      // insert commits
      for (var doc : insertDocs.entrySet()) {
        internalNode.insert(doc.getKey(), doc.getValue());
        insertedKeys.add(doc.getKey());
      }

      // update commits
      for (var doc : updateDocuments.entrySet()) {
        UUID uuid = doc.getKey();
        NodeDocument updateDoc = doc.getValue();
        internalNode.update(uuid, updateDoc);
      }

      // delete commits
      for (UUID key : deleteDocuments) {
        originalDeleteDocs.put(key, internalNode.delete(key));
      }
    } catch (Exception e) {
        throw new NodeException("Failed to commit transaction.");
    } finally {
      releaseLocks();
    }
  }

  @Override
  public void abortTransaction() {
    // Throw away change sets
    // ToDo: Post commit, should we roll back?
    insertedKeys.clear();
    updateDocuments.clear();
    deleteDocuments.clear();

    releaseLocks();
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
    } finally {
      releaseLocks();
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // EditableNode
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public List<UUID> insert(List<NodeDocument> documents) {
    List<UUID> insertKeys = new ArrayList<>();
    for (NodeDocument doc : documents) {
      UUID newUUID = UUID.randomUUID();
      synchronized(insertDocs) {
        if (!insertDocs.containsKey(newUUID))
          insertDocs.put(newUUID, doc);
          insertKeys.add(newUUID);
      }
    }

    // ToDo: bubble NodeDocument all the way up the stack?
    return insertKeys;
  }

  @Override
  public Map<UUID, NodeDocument> update(Map<UUID, NodeDocument> updatePartialDocuments, UpdateOptions opts)
    throws NodeException {

    for (var updateMap : updatePartialDocuments.entrySet()) {
      UUID key = updateMap.getKey();
      NodeDocument updatePartial = updateMap.getValue();

      // We should first check the transaction context for updating nodes. We may be updating
      // a document which has already participated in the transaction explicitly or implicitly
      NodeDocument workingDoc;
      if (this.updateDocuments.containsKey(key)) {
        workingDoc = updateDocuments.get(key);
      } else if (this.insertDocs.containsKey(key)) {
        workingDoc = insertDocs.get(key);
      } else if (internalNode.getDocument(key) != null) {
        workingDoc = internalNode.getDocument(key);

        // we only take a lock when there exists an entry in the internal node
        internalNode.takeLock(key, 5L);

        // this is used for roll back. Track only original value.
        originalUpdateDocs.put(key, workingDoc);
      } else {
        throw new NodeException("Document with key, " + key + ", does not exist.");
      }

      NodeDocument updateDoc = new NodeDocument(workingDoc);

      // Merge Operation - overwrites any property which exists
      for (var prop : updatePartial.getProperties().entrySet()) {
        String propName = prop.getKey();
        Object propVal = prop.getValue();

        updateDoc.addAttribute(propName, propVal);
      }

      if (updatePartial.hasRelationship()) {
        // new relationship in partial document.
        // ToDo: Do we need to check these are valid relationships?
        Map<String, Set<NodePtr>> newRels = updatePartial.getAllRelationships();
        for (var map : newRels.entrySet()) {
          String relName = map.getKey();
          Set<NodePtr> adjacentNodes = map.getValue();

          for (NodePtr node : adjacentNodes) {
            updateDoc.addRelationship(relName, node);
          }
        }
      }

      if (updatePartial.hasRemoveNodes()) {
        // remove relationship
        Map<String, Set<NodePtr>> removeNodes = updatePartial.getAllRemoveNodes();
        for (var map : removeNodes.entrySet()) {
          String relName = map.getKey();
          Set<NodePtr> adjacentNodes = map.getValue();

          for (NodePtr node : adjacentNodes) {
            updateDoc.removeRelationship(relName, node);
          }
        }
      }

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
  public void delete(Set<UUID> deleteKeys) throws NodeException {
    for (UUID key : deleteKeys) {
      try {
        internalNode.takeLock(key, 5L);
        deleteDocuments.add(key);
      } catch (Exception e) {
        throw new NodeException("Failed to acquire Lock on " + key);
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Helpers
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private void releaseLocks() {
    // release update locks
    for (var doc : updateDocuments.entrySet()) {
      internalNode.releaseLock(doc.getKey());
    }

    // release delete locks
    for (UUID key : deleteDocuments) {
      internalNode.releaseLock(key);
    }
  }
}
