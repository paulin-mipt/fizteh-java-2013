package ru.fizteh.fivt.students.chernigovsky.junit;

import ru.fizteh.fivt.storage.strings.Table;

import java.util.Map;
import java.util.Set;

public interface ExtendedMultiFileHashMapTable extends Table {
    Set<Map.Entry<String, String>> getEntrySet();
    int getDiffCount();
}
