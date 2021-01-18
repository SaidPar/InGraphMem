package entities;

import entities.edit_options.DeleteOptions;
import entities.edit_options.InsertOptions;
import entities.edit_options.UpdateOptions;
import entities.transactions.NodeParticipant;
import entities.transactions.Transaction;
import entities.transactions.TransactionManager;
import entities.transactions.TxStatus;

import java.util.List;
import java.util.Map;
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

  public void insert(List<Properties> documents, InsertOptions opts) throws Exception {
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
    participant.insert(documents);

    if (null == opts.getTransactionID())
      tx.commit();
  }

  public void update(Map<UUID, Properties> updateDocuments, UpdateOptions opts) {

  }

  public void delete(List<UUID> deleteKeys, DeleteOptions opts) {

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
