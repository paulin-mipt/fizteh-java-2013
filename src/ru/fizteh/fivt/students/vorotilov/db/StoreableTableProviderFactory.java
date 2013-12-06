package ru.fizteh.fivt.students.vorotilov.db;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

/**
 * Представляет интерфейс для создание экземпляров {@link ru.fizteh.fivt.storage.structured.TableProvider}.
 *
 * Предполагается, что реализация интерфейса фабрики будет иметь публичный конструктор без параметров.
 */
public class StoreableTableProviderFactory implements TableProviderFactory {

    /**
     * Возвращает объект для работы с базой данных.
     *
     * @param path Директория с файлами базы данных.
     * @return Объект для работы с базой данных, который будет работать в указанной директории.
     *
     * @throws IllegalArgumentException Если значение директории null или имеет недопустимое значение.
     * @throws java.io.IOException В случае ошибок ввода/вывода.
     */

    @Override
    public StoreableTableProvider create(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Dir with tables is null");
        }
        if (path.trim().equals("")) {
            throw new IllegalArgumentException("Dir is empty string");
        }
        if (path.contains("..")) {
            throw new IllegalArgumentException("Dir includes ..");
        }
        File rootDir = (new File(path)).getCanonicalFile();
        if (rootDir.exists()) {
            if (!rootDir.isDirectory()) {
                throw new IllegalArgumentException("Proposed object is not directory");
            }
        } else {
            if (!rootDir.mkdirs()) {
                throw new IOException("Can't create root directory: " + path);
            }
        }
        return new StoreableTableProvider(rootDir);
    }

}
