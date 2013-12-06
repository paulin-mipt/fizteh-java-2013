package ru.fizteh.fivt.students.dmitryKonturov.shell;

import java.util.HashMap;
import java.util.Map;

public class ShellInfo {
    private Map<String, Object> property;

    public ShellInfo() {
        property = new HashMap<>();
    }

    public Object getProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null property name");
        }
        return property.get(name);
    }
    public void setProperty(String name, Object object) {
        if (name == null) {
            throw new IllegalArgumentException("Null property name");
        }
        property.put(name, object);
    }
}
