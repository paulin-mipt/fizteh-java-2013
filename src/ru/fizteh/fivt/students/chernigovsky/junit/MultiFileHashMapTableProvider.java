package ru.fizteh.fivt.students.chernigovsky.junit;

import ru.fizteh.fivt.students.chernigovsky.filemap.FileMapState;
import ru.fizteh.fivt.students.chernigovsky.multifilehashmap.MultiFileHashMapUtils;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapTableProvider extends AbstractTableProvider<ExtendedMultiFileHashMapTable> implements ExtendedMultiFileHashMapTableProvider {
    public MultiFileHashMapTableProvider(File newDbDirectory, boolean flag) {
        super(newDbDirectory, flag);
        if (newDbDirectory != null) {
            for (String string : newDbDirectory.list()) {
                ExtendedMultiFileHashMapTable newTable = new MultiFileHashMapTable(string, flag);
                tableHashMap.put(string, newTable);
                try {
                    MultiFileHashMapUtils.readTable(new FileMapState(newTable, this));
                } catch (IOException ex) {
                    throw new RuntimeException();
                }
            }
        }
    }

    /**
     * Создаёт таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблица уже существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    public ExtendedMultiFileHashMapTable createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (!name.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("wrong table name");
        }

        if (tableHashMap.get(name) != null) {
            return null;
        }

        File tableDirectory = new File(getDbDirectory(), name);
        if (!tableDirectory.mkdir()) {
            throw new IllegalArgumentException("directory making error");
        }

        ExtendedMultiFileHashMapTable newTable = new MultiFileHashMapTable(name, autoCommit);

        tableHashMap.put(name, newTable);
        return newTable;
    }

}
