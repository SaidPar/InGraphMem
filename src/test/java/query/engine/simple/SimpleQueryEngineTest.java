package query.engine.simple;

import database.Database;
import database.Instance;
import database.types.DBOptions;
import database.types.DBType;
import entities.*;
import entities.edit_options.InsertOptions;
import entities.transactions.Transaction;
import entities.transactions.TransactionManager;
import exceptions.InGraphDBException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import query.engine.ResultSet;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SimpleQueryEngineTest {

  private final String dbName = "queryTest";
  private Database queryDB;

  @BeforeAll
  void init() throws InGraphDBException {

    Instance instance = Instance.getInstance();
    instance.addDatabase(
      new Database(
        new DBOptions.Builder()
          .storageType(DBType.IN_MEMORY)
          .withName(dbName)
          .build()));

    queryDB = instance.getDatabase(dbName);
    assertNotNull(queryDB);
    
    populateDB();
  }

  void populateDB() throws InGraphDBException {
    // Setup
    String personNodeName = "Person";
    String vehicleNodeName = "Vehicle";
    String ownsRelName = "OWNS";

    Node personNode = queryDB.createNode(personNodeName);
    Node vehicleNode = queryDB.createNode(vehicleNodeName);
    Edge ownsRel = queryDB.createRelationship(ownsRelName);

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

    Node persons = queryDB.node(personNodeName);
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
  void query() {

    String cypher = "MATCH (p:Person) WHERE p.name = 'Said' RETURN p.name";

    ResultSet resultSet = new SimpleQueryEngine(queryDB)
      .query(cypher);

    assertNotNull(resultSet);
  }
}