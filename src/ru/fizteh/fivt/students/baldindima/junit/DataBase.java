package ru.fizteh.fivt.students.baldindima.junit;

import java.io.File;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.IOException;

public class DataBase implements Table {
    private String dataBaseDirectory;
    private DataBaseFile[] files;

    private void checkNames(String[] fileList, String extension) throws IOException {
        for (String fileNumber : fileList) {
            String[] nameFile = fileNumber.split("\\.");
            if ((nameFile.length != 2)
                    || !nameFile[1].equals(extension)) {
                throw new IOException(dataBaseDirectory + " wrong file " + fileNumber);
            }
            int intName;
            try {
                intName = Integer.parseInt(nameFile[0]);
            } catch (NumberFormatException e) {
                throw new IOException(dataBaseDirectory + " wrong name of file" + fileNumber);
            }
            if ((intName < 0) || (intName > 15))
                throw new IOException(dataBaseDirectory + " wrong name of file" + fileNumber);
        }
    }

    private void checkCorrectionDirectory(String directoryName) throws IOException {
        File file = new File(directoryName);
        if (file.isFile()) {
            throw new IOException(directoryName + " isn't a directory!");
        }
        checkNames(file.list(), "dat");
        for (String fileName : file.list()) {
            if (new File(directoryName, fileName).isDirectory()) {
                throw new IOException(directoryName + File.separator + fileName + " isn't a file!");
            }
        }
    }

    private void checkCorrection() throws IOException {
        File file = new File(dataBaseDirectory);
        if (!file.exists()) {
            throw new IOException(dataBaseDirectory + " isn't exist");
        }
        checkNames(file.list(), "dir");
        for (String fileNumber : file.list()) {
            checkCorrectionDirectory(dataBaseDirectory + File.separator + fileNumber);
        }


    }

    public DataBase(String nameDirectory) throws IOException {
        dataBaseDirectory = nameDirectory;
        checkCorrection();
        files = new DataBaseFile[256];

        loadDataBase();

    }

    private void addDirectory(final String directoryName) throws IOException {
        File file = new File(dataBaseDirectory + File.separator + directoryName);
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new IOException("Cannot create a directory!");
            }
        }
    }

    private String getFullName(int nDir, int nFile) {
        return dataBaseDirectory + File.separator + Integer.toString(nDir)
                + ".dir" + File.separator + Integer.toString(nFile) + ".dat";
    }

    private void loadDataBase() throws IOException {
        try {
            for (int i = 0; i < 16; ++i) {
                addDirectory(Integer.toString(i) + ".dir");
                for (int j = 0; j < 16; ++j) {
                    int nFile = j;
                    int nDir = i;
                    DataBaseFile file = new DataBaseFile(getFullName(i, j), i, j);
                    files[i * 16 + j] = file;
                }

            }
        } catch (IOException e) {
            saveDataBase();
            throw e;
        }

    }

    private void deleteEmptyDirectory(final String name) throws IOException {
        File file = new File(dataBaseDirectory + File.separator + name);
        if (file.exists()) {
            if (file.list().length == 0) {
                if (!file.delete()) {
                    throw new IOException("Cannot delete a directory!");
                }
            }
        }
    }

    public void drop() throws IOException {
        for (byte i = 0; i < 16; ++i) {
            for (byte j = 0; j < 16; ++j) {
                File file = new File(getFullName(i, j));
                if (file.exists()) {
                    if (!file.isFile()) {
                        throw new IOException("It isn't a file!");
                    }
                    if (!file.delete()) {
                        throw new IOException("Cannot delete a file!");
                    }
                }
            }
            deleteEmptyDirectory(Integer.toString(i) + ".dir");
        }
    }

    public void saveDataBase() throws IOException {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (!(files[i * 16 + j].getCurrentTable().isEmpty())) {
                    files[i * 16 + j].write();
                } else {
                    File file = new File(getFullName(i, j));
                    if (file.exists()) {
                        if (!file.delete())
                            throw new IOException("Cannot delete a file");
                    }
                }
            }
            deleteEmptyDirectory(Integer.toString(i) + ".dir");
        }
    }

    public String get(String keyString) {
        checkString(keyString);
        int nDir = Math.abs(keyString.getBytes()[0]) % 16;
        int nFile = Math.abs((keyString.getBytes()[0] / 16) % 16);
        DataBaseFile file = files[nDir * 16 + nFile];
        return file.get(keyString);
    }

    public String put(String keyString, String valueString) {
        checkString(keyString);
        checkString(valueString);
        int nDir = Math.abs(keyString.getBytes()[0]) % 16;
        int nFile = Math.abs((keyString.getBytes()[0] / 16) % 16);
        DataBaseFile file = files[nDir * 16 + nFile];
        return file.put(keyString, valueString);
    }

    public String remove(String keyString) {
        checkString(keyString);
        int nDir = Math.abs(keyString.getBytes()[0]) % 16;
        int nFile = Math.abs((keyString.getBytes()[0] / 16) % 16);
        DataBaseFile file = files[nDir * 16 + nFile];
        return file.remove(keyString);
    }

    public int countCommits() {
        int count = 0;
        for (int i = 0; i < 256; ++i) {
            count += files[i].countCommits();
        }
        return count;
    }

    public int commit() {
        int count = 0;
        for (int i = 0; i < 256; ++i) {
            count += files[i].countCommits();
            try {
                files[i].commit();
            } catch (IOException e) {
                throw new RuntimeException("cannot do commit");
            }
        }
        return count;
    }

    public int rollback() {
        int count = 0;
        for (int i = 0; i < 256; ++i) {
            count += files[i].countCommits();
            try {
                files[i].rollback();
            } catch (IOException e) {
                throw new RuntimeException("cannot do rollback");
            }
        }
        return count;
    }

    public int size() {
        int count = 0;
        for (int i = 0; i < 256; ++i) {
            count += files[i].countSize();
        }
        return count;
    }

    public String getName() {
        return new File(dataBaseDirectory).getName();
    }

    private void checkString(String str) {
        if ((str == null) || (str.trim().length() == 0)) {
            throw new IllegalArgumentException("Wrong key!");
        }
    }


}
