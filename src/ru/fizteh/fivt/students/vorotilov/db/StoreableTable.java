package ru.fizteh.fivt.students.vorotilov.db;

import ru.fizteh.fivt.storage.structured.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Представляет интерфейс для работы с таблицей, содержащей ключи-значения. Ключи должны быть уникальными.
 *
 * Транзакционность: изменения фиксируются или откатываются с помощью методов {@link #commit()} или {@link #rollback()},
 * соответственно. Предполагается, что между вызовами этих методов никаких операций ввода-вывода не происходит.
 *
 * Данный интерфейс не является потокобезопасным.
 */
public class StoreableTable implements Table {

    private TableProvider tableProvider;
    private List<Class<?>> columnTypes;
    private final File tableRootDir;
    private TableFile[][] tableFiles = new TableFile[16][16];

    private HashMap<String, Storeable> tableOnDisk = new HashMap<>();

    private final ThreadLocal<HashMap<String, Storeable>> changedKeys = new ThreadLocal<HashMap<String, Storeable>>() {
        @Override
        protected HashMap<String, Storeable> initialValue() {
            return new HashMap<>();
        }
    };

    private final ThreadLocal<HashSet<String>> removedKeys = new ThreadLocal<HashSet<String>>() {
        @Override
        protected HashSet<String> initialValue() {
            return new HashSet<>();
        }
    };

    private final ReadWriteLock tableLock = new ReentrantReadWriteLock(true);

    private void index() {
        tableLock.writeLock().lock();
        try {
            File[] subDirsList = tableRootDir.listFiles();
            if (subDirsList != null) {
                for (File subDir: subDirsList) {
                    int numberOfSubDir;
                    if (subDir.getName().equals(SignatureFile.signatureFileName)) {
                        continue;
                    }
                    if (!subDir.isDirectory()) {
                        throw new IllegalStateException("In table root dir found object is not a directory");
                    }
                    String[] tableSubDirName = subDir.getName().split("[.]");
                    try {
                        numberOfSubDir = Integer.parseInt(tableSubDirName[0]);
                    } catch (NumberFormatException e) {
                        throw new IllegalStateException("Table root directory contains not 0.dir ... 15.dir");
                    }
                    if (numberOfSubDir < 0 || numberOfSubDir > 15
                            || !tableSubDirName[1].equals("dir") || tableSubDirName.length != 2) {
                        throw new IllegalStateException("Table root directory contains not 0.dir ... 15.dir");
                    }
                    File[] subFilesList = subDir.listFiles();
                    if (subFilesList != null && subFilesList.length == 0) {
                        throw new IllegalStateException("data base contains empty dir");
                    }
                    if (subFilesList != null) {
                        for (File subFile: subFilesList) {
                            int numberOfSubFile;
                            if (!subFile.isFile()) {
                                throw new IllegalStateException("In table sub dir found object is not a file");
                            }
                            String[] dbFileName = subFile.getName().split("[.]");
                            try {
                                numberOfSubFile = Integer.parseInt(dbFileName[0]);
                            } catch (NumberFormatException e) {
                                throw new IllegalStateException("Table sub directory contains not 0.dat ... 15.dat");
                            }
                            if (numberOfSubFile < 0 || numberOfSubFile > 15
                                    || !dbFileName[1].equals("dat") || dbFileName.length != 2) {
                                throw new IllegalStateException("Table sub directory contains not 0.dat ... 15.dat");
                            } else if (subFile.length() == 0) {
                                throw new IllegalStateException("Empty file in sub dir");
                            } else {
                                tableFiles[numberOfSubDir][numberOfSubFile] = new TableFile(subFile);
                                List<TableFile.Entry> fileData =
                                        tableFiles[numberOfSubDir][numberOfSubFile].readEntries();
                                for (TableFile.Entry i : fileData) {
                                    HashcodeDestination dest = new HashcodeDestination(i.getKey());
                                    if (dest.getFile() != numberOfSubFile || dest.getDir() != numberOfSubDir) {
                                        throw new IllegalStateException("Wrong key placement");
                                    }
                                    try {
                                        tableOnDisk.put(i.getKey(),
                                                tableProvider.deserialize(this, i.getValue()));
                                    } catch (ParseException e) {
                                        throw new IllegalStateException("Can't deserialize", e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            tableLock.writeLock().unlock();
        }

    }

    StoreableTable(TableProvider tableProvider, File tableRootDir, List<Class<?>> classes) {
        if (tableRootDir == null) {
            throw new IllegalArgumentException("Table root dir is null");
        } else if (!tableRootDir.exists()) {
            throw new IllegalArgumentException("Proposed root dir not exists");
        } else if (!tableRootDir.isDirectory()) {
            throw new IllegalArgumentException("Proposed object is not directory");
        }
        if (classes == null) {
            throw new IllegalArgumentException("Column type is null");
        }
        this.tableProvider = tableProvider;
        this.tableRootDir = tableRootDir;
        columnTypes = classes;
        try {
            SignatureFile.createSignature(tableRootDir, columnTypes);
        } catch (IOException e) {
            throw new IllegalStateException("Can't write signature", e);
        }
        index();
    }

    StoreableTable(TableProvider tableProvider, File tableRootDir) {
        if (tableRootDir == null) {
            throw new IllegalArgumentException("Table root dir is null");
        } else if (!tableRootDir.exists()) {
            throw new IllegalArgumentException("Proposed root dir not exists");
        } else if (!tableRootDir.isDirectory()) {
            throw new IllegalArgumentException("Proposed object is not directory");
        }
        this.tableProvider = tableProvider;
        this.tableRootDir = tableRootDir;
        try {
            columnTypes = SignatureFile.readSignature(tableRootDir);
        } catch (IOException e) {
            throw new IllegalStateException("Can't read signature", e);
        }
        index();
    }

    /**
     * Возвращает название таблицы.
     *
     * @return Название таблицы.
     */
    @Override
    public String getName() {
        return tableRootDir.getName();
    }

    /**
     * Получает значение по указанному ключу.
     *
     * @param key Ключ для поиска значения. Не может быть null.
     * @return Значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    @Override
    public Storeable get(String key) {
        checkKey(key);
        Storeable value = changedKeys.get().get(key);
        if (value == null) {
            if (removedKeys.get().contains(key)) {
                return null;
            } else {
                tableLock.readLock().lock();
                try {
                    return tableOnDisk.get(key);
                } finally {
                    tableLock.readLock().unlock();
                }
            }
        } else {
            return value;
        }
    }

    /**
     * Устанавливает значение по указанному ключу.
     *
     * @param key Ключ для нового значения. Не может быть null.
     * @param value Новое значение. Не может быть null.
     * @return Значение, которое было записано по этому ключу ранее. Если ранее значения не было записано,
     * возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметров key или value является null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - при попытке передать Storeable с колонками другого типа.
     */
    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        checkKey(key);
        checkValue(value);
        Storeable valueOnDisk = null;
        Storeable oldValue = changedKeys.get().put(key, value);
        if (!removedKeys.get().contains(key) && oldValue == null) {
            tableLock.readLock().lock();
            try {
                valueOnDisk = tableOnDisk.get(key);
            } finally {
                tableLock.readLock().unlock();
            }
            oldValue = valueOnDisk;
            if (valueOnDisk != null) {
                removedKeys.get().add(key);
            }
        }
        return oldValue;
    }

    /**
     * Удаляет значение по указанному ключу.
     *
     * @param key Ключ для поиска значения. Не может быть null.
     * @return Предыдущее значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    @Override
    public Storeable remove(String key) {
        checkKey(key);
        if (key == null) {
            throw new IllegalArgumentException("table remove: key is null");
        }
        Storeable value = changedKeys.get().get(key);
        if (value == null) {
            if (!removedKeys.get().contains(key)) {
                tableLock.readLock().lock();
                try {
                    value = tableOnDisk.get(key);
                } finally {
                    tableLock.readLock().unlock();
                }
                if (value != null) {
                    removedKeys.get().add(key);
                }
            }
        } else {
            changedKeys.get().remove(key);
            tableLock.readLock().lock();
            try {
                if (tableOnDisk.containsKey(key)) {
                    removedKeys.get().add(key);
                }
            } finally {
                tableLock.readLock().unlock();
            }
        }
        return value;
    }

    /**
     * Возвращает количество ключей в таблице. Возвращает размер текущей версии, с учётом незафиксированных изменений.
     *
     * @return Количество ключей в таблице.
     */
    @Override
    public int size() {
        int count = 0;
        tableLock.readLock().lock();
        try {
            count = tableOnDisk.size();
            //удаляем те которые с коммитом уйдут в мир иной
            for (String currentKey : removedKeys.get()) {
                if (tableOnDisk.containsKey(currentKey) && !changedKeys.get().containsKey(currentKey)) {
                    --count;
                }
            }
            //добавляем те которые запишутся на диск при коммите
            for (String currentKey : changedKeys.get().keySet()) {
                if (!removedKeys.get().contains(currentKey) && !tableOnDisk.containsKey(currentKey)) {
                    ++count;
                }
            }
            return count;
        } finally {
            tableLock.readLock().unlock();
        }
    }

    /**
     * Выполняет фиксацию изменений.
     *
     * @return Число записанных изменений.
     *
     * @throws java.io.IOException если произошла ошибка ввода/вывода. Целостность таблицы не гарантируется.
     */
    @Override
    public int commit() throws IOException {
        tableLock.writeLock().lock();
        try {
            int count = uncommittedChanges();
            for (String currentKey : removedKeys.get()) {
                tableOnDisk.remove(currentKey);
            }
            tableOnDisk.putAll(changedKeys.get());
            Set<Map.Entry<String, Storeable>> dbSet = tableOnDisk.entrySet();
            for (int nDir = 0; nDir < 16; ++nDir) {
                for (int nFile = 0; nFile < 16; ++nFile) {
                    List<TableFile.Entry> fileData = new ArrayList<>();
                    Iterator<Map.Entry<String, Storeable>> iter = dbSet.iterator();
                    while (iter.hasNext()) {
                        Map.Entry<String, Storeable> tempMapEntry = iter.next();
                        HashcodeDestination dest = new HashcodeDestination(tempMapEntry.getKey());
                        if (dest.getDir() == nDir && dest.getFile() == nFile) {
                            fileData.add(new TableFile.Entry(tempMapEntry.getKey(),
                                    tableProvider.serialize(this, tempMapEntry.getValue())));
                        }
                    }
                    if (fileData.isEmpty()) {
                        continue;
                    }
                    if (tableFiles[nDir][nFile] == null) {
                        File subDir = new File(tableRootDir, Integer.toString(nDir) + ".dir");
                        File subFile = new File(subDir, Integer.toString(nFile) + ".dat");
                        if (!subDir.exists()) {
                            if (!subDir.mkdir()) {
                                throw new IllegalStateException("Sub dir was not created");
                            }
                        }
                        tableFiles[nDir][nFile] = new TableFile(subFile);
                    }
                    tableFiles[nDir][nFile].writeEntries(fileData);
                }
            }
            changedKeys.get().clear();
            removedKeys.get().clear();
            return count;
        } finally {
            tableLock.writeLock().unlock();
        }
    }

    /**
     * Выполняет откат изменений с момента последней фиксации.
     *
     * @return Число откаченных изменений.
     */
    @Override
    public int rollback() {
        tableLock.readLock().lock();
        try {
            int numberOfRolledChanges = uncommittedChanges();
            changedKeys.get().clear();
            removedKeys.get().clear();
            return numberOfRolledChanges;
        } finally {
            tableLock.readLock().unlock();
        }
    }

    public void close() throws IOException {
        File[] listOfSubDirs = tableRootDir.listFiles();
        if (listOfSubDirs != null) {
            for (File subDir : listOfSubDirs) {
                if (subDir.exists()) {
                    File[] listOfFiles = subDir.listFiles();
                    if (listOfFiles != null && listOfFiles.length == 0) {
                        if (!subDir.delete()) {
                            throw new IllegalStateException("Can't delete empty sub dir");
                        }
                    }
                }
            }
        }
    }

    public int uncommittedChanges() {
        int count = 0;
        //удаленные с диска
        for (String currentKey : removedKeys.get()) {
            if (tableOnDisk.containsKey(currentKey)) {
                Storeable currentValue = changedKeys.get().get(currentKey);
                if (currentValue == null || !currentValue.equals(tableOnDisk.get(currentKey))) {
                    ++count;
                }
            }
        }
        //измененные
        for (String currentKey : changedKeys.get().keySet()) {
            if (!tableOnDisk.containsKey(currentKey)) {
                ++count;
            }
        }
        return count;
    }

    /**
     * Возвращает количество колонок в таблице.
     *
     * @return Количество колонок в таблице.
     */
    @Override
    public int getColumnsCount() {
        return columnTypes.size();
    }

    /**
     * Возвращает тип значений в колонке.
     *
     * @param columnIndex Индекс колонки. Начинается с нуля.
     * @return Класс, представляющий тип значения.
     *
     * @throws IndexOutOfBoundsException - неверный индекс колонки
     */
    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columnTypes.size()) {
            throw new IndexOutOfBoundsException();
        }
        return columnTypes.get(columnIndex);
    }

    public List<Class<?>> getColumnTypes() {
        return columnTypes;
    }

    private void checkKey(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("Key is empty");
        }
        if (key.contains(" ") || key.contains("\t") || key.contains("\n")
                || key.contains("\\x0B") || key.contains("\f") || key.contains("\r")) {
            throw new IllegalArgumentException("Kay contains whitespaces");
        }
    }

    private void checkValue(Storeable value) throws ColumnFormatException {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        try {
            for (int i = 0; i < columnTypes.size(); ++i) {
                if (value.getColumnAt(i) != null && !columnTypes.get(i).equals(value.getColumnAt(i).getClass())) {
                    throw new ColumnFormatException("Wrong column type. was: "
                            + value.getColumnAt(i).getClass().toString() + "; expected: " + columnTypes.get(i));
                }
            }
            boolean unusedValue = true;
            try {
                value.getColumnAt(columnTypes.size());
            } catch (IndexOutOfBoundsException e) {
                unusedValue = false;
            }
            if (unusedValue) {
                throw new ColumnFormatException("Alien value");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("Alien value", e);
        }

    }

}
