package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.extend;

import ru.fizteh.fivt.storage.strings.Table;

public interface ExtendTable extends Table {
    
    int getChangedValuesNumber();

    void loadAll();
}
