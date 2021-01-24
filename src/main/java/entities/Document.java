package entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Package private
 */
public final class Document {

  private final Map<String, Object> properties;

  public Document() {
    properties = new HashMap<>();
    //properties.put(Constants._UUID, uuid); // ToDo: evaluate if we need to store uuid in both properties and nodes / rels
  }

  public Document(Document doc) {
    // Deep copy
    properties = new HashMap<>(doc.properties);
  }

  public Document addAttribute(String attributeName, Object value) {
    properties.put(attributeName, value);
    return this;
  }

  public Object getAttribute(String attributeName) {
    return properties.get(attributeName);
  }

  public Map<String, Object> getProperties() {
    return properties;
  }
}
