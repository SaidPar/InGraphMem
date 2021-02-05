package entities;

import java.util.UUID;

public final class NodeID {
  private final String nodeName;
  private final UUID uuid;

  public NodeID(String nodeName, UUID uuid) {
    this.nodeName = nodeName;
    this.uuid = uuid;
  }

  public String getNodeName() {
    return this.nodeName;
  }

  public UUID getUUID() {
    return this.uuid;
  }
}
