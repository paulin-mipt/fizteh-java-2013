package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;

import java.io.File;
import java.util.regex.Pattern;

public class WrappedMindfulDataBaseMultiFileHashMap<V> extends MindfulDataBaseMultiFileHashMap<V> {
    private Dispatcher dispatcher;
    private static final Pattern WHITESPACE_PATTERN;

    static {
        WHITESPACE_PATTERN = Pattern.compile("\\s");
    }

    public WrappedMindfulDataBaseMultiFileHashMap(File path, Dispatcher dispatcher, ObjectTransformer<V> transformer) {
        super(path, transformer);
        this.dispatcher = dispatcher;
    }

    protected void checkGetInput(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public V get(String key) {
        checkGetInput(key);
        return super.get(key);
    }

    protected void checkPutInput(String key, V value) {
        if (key == null || key.isEmpty() || WHITESPACE_PATTERN.matcher(key).find()) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public V put(String key, V value) {
        checkPutInput(key, value);
        return super.put(key, value);
    }

    protected void checkRemoveInput(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public V remove(String key) {
        checkRemoveInput(key);
        return super.remove(key);
    }

    @Override
    public int commit() {
        try {
            return super.commit();
        } catch (DataBaseHandler.DataBaseException e) {
            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("Database %s: %s", getName(), e.getMessage()));
        }
        return -1;
    }
}
