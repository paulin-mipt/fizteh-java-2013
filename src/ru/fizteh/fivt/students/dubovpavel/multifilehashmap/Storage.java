package ru.fizteh.fivt.students.dubovpavel.multifilehashmap;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;
import ru.fizteh.fivt.students.dubovpavel.shell2.performers.PerformerRemove;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Storage<DB extends FileRepresentativeDataBase> {
    private File dir;
    protected HashMap<String, DB> storage;
    private DB cursor;
    private Dispatcher dispatcher;
    private DataBaseBuilder<DB> builder;

    public void save() throws StorageException {
        for (Map.Entry<String, DB> entry : storage.entrySet()) {
            try {
                entry.getValue().save();
            } catch (DataBaseHandler.DataBaseException e) {
                throw new StorageException(String.format("Saving: Database %s: %s", entry.getKey(), e.getMessage()));
            }
        }
    }

    public String getPath() {
        return dir.getAbsolutePath();
    }

    public Iterator<DB> getDBIterator() {
        return storage.values().iterator();
    }

    public DB reOpenDataBase(File folder) {
        builder.setPath(folder);
        DB dataBase = builder.construct();
        try {
            dataBase.open();
        } catch (DataBaseHandler.DataBaseException e) {
            dispatcher.callbackWriter(Dispatcher.MessageType.WARNING,
                    String.format("Storage loading: Database %s: %s", folder.getName(), e.getMessage()));
            if (!e.acceptable) {
                dispatcher.callbackWriter(Dispatcher.MessageType.WARNING,
                        "Database denied");
                System.exit(-1);
            }
        }
        storage.put(folder.getName(), dataBase);
        return dataBase;
    }

    public Storage(String path, Dispatcher dispatcher, DataBaseBuilder<DB> dataBaseBuilder) {
        builder = dataBaseBuilder;
        storage = new HashMap<>();
        this.dispatcher = dispatcher;
        dir = new File(path);
        cursor = null;
        if (!dir.isDirectory()) {
            dispatcher.callbackWriter(Dispatcher.MessageType.WARNING,
                    String.format("Storage loading: '%s' is not a directory. Empty storage applied", dir.getPath()));
            dir.mkdir();
        } else {
            for (File folder : dir.listFiles()) {
                if (folder.isDirectory()) {
                   reOpenDataBase(folder);
                }
            }
        }
    }

    public DB create(String key) {
        try {
            return createExplosive(key);
        } catch (IOException | StorageException e) {
            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR, e.getMessage());
            return null;
        }
    }

    public DB createExplosive(String key) throws IOException, StorageException {
        if (storage.containsKey(key)) {
            dispatcher.callbackWriter(Dispatcher.MessageType.WARNING,
                    String.format("%s exists", key));
            return null;
        } else {
            File newData = new File(dir, key);
            try {
                if (!newData.getCanonicalFile().getName().equals(key)) {
                    throw new StorageException("Can not create table with this name");
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage()); // See the note for PerformerShell
            }
            if (!newData.isDirectory()) {
                if (!newData.mkdir()) {
                    throw new IOException(String.format("Can not create directory '%s'", newData.getPath()));
                }
            }
            builder.setPath(newData);
            DB newDataBase = builder.construct();
            try {
                newDataBase.save();
            } catch (DataBaseHandler.DataBaseException e) {
                throw new IOException(String.format("Can not create database prototype: %s", e.getMessage()));
            }
            storage.put(key, newDataBase);
            dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS, "created");
            return newDataBase;
        }
    }

    public DB drop(String key) {
        try {
            return dropExplosive(key);
        } catch (IOException e) {
            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR, e.getMessage());
            return null;
        }
    }

    public DB dropExplosive(String key) throws IOException {
        if (storage.containsKey(key)) {
            DB value = storage.get(key);
            if (value.getPath().isDirectory()) {
                try {
                    new PerformerRemove().removeObject(storage.get(key).getPath());
                } catch (PerformerRemove.PerformerRemoveException e) {
                    throw new IOException(String.format("Drop: Can not remove: %s", e.getMessage()));
                }
            }
            if (cursor != null && cursor.getPath().getName().equals(key)) {
                cursor = null;
            }
            storage.remove(key);
            dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS, "dropped");
            return value;
        } else {
            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR, String.format("%s not exists", key));
            return null;
        }
    }

    public void setCurrent(String key) {
        if (storage.containsKey(key)) {
            cursor = storage.get(key);
            dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS,
                    String.format("using %s", key));
        } else {
            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("%s not exists", key));
        }
    }

    public DB getCurrent() {
        return cursor;
    }

    public DB getDataBase(String name) {
        if (storage.containsKey(name)) {
            return storage.get(name);
        } else {
            return null;
        }
    }
}
