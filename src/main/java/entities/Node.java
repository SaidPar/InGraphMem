package entities;

import entities.edit_options.DeleteOptions;
import entities.edit_options.InsertOptions;
import entities.edit_options.UpdateOptions;
import entities.transactions.NodeParticipant;
import entities.transactions.Transaction;
import entities.transactions.TransactionManager;
import entities.transactions.TxStatus;
import exceptions.NodeException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * public facing Node. All CRUD operations occur within transaction implicit or explicit
 */
public class Node {

  private final String name;
  private final NodeInternal internalNode;

  public Node(String name) {
    this.name = name;
    internalNode = new NodeInternal();
  }

  public List<UUID> insert(List<Document> documents, InsertOptions opts) throws Exception {
    // ToDo: Insert needs to return List of UUIDs

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
      throw new Exception("Transaction not running."); // ToDo: InGraphDBException

    NodeParticipant participant = getParticipant(tx);
    List<UUID> insertKeys = participant.insert(documents);

    if (null == opts.getTransactionID())
      tx.commit();

    return insertKeys;
  }

  public Map<UUID, Document> update(Map<UUID, Document> updateDocuments, UpdateOptions opts) throws Exception {
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
      throw new Exception("Transaction not running."); // ToDo: InGraphDBException

    Map<UUID, Document> doc;
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
  }

  public void delete(Set<UUID> deleteKeys, DeleteOptions opts) throws Exception {
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
      throw new Exception("Transaction not running."); // ToDo: InGraphDBException

    try {
      NodeParticipant participant = getParticipant(tx);
      participant.delete(deleteKeys);
    } catch (NodeException e) {
      if (null == opts.getTransactionID())
        tx.abort();
    }

    if (null == opts.getTransactionID())
      tx.commit();
  }

  public Document getDocument(UUID key) {
    return internalNode.getDocument(key);
  }

  private NodeParticipant getParticipant(Transaction tx) throws Exception {
    NodeParticipant participant = (NodeParticipant) tx.getParticipant(name);
    if (null == participant) {
      participant = new NodeParticipant(name, this.internalNode);
      tx.addParticipant(name, participant);
      participant = (NodeParticipant) tx.getParticipant(name);
    }

    return participant;
  }
}
