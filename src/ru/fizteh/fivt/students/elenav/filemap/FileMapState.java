package ru.fizteh.fivt.students.elenav.filemap;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Set;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.utils.Writer;

public class FileMapState extends FilesystemState {

    public HashMap<String, String> map = new HashMap<>();
    
    public FileMapState(String n, File wd, PrintStream s) {
        super(n, wd, s);
    }
    
    public void writeFile(File out) throws IOException {
        DataOutputStream s = new DataOutputStream(new FileOutputStream(out));
        Set<Entry<String, String>> set = map.entrySet();
        for (Entry<String, String> element : set) {
            Writer.writePair(element.getKey(), element.getValue(), s);
        }
        s.close();
    }

    public String get(String key) {
        return map.get(key);
    }
    
    @Override
    public String getValue(String key) {
        return get(key);
    }

    @Override
    public String put(String key, String value) {
        return map.put(key, value);
    }

    public String remove(String key) {
        return map.remove(key);
    }
    
    @Override
    public String removeKey(String key) {
        return remove(key);
    }

    @Override
    public int commit() {
        throw new UnsupportedOperationException("Command isn't supported in this implementation");
    }

    @Override
    public int rollback() {
        throw new UnsupportedOperationException("Command isn't supported in this implementation");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Command isn't supported in this implementation");
    }

    @Override
    public int getNumberOfChanges() {
        throw new UnsupportedOperationException("Command isn't supported in this implementation");
    }

    @Override
    public void read() {
        throw new UnsupportedOperationException("Command isn't supported in this implementation");
    }

    @Override
    public Storeable put(String string, Storeable string2) {
        throw new UnsupportedOperationException("Command isn't supported in this implementation");
    }                                                                  
    
}
