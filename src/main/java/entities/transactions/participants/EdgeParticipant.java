package entities.transactions.participants;

import entities.EdgeDocument;
import entities.EdgeInternal;
import entities.NodeDocument;
import entities.NodeInternal;
import entities.edit_options.UpdateOptions;
import entities.transactions.Transactionable;
import exceptions.EdgeException;
import exceptions.NodeException;

import java.util.*;

public final class EdgeParticipant implements Transactionable, EditableEdge {

  private final String name;
  private final EdgeInternal internalEdge;

  // Member variables holding interim tx changeset.
  private final Map<UUID, EdgeDocument> insertDocs;
  private final Map<UUID, EdgeDocument> updateDocuments;
  private final Set<UUID> deleteDocuments;

  // Member variables  holding original state for rollback.
  private final List<UUID> insertedKeys;
  private final Map<UUID, EdgeDocument> originalUpdateDocs;
  private final Map<UUID, EdgeDocument> originalDeleteDocs;

  public EdgeParticipant(String name, EdgeInternal internalEdge) {
    this.name = name;
    this.internalEdge = internalEdge;

    insertDocs = new HashMap<>();
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
  public void commitTransaction() throws NodeException {
    // Todo: commit changeset updates to internal edge

    // inserts
    // ToDo: insert into internal edge
    // what to do about internal node changes?
  }

  @Override
  public void abortTransaction() {
    // ToDo: clear changesets
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
    // ToDo: Insert into changeset
    //    Insert into nodes as well? Can i farm changes to nodeparticipant? too much coupling?
    // ToDo: take lock on origin / dest nodes(?)

    return null;
  }

  @Override
  public Map<UUID, EdgeDocument> update(Map<UUID, EdgeDocument> updateDocuments, UpdateOptions options)
    throws EdgeException {

    // ToDo: merge any updates into existing doc
    //  take rel lock
    //  if updates to origin / dest nodes, take node locks


    return null;
  }

  @Override
  public void delete(Set<UUID> deleteKeys) throws EdgeException {
    // ToDo: take lock on rel
    //  take lock on nodes
    //  add to deleteDocs
  }
}
