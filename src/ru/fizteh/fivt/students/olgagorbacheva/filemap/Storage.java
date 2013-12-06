package ru.fizteh.fivt.students.olgagorbacheva.filemap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Storage {
      Map<String, String> storage;
      Boolean commited;

      public Storage() {
            storage = new HashMap<String, String>();
            commited = true;
      }

      public boolean put(String key, String value) {
            if (storage.get(key) != null) {
                  return false;
            }
            storage.put(key, value);
            return true;
      }

      public boolean set(String key, String value) {
            if (storage.get(key) == null) {
                  return false;
            }
            storage.put(key, value);
            return true;
      }

      public boolean remove(String key) {
            if (storage.get(key) == null) {
                  return false;
            }
            storage.remove(key);
            return true;
      }

      public String get(String key) {
            if (storage.get(key) == null) {
                  return null;
            }
            return storage.get(key);
      }

      public Map<String, String> getMap() {
            return storage;
      }

      public Set<String> keySet() {
            return storage.keySet();
      }

      public int getSize() {
            return storage.size();
      }

      public void clear() {
            storage.clear();
      }

      // public ... getKeys() {
      // return storage.
      // }

}
