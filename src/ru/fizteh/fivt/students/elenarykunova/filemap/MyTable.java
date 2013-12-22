package ru.fizteh.fivt.students.elenarykunova.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.elenarykunova.shell.Shell;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyTable implements Table, AutoCloseable {

    private DataBase[][] data = new DataBase[16][16];
    private String currTablePath = null;
    private String currTableName = null;
    private MyTableProvider provider = null;
    private List<Class<?>> types = new ArrayList<Class<?>>();
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private Lock read = readWriteLock.readLock();
    private Lock write = readWriteLock.writeLock();
    private volatile boolean isClosed = false;
    
    private ThreadLocal<HashMap<String, Storeable>> changesMap = new ThreadLocal<HashMap<String, Storeable>>() {
        @Override
        protected HashMap<String, Storeable> initialValue() {
            return new HashMap<String, Storeable>();
        }
    };

    protected MyTableProvider getProvider() {
        checkClosed();
        return provider;
    }

    protected String getTablePath() {
        checkClosed();
        return currTablePath;
    }

    private DataBase getDataBaseFromKey(String key) throws RuntimeException {
        checkClosed();
        int hashcode = Math.abs(key.hashCode());
        int ndir = hashcode % 16;
        int nfile = hashcode / 16 % 16;
        return data[ndir][nfile];
    }

    public String getName() {
        checkClosed();
        return currTableName;
    }

    private boolean isEmpty(String val) {
        checkClosed();
        return (val == null || val.trim().isEmpty());
    }

    private boolean isCorrectKey(String key) {
        checkClosed();
        return (!provider.hasBadSymbols(key) && !key.contains("[") && !key
                .contains("]"));
    }
   
    private void checkClosed() throws IllegalStateException {
        if (isClosed) {
            throw new IllegalStateException(currTableName + " is closed");
        }
    }
    
    private void checkValue(Storeable value) {
        checkClosed();
        for (int i = 0; i < types.size(); ++i) {
            try {
                Object val = value.getColumnAt(i);
                if (val != null && !types.get(i).equals(val.getClass())) {
                    throw new ColumnFormatException("types mismatch: expected "
                            + types.get(i) + " but was " + val.getClass());
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException("number of columns mismatch");
            }
        }
        try {
            value.getColumnAt(types.size());
            throw new ColumnFormatException("number of columns mismatch");
        } catch (IndexOutOfBoundsException e) {
            return;
        }
    }

    private boolean differs(Storeable st1, Storeable st2) {
        checkClosed();
        if (st1 == null && st2 == null) {
            return true;
        }
        if (st1 != null && st2 == null) {
            return true;
        }
        if (st1 == null && st2 != null) {
            return true;
        }
        String str1 = provider.serialize(this, st1);
        String str2 = provider.serialize(this, st2);
        return (!str1.equals(str2));
    }

    public void setNameToNull() {
        checkClosed();
        currTablePath = null;
        currTableName = null;
    }

// write to disk
    private void saveChanges() throws RuntimeException {
        checkClosed();
        if (currTableName == null) {
            return;
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                write.lock();
                try {
                    if (data[i][j].hasFile() && data[i][j].hasChanged) {
                        try {
                            data[i][j].commitChanges();
                            data[i][j].hasChanged = false;
                        } catch (IOException e) {
                            throw new RuntimeException("can't write to file", e);
                        }
                    }
                } finally {
                    write.unlock();
                }
            }
        }
        Shell sh = new Shell(currTablePath, false);
        for (int i = 0; i < 16; i++) {
            File tmpDir = new File(currTablePath + File.separator + i + ".dir");
            if (tmpDir.exists() && tmpDir.list().length == 0) {
                if (sh.rm(tmpDir.getAbsolutePath()) != Shell.ExitCode.OK) {
                    throw new RuntimeException(tmpDir.getAbsolutePath()
                            + " can't delete directory");
                }
            }
        }
    }

//read from disk
    private void loadFromDisk() throws RuntimeException {
        checkClosed();
        if (changesMap != null) {
            changesMap.get().clear();
        } else {
            changesMap.set(new HashMap<String, Storeable>());
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                read.lock();
                try {
                    try {
                        data[i][j] = new DataBase(this, i, j);
                    } catch (ParseException e) {
                        throw new RuntimeException("wrong type (load "
                                + e.getMessage() + ")", e);
                    }
                } finally {
                    read.unlock();
                }
            }
        }
    }

 // write
    private void trackChanges() {
        checkClosed();
        Set<Map.Entry<String, Storeable>> mySet = changesMap.get().entrySet();
        String key;
        Storeable value;
        DataBase mdb;
        for (Map.Entry<String, Storeable> myEntry : mySet) {
            key = myEntry.getKey();
            value = myEntry.getValue();
            mdb = getDataBaseFromKey(myEntry.getKey());
            write.lock();
            try {
                if (value == null) {
                    mdb.remove(key);
                } else {
                    mdb.put(key, value);
                }
                mdb.createFile();
                mdb.hasChanged = true;
            } finally {
                write.unlock();
            }
        }
    }
    
    public int getUncommitedChanges() {
        checkClosed();
        Set<Map.Entry<String, Storeable>> mySet = changesMap.get().entrySet();
        String key;
        Storeable value;
        DataBase mdb;
        int nchanges = 0;
        for (Map.Entry<String, Storeable> myEntry : mySet) {
            key = myEntry.getKey();
            value = myEntry.getValue();
            mdb = getDataBaseFromKey(key);
            if (differs(value, mdb.get(key))) {
                nchanges++;
            }
        }
        return nchanges;
    }

