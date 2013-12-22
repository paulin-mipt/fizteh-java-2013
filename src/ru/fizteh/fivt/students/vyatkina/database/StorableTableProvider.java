package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.Closeable;
import java.util.List;

public interface StorableTableProvider extends TableProvider,Closeable {

    public List<Class<?>> parseStructedSignature(String structedSignature);

    public void saveChangesOnExit();
}
