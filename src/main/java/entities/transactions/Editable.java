package entities.transactions;

import entities.Properties;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Interface to manage CUD operations
 */
public interface Editable {
  void insert(List<Properties> documents);

  void update(Map<UUID, Properties> updateDocuments);

  void delete(Set<UUID> deleteKeys);
}
