package entities;

import entities.edit_options.DeleteOptions;
import entities.edit_options.InsertOptions;
import entities.transactions.Transaction;
import entities.transactions.TransactionManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

  @Test
  void insert() throws Exception {

    Node testNode = new Node("test");

    List<Document> documents = new ArrayList<>();
    documents.add(
      new Document()
        .addAttribute("test1", "test1")
        .addAttribute("test2" ,2)
    );

    List<UUID> insertKeys = testNode.insert(documents, new InsertOptions());
    assertNotNull(insertKeys);
    assertEquals(1, insertKeys.size());

    Document insertDoc = testNode.getDocument(insertKeys.get(0));
    assertEquals("test1", insertDoc.getAttribute("test1"));
  }

  @Test
  void insertsInTxSingleNode() throws Exception {
    Node testNode = new Node("test");

    Transaction tx = TransactionManager.getInstance()
      .createTransaction();

    tx.start();

    List<Document> doc1List = new ArrayList<>();
    doc1List.add(new Document()
      .addAttribute("test1", "test1"));

    List<UUID> insert1Keys = testNode.insert(doc1List, new InsertOptions().withTransactionID(tx.getID()));

    assertNotNull(insert1Keys);
    assertNull(testNode.getDocument(insert1Keys.get(0)));

    List<Document> doc2List = new ArrayList<>();
    doc2List.add(new Document()
      .addAttribute("test2", "test2"));

    List<UUID> insert2Keys = testNode.insert(doc2List, new InsertOptions().withTransactionID(tx.getID()));

    assertNotNull(insert2Keys);
    assertNull(testNode.getDocument(insert2Keys.get(0)));

    tx.commit();

    assertEquals("test1", testNode.getDocument(insert1Keys.get(0)).getAttribute("test1"));
    assertEquals("test2", testNode.getDocument(insert2Keys.get(0)).getAttribute("test2"));
  }

  @Test
  void insertTxMultipleNodes() throws Exception {
    Node testNode1 = new Node("test1");
    Node testNode2 = new Node("test2");

    Transaction tx = TransactionManager.getInstance().createTransaction();
    tx.start();

    List<Document> doc1List = new ArrayList<>();
    doc1List.add(new Document()
      .addAttribute("test1", "test1"));

    List<UUID> node1Keys = testNode1.insert(doc1List, new InsertOptions().withTransactionID(tx.getID()));
    List<UUID> node2Keys = testNode2.insert(doc1List, new InsertOptions().withTransactionID(tx.getID()));

    assertNotNull(node1Keys);
    assertNotNull(node2Keys);
    assertNull(testNode1.getDocument(node1Keys.get(0)));
    assertNull(testNode2.getDocument(node2Keys.get(0)));

    tx.commit();

    assertEquals("test1", testNode1.getDocument(node1Keys.get(0)).getAttribute("test1"));
    assertEquals("test1", testNode2.getDocument(node2Keys.get(0)).getAttribute("test1"));
  }

  @Test
  void delete() throws Exception {
    Node testNode = new Node("test");

    List<Document> documents = new ArrayList<>();
    documents.add(new Document().addAttribute("test1", 1));
    documents.add(new Document().addAttribute("test1", 1));
    documents.add(new Document().addAttribute("test1", 1));

    List<UUID> insertKeys = testNode.insert(documents, new InsertOptions());
    assertNotNull(insertKeys);
    assertEquals(3, insertKeys.size());

    for (UUID key : insertKeys) {
      assertNotNull(testNode.getDocument(key));
    }

    testNode.delete(new HashSet<UUID>(insertKeys), new DeleteOptions());

    for (UUID key : insertKeys) {
      assertNull(testNode.getDocument(key));
    }
  }
}