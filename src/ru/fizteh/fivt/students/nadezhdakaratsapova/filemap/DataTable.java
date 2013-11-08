package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap.MultiFileHashMapProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalDataTable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataTable implements Table {
    public static final int DIR_COUNT = 16;
    public static final int FILE_COUNT = 16;


    private UniversalDataTable<String> dataTable;
   /* private File dataBaseDirectory;
    private String tableName;
    private Map<String, String> dataStorage = new HashMap<String, String>();

    private Map<String, String> putKeys = new HashMap<String, String>();
    private Set<String> removeKeys = new HashSet<String>();  */

    public DataTable() {
        dataTable = new UniversalDataTable<String>();
    }

    public DataTable(String name) {
        dataTable = new UniversalDataTable<String>(name);
        //tableName = name;
    }

    public DataTable(String name, File dir) {
        dataTable = new UniversalDataTable<String>(name, dir);
        /*tableName = name;
        dataBaseDirectory = dir;*/
    }

    public String getName() {
        return dataTable.getName();
    }

    public String put(String key, String value) throws IllegalArgumentException {
        if ((key == null) || (key.trim().isEmpty()) || (value == null) || (value.trim().isEmpty())) {
            throw new IllegalArgumentException("Not correct key or value");
        }
        return dataTable.put(key, value);
    }

    public Set<String> getKeys() {
        return dataTable.getKeys();
    }

    public String get(String key) throws IllegalArgumentException {
        return dataTable.get(key);
    }

    public String remove(String key) throws IllegalArgumentException {
        return dataTable.remove(key);
    }

    public boolean isEmpty() {
        return dataTable.isEmpty();
    }

    public int size() {
        return dataTable.size();
    }

    public int commit() {
        return dataTable.commit();
    }

    public int rollback() {
        return dataTable.rollback();
    }

    public int commitSize() {
        return dataTable.commitSize();
    }

    public File getWorkingDirectory() {
        return dataTable.getWorkingDirectory();
    }

    public void load() throws IOException, IllegalArgumentException {
        File curTable = new File(dataTable.getWorkingDirectory(), dataTable.getName());
        curTable = curTable.getCanonicalFile();
        File[] dirs = curTable.listFiles();
        if (dirs.length > DIR_COUNT) {
            throw new IOException("The table includes more than " + DIR_COUNT + " directories");
        }
        for (File d : dirs) {
            if (!d.isDirectory()) {
                throw new IOException(dataTable.getName() + " should include only directories");
            }
            File[] files = d.listFiles();
            if (files.length > FILE_COUNT) {
                throw new IOException("The directory includes more than " + FILE_COUNT + " files");
            }
            String dirName = d.getName();
            char firstChar = dirName.charAt(0);
            char secondChar;
            int dirNumber;
            if (dirName.length() > 1) {
                secondChar = dirName.charAt(1);
            } else {
                throw new IllegalArgumentException("Not allowed name of directory in table");
            }
            if (Character.isDigit(firstChar)) {
                if (Character.isDigit(secondChar)) {
                    dirNumber = Integer.parseInt(dirName.substring(0, 2));
                } else {
                    dirNumber = Integer.parseInt(dirName.substring(0, 1));
                }
            } else {
                throw new IllegalArgumentException("Not allowed name of directory in table");
            }
            if (!dirName.equals(new String(dirNumber + ".dir"))) {
                throw new IllegalArgumentException("Not allowed name of directory in table");
            }
            for (File f : files) {
                if (!f.isFile()) {
                    throw new IOException("Unexpected directory");
                }
                String fileName = f.getName();
                char firstFileChar = fileName.charAt(0);
                char secondFileChar;
                int fileNumber;
                if (fileName.length() > 1) {
                    secondFileChar = fileName.charAt(1);
                } else {
                    throw new IllegalArgumentException("Not allowed name of file in table");
                }
                if (Character.isDigit(firstFileChar)) {
                    if (Character.isDigit(secondFileChar)) {
                        fileNumber = Integer.parseInt(fileName.substring(0, 2));
                    } else {
                        fileNumber = Integer.parseInt(fileName.substring(0, 1));
                    }
                } else {
                    throw new IllegalArgumentException("Not allowed name of file in table");
                }
                if (!fileName.equals(new String(fileNumber + ".dat"))) {
                    throw new IllegalArgumentException("Not allowed name of file in table");
                }
                FileReader fileReader = new FileReader(f, this.dataTable);
                while (fileReader.checkingLoadingConditions()) {
                    String key = fileReader.getNextKey();
                    int hashByte = Math.abs(key.getBytes()[0]);
                    int ndirectory = hashByte % DIR_COUNT;
                    int nfile = (hashByte / DIR_COUNT) % FILE_COUNT;
                    if (ndirectory != dirNumber) {
                        throw new IllegalArgumentException("Wrong key in " + dirName);
                    }
                    if (fileNumber != nfile) {
                        throw new IllegalArgumentException("Wrong key in" + fileName);
                    }
                }
                while (fileReader.valuesToReadExists()) {
                    fileReader.putStringValueToTable(fileReader.getNextValue());
                }
                fileReader.closeResources();
            }
        }
    }

    public void writeToDataBase() throws IOException {
        dataTable.rollback();
        Set<String> keys = dataTable.getKeys();
        if (!keys.isEmpty()) {
            for (int i = 0; i < DIR_COUNT; ++i) {
                File dir = new File(new File(dataTable.getWorkingDirectory(), dataTable.getName()), new String(i + ".dir"));
                for (int j = 0; j < FILE_COUNT; ++j) {
                    DataTable keysToFile = new DataTable();
                    File file = new File(dir, new String(j + ".dat"));
                    for (String key : keys) {
                        int hashByte = Math.abs(key.getBytes()[0]);
                        int ndirectory = hashByte % DIR_COUNT;
                        int nfile = (hashByte / DIR_COUNT) % FILE_COUNT;
                        if ((ndirectory == i) && (nfile == j)) {
                            if (!dir.getCanonicalFile().exists()) {
                                dir.getCanonicalFile().mkdir();
                            }

                            if (!file.getCanonicalFile().exists()) {
                                file.getCanonicalFile().createNewFile();
                            }
                            keysToFile.put(key, dataTable.get(key));
                            keysToFile.commit();
                        }
                    }

                    if (!keysToFile.isEmpty()) {
                        FileWriter fileWriter = new FileWriter();
                        fileWriter.writeDataToFile(file.getCanonicalFile(), keysToFile);
                    } else {
                        if (file.getCanonicalFile().exists()) {
                            file.getCanonicalFile().delete();
                        }
                    }
                }
                if (dir.getCanonicalFile().listFiles() == null) {
                    dir.delete();
                }
            }
        }
    }
}

