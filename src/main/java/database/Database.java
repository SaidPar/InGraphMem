package database;

import entities.Node;
import entities.Relationship;

import java.util.HashMap;
import java.util.Map;

public final class Database {

    private final DBType storageType;
    private final Map<String, Node> nodes;
    private final Map<String, Relationship> rels;

    public Database(DBOptions options) {
        storageType = options.getStorageType();

        nodes = new HashMap<String, Node>();
        rels = new HashMap<String, Relationship>();
    }

    public void Start() {
        // create file structure if needed?
    }
}
