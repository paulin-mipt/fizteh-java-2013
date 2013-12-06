package ru.fizteh.fivt.students.irinapodorozhnaya.storeable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.GenericTable;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend.ExtendProvider;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend.ExtendTable;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Types;

public class MyTable extends GenericTable<Storeable> implements ExtendTable {
    
    private final List<Class<?>> columnType;
    private final ExtendProvider provider;
    private volatile boolean isClosed = false;

    public MyTable(String name, File rootDir, ExtendProvider provider) throws IOException {
        super(name, rootDir);
        columnType = readSignature();
        this.provider = provider;
    }

    public MyTable(String name, File rootDir, ExtendProvider provider, List<Class<?>> columnType) throws IOException {
        super(name, rootDir);
        this.columnType = new ArrayList<>();
        this.columnType.addAll(columnType);
        this.provider = provider;
    }

    private void checkClosed() {
        if (isClosed) {
            throw new IllegalStateException("call for closed object");
        }
    }

    @Override
    public void close() {
        if (!isClosed) {
            rollback();
            isClosed = true;
        }
    }

    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + tableDirectory.getAbsolutePath() + "]";
    }

    private List<Class<?>> readSignature() throws IOException {
        List<Class<?>> columns = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(tableDirectory, "signature.tsv"))) {
            while (sc.hasNext()) {
                columns.add(Types.getTypeByName(sc.next()));
            }
        } catch (FileNotFoundException e) {
            throw new IOException(getName() + ": signature file not found");
        }

        if (columns.isEmpty()) {
            throw new IOException("empty signature");
        }
        return columns;        
    }

    @Override
    public int commit() throws IOException {
        checkClosed();
        loadOldDatabase();
        return super.commit();
    }

    public int rollback() {
        checkClosed();
        return super.rollback();
    }

    @Override
    public Storeable get(String key) {
        checkClosed();
        return super.get(key);
    }

    @Override
    public String getName() {
        checkClosed();
        return super.getName();
    }

    @Override
    public Storeable remove(String key) {
        checkClosed();
        return super.remove(key);
    }

    @Override
    public int size() {
        checkClosed();
        return super.size();
    }

    @Override
    protected boolean checkEquals(Storeable val1, Storeable val2) {
        if (val1 == null && val2 == null) {
            return true;
        }
        return val1 != null && val2 != null && provider.serialize(this, val1).equals(provider.serialize(this, val2));

    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        checkClosed();
        if (value == null || key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("null argument in put");
        }

        int sizeColumn = columnType.size();

        try {
            for (int i = 0; i < sizeColumn; ++i) {
                Object valueI = value.getColumnAt(i);
                if (valueI != null && valueI.getClass() != columnType.get(i)) {
                    throw new ColumnFormatException(i + " column has incorrect format");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("alien Storeable");
        }

        try {
            value.getColumnAt(sizeColumn);
            throw new ColumnFormatException("alien Storeable");
        } catch (IndexOutOfBoundsException e) {
            return super.put(key, value);
        }
     }

    @Override
    public int getColumnsCount() {
        checkClosed();
        return columnType.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkClosed();
        return columnType.get(columnIndex);
    }

    @Override
    protected Map<String, String> serialize(Map<String, Storeable> values) {
        checkClosed();
        if (values == null) {
            return null;
        }
        Map<String, String> value = new HashMap<>();
        Set<Entry<String, Storeable>> t = values.entrySet();
        for (Entry<String, Storeable> k: t) {
            value.put(k.getKey(), provider.serialize(this, k.getValue()));
        }
        return value;
    }

    @Override
    protected Map<String, Storeable> deserialize(Map<String, String> values) throws IOException {
        checkClosed();
        if (values == null) {
            return null;
        }
        Map<String, Storeable> value = new HashMap<>();
        Set<Entry<String, String>> t = values.entrySet();
        for (Entry<String, String> k: t) {
            try { 
                value.put(k.getKey(), provider.deserialize(this, k.getValue()));
            } catch (ParseException e) {
                throw new IOException(e);
            }            
        }
        return value;
    }
}
