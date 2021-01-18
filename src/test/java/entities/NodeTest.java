package entities;

import entities.edit_options.InsertOptions;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

  @org.junit.jupiter.api.Test
  void insert() throws Exception {

    Node testNode = new Node("test");

    List<Properties> documents = new ArrayList<>();
    documents.add(
      new Properties()
        .addAttribute("test1", "test1")
        .addAttribute("test2" ,2)
    );

    testNode.insert(documents, new InsertOptions());

    // ToDo: validation
  }
}