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

    // deep copy of member variables
    this.origin = new NodeID(document.origin.getNodeName(), document.origin.getUUID());
    this.destination = new NodeID(document.destination.getNodeName(), document.destination.getUUID());
  }

  public NodeID getOrigin() {
    return this.origin;
  }

  public NodeID getDestination() {
    return this.destination;
  }

  public EdgeDocument setOrigin(NodeID origin) {
    this.origin = origin;
    this.addAttribute(Constants._ORIGIN, origin);
    return this;
  }

  public EdgeDocument setDestination(NodeID destination) {
    this.destination = destination;
    this.addAttribute(Constants._DESTINATION, destination);
    return this;
  }
}
