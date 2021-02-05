package entities;

import entities.edit_options.InsertOptions;
import entities.transactions.Transaction;
import entities.transactions.TransactionManager;
import entities.transactions.TxStatus;
import entities.transactions.participants.EdgeParticipant;
import entities.transactions.participants.NodeParticipant;
import exceptions.InGraphDBException;
import exceptions.TransactionException;

import java.util.*;

public final class Edge {

  private final String name;
  private final EdgeInternal internalEdge;

  public Edge(String databaseName, String name) {
    this.name = name;
    this.internalEdge = new EdgeInternal(databaseName, name);
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

      if (null == opts.getTransactionID())
        tx.commit();

      return insertKeys;
    } catch (TransactionException e) {
      throw new InGraphDBException(e);
    }
  }

  public Map<UUID, EdgeDocument> update() {
    return new HashMap<>();
  }

  public void delete() {
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
}
