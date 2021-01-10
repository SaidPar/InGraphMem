package entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Node {

    // <Unique Identifier, Properties>
    // ToDo: make thread safe resource
    private final Map<UUID, Properties> documents;

    public Node() {
        documents = new HashMap<>();
    }

    public void insert() {
        // create unique identifier
    }

    public void update() {
        // find uid and update
    }

    public void delete() {
        // remove uid
    }
}
