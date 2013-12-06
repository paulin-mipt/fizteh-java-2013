package ru.fizteh.fivt.students.adanilyak.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.adanilyak.tools.WorkWithDatFiles;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 1:05
 */
public class FileMapTable implements Table {
    private Map<String, String> dataBaseStorage;
    private File dataBaseDatFile;

    public FileMapTable(File datFile) throws IOException {
        dataBaseStorage = new HashMap<>();
        dataBaseDatFile = datFile;
        try {
            WorkWithDatFiles.readIntoMap(dataBaseDatFile, dataBaseStorage);
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
            System.exit(1);
        }
    }

    @Override
    public String getName() {
        System.err.println("getName(): not supported function, null returned");
        return null;
        /*
         not supported function
          */
    }

    @Override
    public String get(String key) {
        return dataBaseStorage.get(key);
    }

    @Override
    public String put(String key, String value) {
        return dataBaseStorage.put(key, value);
    }

    @Override
    public String remove(String key) {
        return dataBaseStorage.remove(key);
    }


    public int size() {
        System.err.println("size(): not supported function, 0 returned");
        return 0;
        /*
         not supported function
          */
    }

    @Override
    public int commit() {
        try {
            WorkWithDatFiles.writeIntoFile(dataBaseDatFile, dataBaseStorage);
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
            System.exit(1);
        }
        return 0;
    }

    @Override
    public int rollback() {
        System.err.println("size(): not supported function, 0 returned");
        return 0;
        /*
         not supported function
          */
    }

    public int exit() {
        try {
            WorkWithDatFiles.writeIntoFile(dataBaseDatFile, dataBaseStorage);
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
            System.exit(1);
        }
        return 0;
    }
}
