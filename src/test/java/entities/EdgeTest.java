package entities;

import database.Database;
import database.Instance;
import database.types.DBOptions;
import database.types.DBType;
import entities.edit_options.InsertOptions;
import entities.transactions.Transaction;
import entities.transactions.TransactionManager;
import exceptions.InGraphDBException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EdgeTest {

  private final String dbName = "EdgeTest";
  private Database edgeDB;

  @BeforeAll
  void init() {

    Instance instance = Instance.getInstance();
    instance.addDatabase(
      new Database(
        new DBOptions.Builder()
          .storageType(DBType.IN_MEMORY)
          .withName(dbName)
          .build()));

    edgeDB = instance.getDatabase(dbName);
    assertNotNull(edgeDB);
  }

  @Test
  void insert() throws InGraphDBException {

    final String test = "test";

    // Setup
    Node testNode = edgeDB.createNode(test);

    List<NodeDocument> documents = new ArrayList<>();
    documents.add(
      new NodeDocument()
        .addAttribute("name", "origin")
    );
    documents.add(
      new NodeDocument()
        .addAttribute("name", "destination")
    );

    List<UUID> insertKeys = testNode.insert(documents, new InsertOptions());
    assertNotNull(insertKeys);
    assertEquals(2, insertKeys.size());

    UUID originKey = insertKeys.get(0);
    UUID destKey = insertKeys.get(1);

    // Test Case
    String testRel = "testRel";
    Edge testEdge = new Edge(dbName, testRel);

    List<EdgeDocument> relDocs = new ArrayList<>();
    relDocs.add(
      (EdgeDocument) new EdgeDocument()
        .setOrigin(new NodeID(test, originKey))
        .setDestination(new NodeID(test, destKey))
        .addAttribute("name", "relationship"));

    List<UUID> relInsertKeys = testEdge.insert(relDocs, new InsertOptions());
    assertEquals(1, relInsertKeys.size());

    EdgeDocument actualEdge = testEdge.getDocument(relInsertKeys.get(0));
    assertEquals(actualEdge.getDestination().getUUID(), destKey);
    assertEquals(actualEdge.getOrigin().getUUID(), originKey);
    assertEquals(actualEdge.getAttribute("name"), "relationship");

    NodeDocument originNode = edgeDB.node(test).getDocument(originKey);
    Map<String, Set<NodePtr>> rels = originNode.getAllRelationships();
    for (var rel : rels.entrySet()) {
      String relName = rel.getKey();
      Set<NodePtr> adjacentNodes = rel.getValue();

      assertEquals(testRel, relName);
      assertEquals(1, adjacentNodes.size());
      NodePtr destNode = adjacentNodes
        .stream()
        .filter(node -> node.getNodeID().getUUID().equals(destKey))
        .findAny().orElse(null);
      assertNotNull(destNode);
    }
  }

  @Test
  void insertInTx() throws InGraphDBException {
    // Setup
    String personNodeName = "Person";
    String vehicleNodeName = "Vehicle";
    String ownsRelName = "OWNS";

    Node personNode = edgeDB.createNode(personNodeName);
    Node vehicleNode = edgeDB.createNode(vehicleNodeName);
    Edge ownsRel = edgeDB.createRelationship(ownsRelName);

    // Test Case
    Transaction tx = TransactionManager.getInstance()
      .createTransaction();

    tx.start();

    List<NodeDocument> personNodes = new ArrayList<>();
    personNodes.add(new NodeDocument()
      .addAttribute("name", "Bill Brasky"));

    List<UUID> personUUIDs = personNode.insert(personNodes, new InsertOptions().withTransactionID(tx.getID()));
    UUID billUUID = personUUIDs.get(0);

    List<NodeDocument> vehicleNodes = new ArrayList<>();
    vehicleNodes.add(new NodeDocument()
      .addAttribute("make", "Toyota")
      .addAttribute("model", "Tacoma"));
    vehicleNodes.add(new NodeDocument()
      .addAttribute("make", "BMW")
      .addAttribute("model", "e30"));

    List<UUID> vehicleUUIDs = vehicleNode.insert(vehicleNodes, new InsertOptions().withTransactionID(tx.getID()));
    UUID tacoUUID = vehicleUUIDs.get(0);
    UUID bmwUUID = vehicleUUIDs.get(1);

    List<EdgeDocument> ownsDocs = new ArrayList<>();
    ownsDocs.add(new EdgeDocument()
      .setOrigin(new NodeID(personNodeName, billUUID))
      .setDestination(new NodeID(vehicleNodeName, tacoUUID)));
    ownsDocs.add(new EdgeDocument()
      .setOrigin(new NodeID(personNodeName, billUUID))
      .setDestination(new NodeID(vehicleNodeName, bmwUUID)));

    List<UUID> ownsUUIDs = ownsRel.insert(ownsDocs, new InsertOptions().withTransactionID(tx.getID()));

    tx.commit();

    Node persons = edgeDB.node(personNodeName);
    NodeDocument billNode = persons.getDocument(billUUID);
    Map<String, Set<NodePtr>> rels = billNode.getAllRelationships();
    assertEquals(1, rels.size());
    for (var rel : rels.entrySet()) {
      String relName = rel.getKey();
      Set<NodePtr> destNodes = rel.getValue();

      assertEquals(ownsRelName, relName);
      assertEquals(2, destNodes.size());

      var tacoNode = destNodes
        .stream().filter(nodePtr -> nodePtr.getNodeID().getUUID().equals(tacoUUID))
        .findAny().orElse(null);
      assertNotNull(tacoNode);

      var bmwNode = destNodes
        .stream().filter(nodePtr -> nodePtr.getNodeID().getUUID().equals(bmwUUID))
        .findAny().orElse(null);
      assertNotNull(bmwNode);
    }
  }
}
