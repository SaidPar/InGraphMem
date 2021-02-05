package entities;

import common.Constants;

public final class EdgeDocument extends Document {

  private NodeID origin;
  private NodeID destination;

  public EdgeDocument() {
    super();
  }

  public EdgeDocument(EdgeDocument document) {
    super (document);
    // todo: check if assignment is deep copy
    this.origin = document.origin;
    this.destination = document.destination;
  }

  public NodeID getOrigin() {
    return this.origin;
  }

  public NodeID getDestination() {
    return this.destination;
  }

  public void setOrigin(NodeID origin) {
    this.origin = origin;
    this.addAttribute(Constants._ORIGIN, origin);
  }

  public void setDestination(NodeID destination) {
    this.destination = destination;
    this.addAttribute(Constants._DESTINATION, destination);
  }
}