//read
    @Override
    public Storeable get(String key) throws IllegalArgumentException {
        checkClosed();
        if (isEmpty(key)) {
            throw new IllegalArgumentException("get: key is empty");
        }
        if (changesMap.get().containsKey(key)) {
            return changesMap.get().get(key);
        } else {
            read.lock();
            try {
                return getDataBaseFromKey(key).get(key);
            } finally {
                read.unlock();
            }
        } 
    }


//read
    @Override
    public Storeable put(String key, Storeable value)
            throws IllegalArgumentException, ColumnFormatException {
        checkClosed();
        if (isEmpty(key)) {
            throw new IllegalArgumentException("put: key is empty");
        }
        if (!isCorrectKey(key)) {
            throw new IllegalArgumentException("put: key has bad symbols");
        }
        if (value == null) {
            throw new IllegalArgumentException("put: value is empty");
        }
        try {
            checkValue(value);
        } catch (ColumnFormatException e1) {
            throw new ColumnFormatException("wrong type (put: "
                    + e1.getMessage() + ")", e1);
        }
        
        Storeable oldVal = null;        
        read.lock();
        try {
            oldVal = getDataBaseFromKey(key).get(key);
        } finally {
            read.unlock();
        }
        
        Storeable newVal = changesMap.get().get(key);
        Storeable res = null;
        if (changesMap.get().containsKey(key)) {
            res = newVal;
        } else if (oldVal != null) {
            res = oldVal;
        }
        
        changesMap.get().put(key, value);
        return res;
    }
    
//read
    @Override
    public Storeable remove(String key) throws IllegalArgumentException {
        checkClosed();
        if (isEmpty(key)) {
            throw new IllegalArgumentException("remove: key is empty");
        }
        
        Storeable oldVal = null;
        read.lock();
        try {
            oldVal = getDataBaseFromKey(key).get(key);
        } finally {
            read.unlock();
        }
        
        Storeable res;            
        if (changesMap.get().containsKey(key)) {
            res = changesMap.get().get(key);
        } else {
            res = oldVal;
        }
        if (oldVal == null) {
            changesMap.get().remove(key);
        } else {
            changesMap.get().put(key, null);
        }
        return res;
    }

//read
    @Override
    public int size() {
        checkClosed();
        int n = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                read.lock();
                try {
                    n += data[i][j].getSize();
                } finally {
                    read.unlock();
                }
            }
        }
        Set<Map.Entry<String, Storeable>> mySet = changesMap.get().entrySet();
        for (Map.Entry<String, Storeable> myEntry : mySet) {
            if (getDataBaseFromKey(myEntry.getKey()).get(myEntry.getKey()) == null) {
                if (myEntry.getValue() != null) {
                    n++;
                }
            } else if (myEntry.getValue() == null) {
                n--;
            }
        }
        return n;
    }


    @Override
    public int commit() throws RuntimeException {
        checkClosed();
        int nchanges = getUncommitedChanges();
        trackChanges();
        if (nchanges != 0) {
            saveChanges();
            changesMap.get().clear();
        }
        return nchanges;
    }

    @Override
    public int rollback() throws RuntimeException {
        checkClosed();
        int nchanges = getUncommitedChanges();
        changesMap.get().clear();
        return nchanges;
    }


    public MyTable() {
        isClosed = false;
    }

    public MyTable(String path, String name, MyTableProvider mtp,
            List<Class<?>> columnTypes) throws RuntimeException, IOException {
        provider = mtp;
        currTablePath = path;
        currTableName = name;
        types = new ArrayList<Class<?>>(columnTypes);
        isClosed = false;

        if (currTableName != null) {
            loadFromDisk();
        }
    }

    @Override
    public int getColumnsCount() {
        checkClosed();
        return types.size();
    }
    
    @Override
    public Class<?> getColumnType(int columnIndex)
            throws IndexOutOfBoundsException {
        checkClosed();
        if (0 > columnIndex || columnIndex >= types.size()) {
            throw new IndexOutOfBoundsException(
                    "get column type: index is out of bounds");
        }
        return types.get(columnIndex);
    }

    public static void main(String[] args) throws NoSuchMethodException, Throwable {
        MyTableProviderFactory myFactory = new MyTableProviderFactory();
        MyTableProvider provider;
        try {
            provider = (MyTableProvider) myFactory.create(System.getProperty("fizteh.db.dir"));
            MyTable mp = new MyTable();
            ExecuteCmd exec = new ExecuteCmd(mp, provider);
            exec.workWithUser(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e1) {
            System.err.println(e1.getMessage());
            System.exit(1);
        } catch (IllegalStateException e2) {
            System.err.println(e2.getMessage());
            System.exit(1);
        } catch (RuntimeException e3) {
            System.err.println(e3.getMessage());
            System.exit(1);
        } finally {
            try {
                myFactory.close();
            } catch (Exception e) {
                //what's a pitty
            }
        }
    }

    @Override
    public String toString() {
        checkClosed();
        String className = MyTable.class.getSimpleName();
        return (className + "[" + currTablePath + "]");
    }

    @Override
    public void close() throws Exception {
        if (!isClosed) {
            rollback();
            provider.removeTableFromMap(currTableName);
        }
        isClosed = true;
    }
}
