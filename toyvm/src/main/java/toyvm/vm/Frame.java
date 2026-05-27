package toyvm.vm;

import java.util.HashMap;
import java.util.Map;

final class Frame {
    private final Map<String, Value> locals = new HashMap<>();

    Value loadLocal(String name) {
        if (!locals.containsKey(name)) {
            throw new IllegalStateException("Unknown local: " + name);
        }
        return locals.get(name);
    }

    void storeLocal(String name, Value value) {
        locals.put(name, value);
    }
}
