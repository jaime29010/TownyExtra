package me.jaimemartz.townyextra;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonDataPool {
    private final Map<UUID, Boolean> status;

    public JsonDataPool() {
        status = Collections.synchronizedMap(new HashMap<>());
    }

    public Map<UUID, Boolean> getToggleStatus() {
        return status;
    }
}
