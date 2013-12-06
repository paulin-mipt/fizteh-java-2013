package ru.fizteh.fivt.students.chernigovsky.junit;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

public class MultiFileHashMapTableProviderFactory implements TableProviderFactory {
    /**
     * Возвращает объект для работы с базой данных.
     *
     * @param dir Директория с файлами базы данных.
     * @return Объект для работы с базой данных.
     * @throws IllegalArgumentException Если значение директории null или имеет недопустимое значение.
     */
    public ExtendedMultiFileHashMapTableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("dir is null");
        }

        File dbDirectory = new File(dir);
        if (!dbDirectory.exists() || !dbDirectory.isDirectory()) {
            throw new IllegalArgumentException("no such directory");
        }

        return new MultiFileHashMapTableProvider(dbDirectory, false);
    }
}
