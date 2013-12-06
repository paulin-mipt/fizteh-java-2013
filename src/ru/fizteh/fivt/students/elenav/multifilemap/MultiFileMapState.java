package ru.fizteh.fivt.students.elenav.multifilemap;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.utils.Reader;
import ru.fizteh.fivt.students.elenav.utils.Writer;

public class MultiFileMapState extends FilesystemState implements Table {
    
    private static final int DIR_COUNT = 16;
    private static final int FILES_PER_DIR = 16;
    private final HashMap<String, String> startMap = new HashMap<>();
    public final HashMap<String, String> map = new HashMap<>();
    private int numberOfChanges = 0;
    
    public MultiFileMapState(String n, File wd, PrintStream s) {
        super(n, wd, s);
    }
    
    @Override
    public String get(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("can't get null key");
        }
        return map.get(key);
    } 
    
    @Override
    public String getValue(String key) {
        return get(key);
    } 

    @Override
    public String put(String key, String value) {
        if (key == null || value == null || key.trim().isEmpty() || value.trim().isEmpty()) {
            throw new IllegalArgumentException("can't put null key or(and) value");
        }
        String currentValue = map.put(key, value);
        String oldValue = startMap.get(key);
        if (currentValue == null) {
            if (oldValue == null) {
                setNumberOfChanges(getNumberOfChanges() + 1);
            } else {
                if (oldValue.equals(value)) {
                    setNumberOfChanges(getNumberOfChanges() - 1);
                }
            }
        } else {
            if (!value.equals(currentValue)) {
                if (oldValue != null && oldValue.equals(currentValue)) {
                    setNumberOfChanges(getNumberOfChanges() + 1);
                }
                if (oldValue != null && oldValue.equals(value)) {
                    setNumberOfChanges(getNumberOfChanges() - 1);
                } 
            }
        }
        return currentValue;
    }
    
    @Override
    public String remove(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("can't remove null key");
        }
        String oldValue = startMap.get(key);
        String value = map.remove(key);
        if (value != null) {
            if (oldValue == null) {
                setNumberOfChanges(getNumberOfChanges() - 1);
            } else {
                if (oldValue.equals(value)) {
                    setNumberOfChanges(getNumberOfChanges() + 1);
                }
            }
        }
        return value;
    }
    
    @Override
    public String removeKey(String key) {
        return remove(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public int commit() {
        int result = numberOfChanges;
        startMap.clear();
        startMap.putAll(map);
        numberOfChanges = 0;
        try {
            write();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public int rollback() {
        int result = numberOfChanges;
        map.clear();
        map.putAll(startMap);
        numberOfChanges = 0;
        return result;
    }

    public int getNumberOfChanges() {
        return numberOfChanges;
    }

    public void setNumberOfChanges(int numberOfChanges) {
        this.numberOfChanges = numberOfChanges;
    }
    
    private int getDir(String key) throws IOException {
        int hashcode = Math.abs(key.hashCode());
        int ndirectory = hashcode % 16;
        if (!getWorkingDirectory().exists()) {
            getWorkingDirectory().mkdir();
        }
        File dir = new File(getWorkingDirectory(), ndirectory + ".dir");
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new IOException("can't create dir");
            }
        }
        return ndirectory;
    }

    private int getFile(String key) throws IOException {
        int hashcode = Math.abs(key.hashCode());
        int ndirectory = hashcode % 16;
        int nfile = hashcode / 16 % 16;
        File dir = new File(getWorkingDirectory(), ndirectory + ".dir");
        File file = new File(dir.getCanonicalPath(), nfile + ".dat");
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("can't create file");
            }
        }
        return nfile;
    }
    
    public void read() throws IOException {
        map.clear();
        File[] dirs = getWorkingDirectory().listFiles();
        if (dirs != null) {
            for (File file : dirs) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        Reader.readFile(f, map);
                        f.delete();
                    }
                }
                file.delete();
            }
            startMap.clear();
            startMap.putAll(map);
        }
        
    }
    
    public void write() throws IOException {
        if (getWorkingDirectory() != null) {
            for (int i = 0; i < DIR_COUNT; ++i) {
                for (int j = 0; j < FILES_PER_DIR; ++j) {
                    Map<String, String> toWriteInCurFile = new HashMap<>();
            
                    for (String key : map.keySet()) {
                        if (getDir(key) == i && getFile(key) == j) {
                            toWriteInCurFile.put(key, map.get(key));
                        }
                    }
                    
                    if (toWriteInCurFile.size() > 0) {
                        File dir = new File(getWorkingDirectory(), i + ".dir"); 
                        File out = new File(dir, j + ".dat");
                        DataOutputStream s = new DataOutputStream(new FileOutputStream(out));
                        Set<Entry<String, String>> set = toWriteInCurFile.entrySet();
                        try {
                            for (Entry<String, String> element : set) {
                                Writer.writePair(element.getKey(), element.getValue(), s);
                            }
                        } finally {
                            s.close();
                        }
                    }
                }
            }
        }
    }

    @Override
    public Storeable put(String string, Storeable string2) {
        throw new UnsupportedOperationException("Command isn't supported in this implementation");
    }
    
}
