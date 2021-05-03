package entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NodeDocument extends Document {

  private final Map<String, Set<NodePtr>> relatedNodes;

  public NodeDocument() {
    super();

    relatedNodes = new HashMap<>();
  }

  public NodeDocument(NodeDocument doc) {
    super(doc);

    // deep copy of related nodes
    relatedNodes = new HashMap<>(doc.getAllRelationships());
  }

  public boolean hasRelationship() {
    return !relatedNodes.isEmpty();
  }

  public NodeDocument addRelationship(String relationshipName, NodePtr relatedNode) {

    synchronized(relatedNodes) {
      if (!relatedNodes.containsKey(relationshipName)) {
        relatedNodes.put(relationshipName, new HashSet<>());
      }

      Set<NodePtr> connectedNodeSet = relatedNodes.get(relationshipName);
      connectedNodeSet.add(relatedNode);
    }

    return this;
  }

  public Set<NodePtr> getAdjacentNodes(String relationshipName) {
    return relatedNodes.get(relationshipName);
  }

  public Map<String, Set<NodePtr>> getAllRelationships() {
    return relatedNodes;
  }

  @Override
  public NodeDocument addAttribute(String attributeName, Object value) {
    super.addAttribute(attributeName, value);
    return this;
  }

  // ToDo: we don't want to expose this publicly, it should be acquired through query
//  public Set<NodePtr> getRelatedNodes(String relationshipName) {
//    return relatedNodes.get(relationshipName);
//  }
}
