package ru.fizteh.fivt.students.piakovenko.filemap.strings;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 12.10.13
 * Time: 23:25
 * To change this template use File | Settings | File Templates.
 */
public class DataBaseMap {
    private Map<String, String> map = new HashMap<String, String>(15);
    private Map<String, String> oldMap = new HashMap<String, String>(15);

    public String put(String key, String value) {
        String oldValue = null;
        if (!map.containsKey(key)) {
            map.put(key, value);
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            oldValue = map.get(key);
            System.out.println(oldValue);
            map.remove(key);
            map.put(key, value);
        }
        return oldValue;
    }

    public String get(String key) {
        if (!map.containsKey(key)) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(map.get(key));
            return map.get(key);
        }
        return null;
    }

    public String remove(String key) {
        if (!map.containsKey(key)) {
            System.out.println("not found");
        } else {
            String returnValue = map.get(key);
            map.remove(key);
            System.out.println("removed");
            return returnValue;
        }
        return null;
    }

    public void primaryPut(String key, String value) {
        map.put(key, value);
    }

    public Map<String, String> getMap() {
        return map;
    }

    public int changed() {
        int changes = 0;
        for (final String key : oldMap.keySet()) {
            if (!map.containsKey(key)) {
                ++changes;
            } else if (!oldMap.get(key).equals(map.get(key))) {
                ++changes;
            }
        }
        for (final String key : map.keySet()) {
            if (!oldMap.containsKey(key)) {
                ++changes;
            }
        }
        return changes;
    }

    public void commit() {
        oldMap.clear();
        oldMap.putAll(map);
    }

    public void rollback() {
        map.clear();
        map.putAll(oldMap);
    }

}
