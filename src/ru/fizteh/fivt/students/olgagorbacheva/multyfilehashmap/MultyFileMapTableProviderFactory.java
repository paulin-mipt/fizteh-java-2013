package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

import java.io.File;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class MultyFileMapTableProviderFactory implements TableProviderFactory {

      @Override
      public MultyFileMapTableProvider create(String dir) throws IllegalArgumentException {
            if (dir == null || dir.isEmpty()) {
                  throw new IllegalArgumentException("Недопустимое имя хранилища базы данных");
            }
            File directory = new File(dir);
            if (!directory.exists()) {
                  throw new IllegalArgumentException("Директории с данным именем не существует");
            }
            if (!directory.isDirectory()) {
                  throw new IllegalArgumentException("Файл с данным именем не является директорией");
            }
            if (!directory.canExecute() || !directory.canRead() || !directory.canWrite()) {
                  throw new IllegalArgumentException("Директория недоступна");
            }
            MultyFileMapTableProvider tableProvider = new MultyFileMapTableProvider(dir);
            return tableProvider;
      }

}
