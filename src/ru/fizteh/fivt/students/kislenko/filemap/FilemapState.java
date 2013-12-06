package ru.fizteh.fivt.students.kislenko.filemap;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

public class FilemapState extends FatherState {
    private Map<String, String> storage;
    private Path path;

    public FilemapState(Path p) {
        storage = new TreeMap<String, String>();
        path = p;
    }

    public Map<String, String> getMap() {
        return storage;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public boolean alrightPutGetRemove(AtomicReference<Exception> checkingException, AtomicReference<String> message) {
        return true;
    }

    @Override
    public String get(String key, AtomicReference<Exception> exception) {
        return storage.get(key);
    }

    @Override
    public void put(String key, String value, AtomicReference<Exception> exception) {
        storage.put(key, value);
    }

    @Override
    public void remove(String key, AtomicReference<Exception> exception) {
        storage.remove(key);
    }
}
