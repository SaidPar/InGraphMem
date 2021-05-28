package entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NodeDocument extends Document {

  private final Map<String, Set<NodePtr>> relatedNodes;
  private final Map<String, Set<NodePtr>> removeNodes;

  public NodeDocument() {
    super();

    relatedNodes = new HashMap<>();
    removeNodes = new HashMap<>();
  }

  public NodeDocument(NodeDocument doc) {
    super(doc);

    // deep copy of related nodes
    relatedNodes = new HashMap<>(doc.getAllRelationships());
    removeNodes = new HashMap<>();
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

  public NodeDocument removeRelationship(String relationshipName, NodePtr relatedNode) {
    synchronized (relatedNodes) {
      synchronized(removeNodes) {
        if (!removeNodes.containsKey(relationshipName)) {
          removeNodes.put(relationshipName, new HashSet<>());
        }

        Set<NodePtr> connectedNodeSet = relatedNodes.get(relationshipName);
        if (connectedNodeSet != null) {
          connectedNodeSet.remove(relatedNode);
        }

        Set<NodePtr> removeNodeSet = removeNodes.get(relationshipName);
        removeNodeSet.add(relatedNode);
      }
    }

    return this;
  }

  public boolean hasRemoveNodes() {
    return !removeNodes.isEmpty();
  }

  public Map<String, Set<NodePtr>> getAllRemoveNodes() {
    return removeNodes;
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
}
