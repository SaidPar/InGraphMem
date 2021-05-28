package entities;

import database.Database;
import database.Instance;
import entities.edit_options.InsertOptions;
import entities.edit_options.UpdateOptions;
import entities.transactions.Transaction;
import entities.transactions.TransactionManager;
import entities.transactions.TxStatus;
import entities.transactions.participants.EdgeParticipant;
import exceptions.*;

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

      addNodeAdjacency(documents, tx);

      if (null == opts.getTransactionID())
        tx.commit();

      return insertKeys;
    } catch (TransactionException | GraphException e) {
      throw new InGraphDBException(e);
    }
  }

  public Map<UUID, EdgeDocument> update(Map<UUID, EdgeDocument> updateDocuments, UpdateOptions opts)
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

      Map<UUID, EdgeDocument> updates;
      try {
        EdgeParticipant participant = getParticipant(tx);
        updates = participant.update(updateDocuments, opts);
        updateNodeAdjacency(updateDocuments, updates, tx);

      } catch (EdgeException e) {
        if (null == opts.getTransactionID())
          tx.abort();

        throw e;
      }

      if (null == opts.getTransactionID())
        tx.commit();

      return updates;
    } catch (TransactionException | GraphException e) {
      throw new InGraphDBException(e);
    }
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

  private void addNodeAdjacency(List<EdgeDocument> documents, Transaction tx)
    throws InGraphDBException, NodeException {

    // ToDo: optimization to call update once per node participant

    for (EdgeDocument doc : documents) {

      // ToDo: Think about locking here, since we are now in a get and set procedure -- should be handled by transaction semantics
      // ToDo: Error handling strategy? what if nodes don't exist?
      // ToDo: Deadlock scenarios?
      NodeID originNodeID = doc.getOrigin();
      NodeID destNodeID = doc.getDestination();

      // partial document for updates
      NodeDocument originDoc = new NodeDocument()
        .addRelationship(this.name, new NodePtr(destNodeID, Direction.DESTINATION));

      db.node(originNodeID.getNodeName())
        .getParticipant(tx)
        .update(Map.of(originNodeID.getUUID(), originDoc),
          new UpdateOptions().withTransactionID(tx.getID()));

      NodeDocument destDoc = new NodeDocument()
        .addRelationship(this.name, new NodePtr(originNodeID, Direction.ORIGIN));

      db.node(destNodeID.getNodeName())
        .getParticipant(tx)
        .update(Map.of(destNodeID.getUUID(), destDoc),
          new UpdateOptions().withTransactionID(tx.getID()));
    }
  }

  private void updateNodeAdjacency(Map<UUID, EdgeDocument> partialDocs, Map<UUID, EdgeDocument> updateDocs, Transaction tx)
    throws InGraphDBException, NodeException {

    for (var partialDoc : partialDocs.entrySet()) {
      UUID edgeUUID = partialDoc.getKey();
      EdgeDocument edgeDoc = partialDoc.getValue();

      NodeID originNodeID = edgeDoc.getOrigin() != null
        ? edgeDoc.getOrigin()                         // take new origin
        : updateDocs.get(edgeUUID).getOrigin();       // take original origin
      NodeID destNodeID = edgeDoc.getDestination() != null
        ? edgeDoc.getDestination()                    // take new destination
        : updateDocs.get(edgeUUID).getDestination();  // take original destination

      if (edgeDoc.getOrigin() != null) {
        // We have an update to the origin of the relationship
        // 1. Delete old related node from destination,
        // 2. Add new related node to origin

        // delete old
        NodeID oldOriginNodeID = db.relationship(name)
          .getDocument(edgeUUID)
          .getOrigin();

        NodeDocument oldOriginDoc = new NodeDocument()
          .removeRelationship(name, new NodePtr(updateDocs.get(edgeUUID).getDestination(), Direction.ORIGIN));

        db.node(oldOriginNodeID.getNodeName())
          .getParticipant(tx)
          .update(Map.of(oldOriginNodeID.getUUID(), oldOriginDoc),
            new UpdateOptions().withTransactionID(tx.getID()));

        // Add new
        NodeDocument originDoc = new NodeDocument()
          .addRelationship(name, new NodePtr(destNodeID, Direction.ORIGIN));

        db.node(originNodeID.getNodeName())
          .getParticipant(tx)
          .update(Map.of(originNodeID.getUUID(), originDoc),
            new UpdateOptions().withTransactionID(tx.getID()));
      }

      if (edgeDoc.getDestination() != null) {

        NodeDocument destDoc = new NodeDocument()
          .addRelationship(name, new NodePtr(originNodeID, Direction.DESTINATION));

        db.node(destNodeID.getNodeName())
          .getParticipant(tx)
          .update(Map.of(destNodeID.getUUID(), destDoc),
            new UpdateOptions().withTransactionID(tx.getID()));
      }
    }
  }
}
