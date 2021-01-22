package entities.transactions;

import entities.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Interface to manage CUD operations
 */
public interface Editable {
  List<UUID> insert(List<Document> documents);

  void update(Map<UUID, Document> updateDocuments);

  void delete(Set<UUID> deleteKeys);
}
