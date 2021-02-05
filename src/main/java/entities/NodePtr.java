package entities;

import java.util.UUID;

public final class NodePtr {

  private final NodeID nodeID;
  private final Direction direction;

  public NodePtr(NodeID nodeID, Direction direction) {
    this.nodeID = nodeID;
    this.direction = direction;
  }

  public NodeID getNodeID() {
    return this.nodeID;
  }

  public Direction getNodeDirection() {
    return this.direction;
  }
}
