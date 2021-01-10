package database;

import common.Logger;
import entities.Node;
import entities.Relationship;

import java.util.HashMap;
import java.util.Map;

public class Database implements AutoCloseable {

    private static final Logger logger = Logger.getLogger();

    private final String name;
    private final DBType storageType;
    private final Map<String, Node> nodes;
    private final Map<String, Relationship> rels;
    private DBStatus status;

    public Database(DBOptions options) {
        storageType = options.getStorageType();
        name = options.getName();
        status = DBStatus.OFFLINE;

        nodes = new HashMap<String, Node>();
        rels = new HashMap<String, Relationship>();
    }

    public String getName() {
        return this.name;
    }

    public void start() {
        if (status == DBStatus.OFFLINE) {
            status = DBStatus.RUNNING;
            logger.log("Database: '" + name + "' started.");
            logger.log("\tStorage Type: " + storageType);
        } else {
            logger.log("Database: '" + name + "' already running.");
        }
    }

    @Override
    public void close() throws Exception {
        // persist to disk as needed ?
    }
}
