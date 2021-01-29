package entities;

import java.util.*;

public final class Edge {

  private final String name;
  private final EdgeInternal internalEdge;

  public Edge(String name) {
    this.name = name;
    this.internalEdge = new EdgeInternal();
  }

  // ToDo: think through edge transaction handling as we touch node internals
  public List<UUID> insert() {
    return new ArrayList<>();
  }

  public Map<UUID, EdgeDocument> update() {
    return new HashMap<>();
  }

  public void delete() {
  }
}
