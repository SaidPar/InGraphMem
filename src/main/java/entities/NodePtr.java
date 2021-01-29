package entities;

import java.util.UUID;

public final class NodePtr {

  private final UUID uuid;
  private final Direction direction;

  public NodePtr(UUID uuid, Direction direction) {
    this.uuid = uuid;
    this.direction = direction;
  }

  public UUID getNodeID() {
    return this.uuid;
  }

  public Direction getNodeDirection() {
    return this.direction;
  }
}
