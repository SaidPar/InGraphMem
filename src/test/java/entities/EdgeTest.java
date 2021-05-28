package entities;

import database.Database;
import database.Instance;
import database.types.DBOptions;
import database.types.DBType;
import entities.edit_options.InsertOptions;
import entities.edit_options.UpdateOptions;
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

  @Test
  void updateSanity() throws InGraphDBException {
    // Setup
    String personNodeName = "Person";
    String vehicleNodeName = "Vehicle";
    String ownsRelName = "OWNS";

    Node personNode = edgeDB.createNode(personNodeName);
    Node vehicleNode = edgeDB.createNode(vehicleNodeName);
    Edge ownsRel = edgeDB.createRelationship(ownsRelName);

    Transaction tx = TransactionManager.getInstance()
      .createTransaction();

    tx.start();

    List<NodeDocument> personNodes = new ArrayList<>();
    personNodes.add(new NodeDocument()
      .addAttribute("name", "tester McGee"));

    List<UUID> personUUIDs = personNode.insert(personNodes, new InsertOptions().withTransactionID(tx.getID()));
    UUID mcGeeUUID = personUUIDs.get(0);

    List<NodeDocument> vehicleNodes = new ArrayList<>();
    vehicleNodes.add(new NodeDocument()
      .addAttribute("make", "Geo")
      .addAttribute("model", "Metro"));
    vehicleNodes.add(new NodeDocument()
      .addAttribute("make", "VW")
      .addAttribute("model", "Bug"));
    vehicleNodes.add(new NodeDocument()
      .addAttribute("make", "Ford")
      .addAttribute("model", "Mustang"));

    List<UUID> vehicleUUIDs = vehicleNode.insert(vehicleNodes, new InsertOptions().withTransactionID(tx.getID()));
    UUID geoUUID = vehicleUUIDs.get(0);
    UUID bugUUID = vehicleUUIDs.get(1);
    UUID mustangUUID = vehicleUUIDs.get(2);

    List<EdgeDocument> ownsDocs = new ArrayList<>();
    ownsDocs.add((EdgeDocument) new EdgeDocument()
      .setOrigin(new NodeID(personNodeName, mcGeeUUID))
      .setDestination(new NodeID(vehicleNodeName, geoUUID))
      .addAttribute("PurchasePrice", 3500));
    ownsDocs.add(new EdgeDocument()
      .setOrigin(new NodeID(personNodeName, mcGeeUUID))
      .setDestination(new NodeID(vehicleNodeName, bugUUID)));

    List<UUID> ownsUUIDs = ownsRel.insert(ownsDocs, new InsertOptions().withTransactionID(tx.getID()));

    tx.commit();

    // Test Case
    Map<UUID, EdgeDocument> updateDocs = new HashMap<>();
    updateDocs.put(ownsUUIDs.get(0), (EdgeDocument) new EdgeDocument().addAttribute("year", 1994));
    updateDocs.put(ownsUUIDs.get(1), (EdgeDocument) new EdgeDocument()
      .setDestination(new NodeID(vehicleNodeName, mustangUUID)));

    Map<UUID, EdgeDocument> actualUpdates = ownsRel.update(updateDocs, new UpdateOptions().returnNew());
    EdgeDocument actual = ownsRel.getDocument(ownsUUIDs.get(0));

    // Verify original attribute is preserved, new attribute inserted, origin / dest preserved
    assertEquals(3500, actual.getAttribute("PurchasePrice"));
    assertEquals(1994, actual.getAttribute("year"));
    assertEquals(mcGeeUUID, actual.getOrigin().getUUID());
    assertEquals(geoUUID, actual.getDestination().getUUID());

    // Test case for update origin / dest
    EdgeDocument actual2 = ownsRel.getDocument(ownsUUIDs.get(1));

    // Verify dest is updated
    assertEquals(mustangUUID, actual2.getDestination().getUUID());
    // verify index free adjacency is updated
    NodeDocument mustang = vehicleNode.getDocument(mustangUUID);
    Set<NodePtr> relatedNodes = mustang.getAdjacentNodes("OWNS");
    assertEquals(1, relatedNodes.size());
    assertEquals(mcGeeUUID, relatedNodes.stream().findFirst().get().getNodeID().getUUID());

    // ToDo: Test we can update a relationship pointing to an old node to a new node and the IFA updates.
  }


}
