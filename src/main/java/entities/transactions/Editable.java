package entities.transactions;

import entities.Document;
import entities.edit_options.UpdateOptions;
import exceptions.NodeException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Interface to manage CUD operations
 */
public interface Editable {
  List<UUID> insert(List<Document> documents);

  Map<UUID, Document> update(Map<UUID, Document> updateDocuments, UpdateOptions options) throws NodeException;

  void delete(Set<UUID> deleteKeys);
}
