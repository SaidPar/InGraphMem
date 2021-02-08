package entities;

import entities.edit_options.DeleteOptions;
import entities.edit_options.InsertOptions;
import entities.edit_options.UpdateOptions;
import entities.transactions.participants.NodeParticipant;
import entities.transactions.Transaction;
import entities.transactions.TransactionManager;
import entities.transactions.TxStatus;
import exceptions.InGraphDBException;
import exceptions.NodeException;
import exceptions.TransactionException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * public facing Node Container. All CRUD operations occur within transaction implicit or explicit
 */
public class Node {

  private final String name;
  private final NodeInternal internalNode;

  public Node(String name) {
    this.name = name;
    internalNode = new NodeInternal();
  }

  public List<UUID> insert(List<NodeDocument> documents, InsertOptions opts) throws InGraphDBException {

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

      NodeParticipant participant = getParticipant(tx);
      List<UUID> insertKeys = participant.insert(documents);

      if (null == opts.getTransactionID())
        tx.commit();

      return insertKeys;
    } catch (TransactionException e) {
      throw new InGraphDBException(e);
    }
  }

  public Map<UUID, NodeDocument> update(Map<UUID, NodeDocument> updateDocuments, UpdateOptions opts)
    throws InGraphDBException {

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

      Map<UUID, NodeDocument> doc;
      try {
        NodeParticipant participant = getParticipant(tx);
        doc = participant.update(updateDocuments, opts);
      } catch (NodeException e) {
        if (null == opts.getTransactionID())
          tx.abort();

        throw e;
      }

      if (null == opts.getTransactionID())
        tx.commit();

      return doc;
    } catch (NodeException | TransactionException e) {
      throw new InGraphDBException(e);
    }

  }

  public void delete(Set<UUID> deleteKeys, DeleteOptions opts) throws InGraphDBException {
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

      try {
        NodeParticipant participant = getParticipant(tx);
        participant.delete(deleteKeys);
      } catch (NodeException e) {
        if (null == opts.getTransactionID())
          tx.abort();
      }

      if (null == opts.getTransactionID())
        tx.commit();
    } catch (TransactionException e) {
      throw new InGraphDBException(e);
    }
  }

  public NodeDocument getDocument(UUID key) {
    // ToDo: Take read lock
    return internalNode.getDocument(key);
  }

  NodeParticipant getParticipant(Transaction tx) {
    NodeParticipant participant = (NodeParticipant) tx.getParticipant(name);
    if (null == participant) {
      participant = new NodeParticipant(name, this.internalNode);
      tx.addParticipant(name, participant);
      participant = (NodeParticipant) tx.getParticipant(name);
    }

    return participant;
  }
}
