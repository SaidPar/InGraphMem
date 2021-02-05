package entities.transactions.participants;

import entities.Document;
import entities.NodeDocument;
import entities.edit_options.UpdateOptions;
import exceptions.NodeException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Interface to manage CUD operations
 */
public interface EditableNode {
  List<UUID> insert(List<NodeDocument> documents);

  Map<UUID, NodeDocument> update(Map<UUID, NodeDocument> updateDocuments, UpdateOptions options) throws NodeException;

  void delete(Set<UUID> deleteKeys) throws NodeException;
}
