package ru.fizteh.fivt.students.abramova.multifilehashmap;

import ru.fizteh.fivt.students.abramova.filemap.FileMap;
import ru.fizteh.fivt.students.abramova.shell.RemoveCommand;
import ru.fizteh.fivt.students.abramova.shell.Stage;
import ru.fizteh.fivt.students.abramova.shell.Status;

import java.io.*;
import java.nio.file.Files;

public class TableMultiFile implements Closeable {
    private final File tableDir;
    private final String tableName;
    //Изначально храним все существующие (на момент открытия таблицы) файлы в двумерном массиве
    //Первый индекс означает директорию, в которой находится файл, второй - номер файла
    FileMap[][] files = new FileMap[16][];

    {
        for (int i = 0; i < 16; i++) { //Выделяем память для всех директорий
            files[i] = new FileMap[16];
        }
    }

    public TableMultiFile(String tableName, String directory) throws IOException {
        this.tableName = tableName;
        tableDir = new File(directory, tableName);
        File currentDir; //Текущая дериктория *.dir
        File currentFile;   //Текущий файл *.dat
        if (tableDir.exists() && tableDir.isDirectory()) {  //Если таблица не новая
            //Обход по всем возможным папкам
            for (int i = 0; i < 16; i++) {
                currentDir = new File(tableDir.toString(), i + ".dir");
                //Если директория с таким именем существует
                if (currentDir.exists() && currentDir.isDirectory()) {
                    //Обход по всем возможным файлам, создание или чтение из них
                    for (int j = 0; j < 16; j++) {
                        currentFile = new File(currentDir.toString(), j + ".dat");
                        if (currentFile.exists()) { //Если файл существует, то записываем его
                            files[i][j] = new FileMap(j + ".dat", currentDir.getCanonicalPath());
                            if (!isCorrectFile(i, j, files[i][j])) {
                                throw new IOException("Bad file");
                            }
                        } else {
                            files[i][j] = null;
                        }
                    }
                } else {    //Иначе нет существующих файлов
                    files[i] = null;
                }
            }
        } else {    //Иначе создаем ее
            if (!tableDir.mkdir()) {
                throw new IOException("Table " + tableName + " was not created");
            }
        }
    }

    private boolean isCorrectFile(int ndirectory, int nfile, FileMap file) {
        int hashcode;
        for (String key : file.getMap().keySet()) {
            hashcode = Math.abs(key.hashCode());
            if (ndirectory != hashcode % 16 || nfile != hashcode / 16 % 16) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void close() throws IOException {
        File currentDir;
        File currentFile;
        for (int i = 0; i < 16; i++) {
            currentDir = new File(tableDir.toString(), i + ".dir");
            if (files[i] != null) {   //Если директория должна быть не путса
                for (int j = 0; j < 16; j++) {
                    if (files[i][j] != null && !files[i][j].getMap().isEmpty()) {  //Если в этот файл что-то записано, то сохраняем на диск
                        if (!currentDir.exists()) {
                            if (!currentDir.mkdir()) {
                                throw new IOException("Directory " + i + ".dir in table " + tableName + " was not created");
                            }
                        }
                        currentFile = new File(currentDir.toString(), j + ".dat");
                        if (!currentFile.exists()) {
                            if (!currentDir.createNewFile()) {
                                throw new IOException("File " + j + ".dat in directory " + i + ".dir in table " + tableName + " was not created");
                            }
                        }
                        files[i][j].close();
                    } else {    //Иначе удаляем его, если он существовал
                        Files.deleteIfExists(new File(tableDir.toString(), new File(i + ".dir", j + ".dat").toString()).toPath());
                    }
                }
            } else if (currentDir.exists()) { //Иначе удаляем директорию, если она существовала с помощью команды remove из шелла
                String[] args = new String[1];
                args[0] = i + ".dir";
                new RemoveCommand(tableName).doCommand(args, new Status(new Stage(tableDir.getPath())));
            }
        }
    }

    public String getTableName() {
        return tableName;
    }

    public FileMap findFileMap(String key) {
        int hashcode = Math.abs(key.hashCode());
        int ndirectory = hashcode % 16;
        int nfile = hashcode / 16 % 16;
        if (files[ndirectory] == null || files[ndirectory][nfile] == null) {
            return null;
        }
        return files[ndirectory][nfile];
    }

    //Добавлят новый файл для соответствующего ключа и возвращает полученный пустой файл
    //если файл уже существует, то возвращает его
    public FileMap addFileMap(String key) throws IOException{
        FileMap oldFile = findFileMap(key);
        if (oldFile != null) {
            return oldFile;
        }
        int hashcode = Math.abs(key.hashCode());
        int ndirectory = hashcode % 16;
        int nfile = hashcode / 16 % 16;
        File dir = new File(tableDir.getCanonicalPath(), ndirectory + ".dir");
        if (files[ndirectory] == null) {
            files[ndirectory] = new FileMap[16];
            if (!dir.exists() && !dir.mkdir()) {
                throw new IOException(tableName + ": Opening error");
            }
            for (int i = 0; i < 16; i++) {
                if (i != nfile) {
                    files[ndirectory][i] = null;
                }
            }
        }
        File dat = new File(dir.getPath(), nfile + ".dat");
        if (dat.exists() && !dat.delete()) {
                throw new IOException(tableName + ": " + dat.getName() + " was not removed");
        }
        files[ndirectory][nfile] = new FileMap(nfile + ".dat", dir.getCanonicalPath());
        return files[ndirectory][nfile];
    }

    //Зануляет указатели на нулевые файлы и директории
    //Принимает ключ, чтобы найти место, где мог опустеть файл
    public void correctingFiles(String key) {
        int hashcode = Math.abs(key.hashCode());
        int ndirectory = hashcode % 16;
        int nfile = hashcode / 16 % 16;
        if (files[ndirectory] != null && files[ndirectory][nfile] != null &&
                files[ndirectory][nfile].getMap().isEmpty()) {  //Если файл опустел
            files[ndirectory][nfile] = null;
        }
        int nullPointer = 0;    //Количество пустых файлов в папке
        while (nullPointer < 16 && files[ndirectory][nullPointer] == null) {
            nullPointer++;
        }
        if (nullPointer == 16) {    //Если все файлы в директории пустые
            files[ndirectory] = null;
        }
    }

    public File getTableDir() {
        return tableDir;
    }

    public int size() {
        int size = 0;
        for (int i = 0; i < 16; i++) {
            if (files[i] != null) {
                for (int j = 0; j < 16; j++) {
                    size += files[i][j] == null ? 0 : files[i][j].getMap().size();
                }
            }
        }
        return size;
    }
}
