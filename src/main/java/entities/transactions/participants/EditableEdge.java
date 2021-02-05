package entities.transactions.participants;

import entities.EdgeDocument;
import entities.edit_options.UpdateOptions;
import exceptions.EdgeException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface EditableEdge {
  List<UUID> insert(List<EdgeDocument> documents);

  Map<UUID, EdgeDocument> update(Map<UUID, EdgeDocument> updateDocuments, UpdateOptions options) throws EdgeException;

  void delete(Set<UUID> deleteKeys) throws EdgeException;
}
