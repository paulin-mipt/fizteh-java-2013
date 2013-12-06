package ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Paths;

public class TableProviderFactoryImplementation implements TableProviderFactory {

    /**
     * Возвращает объект для работы с базой данных.
     *
     * @param path Директория с файлами базы данных.
     * @return Объект для работы с базой данных, который будет работать в указанной директории.
     * @throws IllegalArgumentException Если значение директории null или имеет недопустимое значение.
     * @throws java.io.IOException      В случае ошибок ввода/вывода.
     */
    @Override
    public TableProvider create(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Null path");
        }
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Empty");
        }
        TableProvider toReturn;
        try {
            toReturn = new TableProviderImplementation(Paths.get(path));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot create tableProvider", e);
        }
        return toReturn;
    }
}
