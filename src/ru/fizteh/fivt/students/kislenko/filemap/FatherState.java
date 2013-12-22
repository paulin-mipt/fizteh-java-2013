package ru.fizteh.fivt.students.kislenko.filemap;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public abstract class FatherState {
    public abstract Path getPath();

    public abstract boolean alrightPutGetRemove(AtomicReference<Exception> checkingException,
                                                AtomicReference<String> message);

    public abstract String get(String key, AtomicReference<Exception> exception);

    public abstract void put(String key, String value, AtomicReference<Exception> exception);

    public abstract void remove(String key, AtomicReference<Exception> exception);
}
