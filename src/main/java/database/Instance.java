package database;

import java.util.HashMap;
import java.util.Map;

public final class Instance {

  private static Instance INSTANCE = new Instance();
  private final Map<String, Database> databases = new HashMap<>();

  private Instance() {
  }

  public static Instance getInstance() {
    return INSTANCE;
  }

  public void addDatabase(Database db) {
    synchronized (databases) {
      if (!databases.containsKey(db.getName()))
        databases.put(db.getName(), db);
    }
  }

  public Database getDatabase(String name) {
    return databases.get(name);
  }

  public Map<String, Database> getDatabases() {
    return databases;
  }
}
