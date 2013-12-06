package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.storage.strings.TableProvider;

public interface StringTableProvider extends TableProvider {

    public void saveChangesOnExit();

}
