package entities;

import common.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Properties {

    private final Map<String, Object> properties;

    public Properties(UUID uuid) {
        properties = new HashMap<>();
        properties.put(Constants._UUID, uuid);
    }
}
