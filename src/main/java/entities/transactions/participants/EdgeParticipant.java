package entities.transactions.participants;

import entities.*;
import entities.edit_options.UpdateOptions;
import entities.transactions.Transactionable;
import exceptions.EdgeException;
import exceptions.GraphException;
import exceptions.NodeException;

import java.util.*;

public final class EdgeParticipant implements Transactionable, EditableEdge {

  private final String name;
  private final EdgeInternal internalEdge;

  // Member variables holding interim tx changeset.
  private final Map<UUID, EdgeDocument> insertDocuments;
  private final Map<UUID, EdgeDocument> updateDocuments;
  private final Set<UUID> deleteDocuments;

  // Member variables  holding original state for rollback.
  private final List<UUID> insertedKeys;
  private final Map<UUID, EdgeDocument> originalUpdateDocs;
  private final Map<UUID, EdgeDocument> originalDeleteDocs;

  public EdgeParticipant(String name, EdgeInternal internalEdge) {
    this.name = name;
    this.internalEdge = internalEdge;

    insertDocuments = new HashMap<>();
    updateDocuments = new HashMap<>();
    deleteDocuments = new HashSet<>();

    insertedKeys = new ArrayList<>();
    originalUpdateDocs = new HashMap<>();
    originalDeleteDocs = new HashMap<>();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Transactionable
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public void startTransaction() {
    // Snapshot moment?
  }

  @Override
  public void commitTransaction() throws GraphException {

    // inserts
    for (var insertDoc : insertDocuments.entrySet()) {
      UUID uuid = insertDoc.getKey();
      EdgeDocument edgeDoc = insertDoc.getValue();

      internalEdge.insert(uuid, edgeDoc);
      insertedKeys.add(uuid);
    }

    // updates
    for (var updateDoc : updateDocuments.entrySet()) {
      UUID uuid = updateDoc.getKey();
      EdgeDocument edgeDocument = updateDoc.getValue();
      internalEdge.update(uuid, edgeDocument);
    }

    // ToDo: Deletes
  }

  @Override
  public void abortTransaction() {
    // Clear any changesets
    // ToDo: Post commit, should we rollback?
    insertDocuments.clear();
    updateDocuments.clear();
    deleteDocuments.clear();
  }

  @Override
  public void rollBack() {
    // ToDo: Reverse any committed actions
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // EditableEdge
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public List<UUID> insert(List<EdgeDocument> documents) {

    List<UUID> insertKeys = new ArrayList<>();
    for (EdgeDocument doc : documents) {
      UUID newUUID = UUID.randomUUID();
      synchronized(insertDocuments) {
        if (!insertDocuments.containsKey(newUUID)) {
          insertDocuments.put(newUUID, doc);
        }

        insertKeys.add(newUUID);
      }
    }

    return insertKeys;
  }

  @Override
  public Map<UUID, EdgeDocument> update(Map<UUID, EdgeDocument> updatePartialDocs, UpdateOptions options)
    throws EdgeException {

    for (var updateDoc : updatePartialDocs.entrySet()) {
      UUID docKey = updateDoc.getKey();
      EdgeDocument partialEdgeDoc = updateDoc.getValue();

      EdgeDocument workingEdge;
      if (updateDocuments.containsKey(docKey)) {
        workingEdge = updateDocuments.get(docKey);
      } else if (insertDocuments.containsKey(docKey)) {
        workingEdge = insertDocuments.get(docKey);
      } else if (internalEdge.getDocument(docKey) != null) {
        workingEdge = internalEdge.getDocument(docKey);

        // we only take a lock when there exists an entry in the internal node
        // ToDo: Deadlock scenarios?
        // ToDo: internalEdge.takeLock(docKey, 5L);

        // this is used for roll back. Track only original value.
        originalUpdateDocs.put(docKey, workingEdge);
      } else {
        throw new EdgeException("Edge Document with key, " + docKey + ", does not exist.");
      }

      EdgeDocument updateEdge = new EdgeDocument(workingEdge);

      // Merge Operation - overwrites any property which exists
      for (var prop : partialEdgeDoc.getProperties().entrySet()) {
        String propName = prop.getKey();
        Object propVal = prop.getValue();

        updateEdge.addAttribute(propName, propVal);
      }

      // Handle origin / dest updates
      if (partialEdgeDoc.getOrigin() != null) {
        updateEdge.setOrigin(partialEdgeDoc.getOrigin());
      }

      if (partialEdgeDoc.getDestination() != null) {
        updateEdge.setDestination(partialEdgeDoc.getDestination());
      }

      updateDocuments.put(docKey, updateEdge);
    } // end for (..partial docs..)

    if (options.canReturnNew())
      return updateDocuments;
    else if (options.canReturnOld())
      return originalUpdateDocs;
    else
      return null;
  }

  @Override
  public void delete(Set<UUID> deleteKeys) throws EdgeException {
    // ToDo: take lock on rel
    //  take lock on nodes
    //  add to deleteDocs
  }
}
