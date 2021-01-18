package entities.transactions;

import database.Instance;
import entities.NodeInternal;
import entities.Properties;

import java.util.*;

/**
 * This class holds the interim data during the lifetime of the transaction. This is the only class that
 * speaks directly with NodeInternal
 */
public final class NodeParticipant implements Transactionable, Editable {

  private final String name;
  private final NodeInternal internalNode;

  // Member variables holding interim tx changeset.
  private final List<Properties> insertDocs;
  private final Map<UUID, Properties> updateDocuments;
  private final Set<UUID> deleteDocuments;

  // Member variables  holding original state for rollback.
  private final List<UUID> insertedKeys;
  private final Map<UUID, Properties> originalUpdateDocs;
  private final Map<UUID, Properties> originalDeleteDocs;

  private final Instance graphInstance;

  public NodeParticipant(String name, NodeInternal internalNode) {
    this.name = name;
    this.internalNode = internalNode;

    insertDocs = new ArrayList<>();
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
    List<UUID> insertedKeys = new ArrayList<>();
    try {
      for (Properties doc : insertDocs) {
        insertedKeys.add(
          internalNode.insert(doc));
      }
    } catch (Exception e) {
        // ToDo: Hold these operations in member variable, and have level above call abort in case another participant
        //  fails.
        // delete keys, throw exception
        for (UUID uuid : insertedKeys) {
            internalNode.delete(uuid);
        }

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

    // ToDo: Updates & Deletes
  }

  @Override
  public void insert(List<Properties> documents) {
    insertDocs.addAll(documents);
  }

  @Override
  public void update(Map<UUID, Properties> updateDocuments) {
    // for each one, does it exist, if so, merge
  }

  @Override
  public void delete(Set<UUID> deleteKeys) {
    deleteDocuments.addAll(deleteKeys);
  }
}
