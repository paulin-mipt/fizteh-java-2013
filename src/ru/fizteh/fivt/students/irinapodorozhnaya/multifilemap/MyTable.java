package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.extend.ExtendTable;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.FileFormatException;

public class MyTable extends GenericTable<String> implements ExtendTable {

    public MyTable(String name, File rootDir) {
        super(name, rootDir);
    }

    @Override
    public String put(String key, String value) {
        if (value == null || value.trim().isEmpty()) {
             throw new IllegalArgumentException("null argument");
        }
        return super.put(key, value);
    }
    
    @Override 
    public int commit() {
        try {
            return super.commit();
        } catch (IOException e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public void loadAll() {
        try {
            super.loadAll();
        } catch (IOException e) {
            throw new FileFormatException(e);
        }    
    }
    
    @Override
    protected Map<String, String> serialize(Map<String, String> fol) {
        return fol;
    }

    @Override
    protected Map<String, String> deserialize(Map<String, String> lof) {
        return lof;
    }
}
