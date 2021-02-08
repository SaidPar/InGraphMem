package entities;

import database.Database;
import database.Instance;
import entities.edit_options.InsertOptions;
import entities.edit_options.UpdateOptions;
import entities.transactions.Transaction;
import entities.transactions.TransactionManager;
import entities.transactions.TxStatus;
import entities.transactions.participants.EdgeParticipant;
import entities.transactions.participants.NodeParticipant;
import exceptions.InGraphDBException;
import exceptions.NodeException;
import exceptions.TransactionException;

import java.util.*;

public final class Edge {

  private final String name;
  private final EdgeInternal internalEdge;
  private final Database db;

  public Edge(String databaseName, String name) {
    this.name = name;
    this.internalEdge = new EdgeInternal(databaseName, name);
    this.db = Instance.getInstance().getDatabase(databaseName);
  }

  // ToDo: think through edge transaction handling as we touch node internals
  public List<UUID> insert(List<EdgeDocument> documents, InsertOptions opts) throws InGraphDBException {

    try {
      TransactionManager txManager = TransactionManager.getInstance();
      Transaction tx;

      if (null == opts.getTransactionID()) {
        // implicit transaction
        tx = txManager.createTransaction();
        tx.start();
      } else {
        // explicit transaction
        tx = txManager.getTransaction(opts.getTransactionID());
      }

      if (tx.getStatus() != TxStatus.RUNNING)
        throw new InGraphDBException("Transaction not running.");

      EdgeParticipant participant = getParticipant(tx);
      List<UUID> insertKeys = participant.insert(documents);

      updateNodeAdjacency(documents, tx);

      if (null == opts.getTransactionID())
        tx.commit();

      return insertKeys;
    } catch (TransactionException | NodeException e) {
      throw new InGraphDBException(e);
    }
  }

  public Map<UUID, EdgeDocument> update() {
    return new HashMap<>();
  }

  public void delete() {
  }

  public EdgeDocument getDocument(UUID uuid) {
    return internalEdge.getDocument(uuid);
  }

  private EdgeParticipant getParticipant(Transaction tx) {
    EdgeParticipant participant = (EdgeParticipant) tx.getParticipant(name);
    if (null == participant) {
      participant = new EdgeParticipant(name, this.internalEdge);
      tx.addParticipant(name, participant);
      participant = (EdgeParticipant) tx.getParticipant(name);
    }

    return participant;
  }

  private void updateNodeAdjacency(List<EdgeDocument> documents, Transaction tx)
    throws InGraphDBException, NodeException {

    // ToDo: optimization to call update once per node participant

    for(EdgeDocument doc : documents) {

      // ToDo: Think about locking here, since we are now in a get and set procedure
      // ToDo: Error handling strategy? what if nodes don't exist?
      NodeID originNodeID = doc.getOrigin();

      // partial document for updates
      NodeDocument originDoc = new NodeDocument()
        .addRelationship(this.name, new NodePtr(originNodeID, Direction.ORIGIN));

      db.node(originNodeID.getNodeName())
        .getParticipant(tx)
        .update(Map.of(originNodeID.getUUID(), originDoc),
          new UpdateOptions().withTransactionID(tx.getID()));

      NodeID destNodeID = doc.getDestination();
      NodeDocument destDoc = new NodeDocument()
        .addRelationship(this.name, new NodePtr(destNodeID, Direction.DESTINATION));

      db.node(destNodeID.getNodeName())
        .getParticipant(tx)
        .update(Map.of(destNodeID.getUUID(), destDoc),
          new UpdateOptions().withTransactionID(tx.getID()));
    }
  }
}
