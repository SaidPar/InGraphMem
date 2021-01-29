package entities;

import common.Constants;

import java.util.UUID;

public final class EdgeDocument extends Document {

  private UUID origin;
  private UUID destination;

  public EdgeDocument() {
    super();
  }

  public EdgeDocument(EdgeDocument document) {
    super (document);
    // todo: deep copy origin / destination
  }

  public void setOrigin(UUID uuid) {
    this.origin = uuid;
    this.addAttribute(Constants._ORIGIN, origin);
  }

  public void setDestination(UUID uuid) {
    this.destination = uuid;
    this.addAttribute(Constants._DESTINATION, destination);
  }
}
