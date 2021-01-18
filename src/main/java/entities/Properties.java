package entities;

import common.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Package private
 */
public final class Properties {

  private final Map<String, Object> properties;

  public Properties() {
    properties = new HashMap<>();
    //properties.put(Constants._UUID, uuid); // ToDo: evaluate if we need to store uuid in both properties and nodes / rels
  }

  public Properties addAttribute(String attributeName, Object value) {
    properties.put(attributeName, value);
    return this;
  }
}
