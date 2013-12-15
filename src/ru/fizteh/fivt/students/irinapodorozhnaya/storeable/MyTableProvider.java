package ru.fizteh.fivt.students.irinapodorozhnaya.storeable;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.CommandRemove;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend.ExtendProvider;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend.ExtendTable;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Types;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.XMLSerializer;

public class MyTableProvider implements ExtendProvider, AutoCloseable {

    private final File dataBaseDir;
    private final Map<String, ExtendTable> tables = new HashMap<>();
    private static final String STRING_NAME_FORMAT = "[a-zA-Zа-яА-Я0-9_]+";
    private volatile boolean isClosed = false;

    private void checkClosed() {
        if (isClosed) {
            throw new IllegalStateException("call for closed object");
        }
    }

    public MyTableProvider(File dataBaseDir) throws IOException {
        this.dataBaseDir = dataBaseDir;
        for (String tableName: dataBaseDir.list()) {
            tables.put(tableName, new MyTable(tableName, dataBaseDir, this));
      }
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + dataBaseDir.getAbsolutePath() + "]";
    }

    @Override
    public synchronized ExtendTable getTable(String name) {
        checkClosed();
        checkCorrectName(name);
        try {
            ExtendTable table = tables.get(name);
            if (table != null && table.isClosed()) {
                ExtendTable newTable = new MyTable(name, dataBaseDir, this);
                tables.put(name, newTable);
            }
            return tables.get(name);
        } catch (IOException e)  {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public synchronized ExtendTable createTable(String name, List<Class<?>> columnTypes)
            throws IOException {

        checkClosed();
        checkCorrectName(name);
        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("bad column list");
        }
        File table = new File(dataBaseDir, name);
        if (table.isDirectory()) {
            return null;
        }
        if (!table.mkdir()) {
            throw new IllegalArgumentException("table has illegal name");
        }

        try (PrintStream signature = new PrintStream(new File(table, "signature.tsv"))) {
            boolean isFirst = true;
            for (Class<?> s : columnTypes) {
                if (!isFirst) {
                    signature.print(" ");
                } else {
                    isFirst = false;
                }
                signature.print(Types.getSimpleName(s));
            }
        }

        try (PrintStream sizePrint = new PrintStream(new File(table, "size.tsv"))) {
            sizePrint.print(0);
        }

            ExtendTable newTable = new MyTable(name, dataBaseDir, this, columnTypes);
        tables.put(name, newTable);
        return newTable;
   }

    @Override
    public synchronized void removeTable(String name) throws IOException {

        checkClosed();
        checkCorrectName(name);
        if (tables.remove(name) == null) {
            throw new IllegalStateException(name + " not exists");
        }
        File table = new File(dataBaseDir, name);
        CommandRemove.deleteRecursivly(table);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {

        checkClosed();
        Storeable res;
        try {
            res = XMLSerializer.deserialize(table, value);
        } catch (XMLStreamException e) {
            throw new ParseException(e.getMessage(), 0);
        }
        return res;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {

        checkClosed();
        String res;
        try {            
            res = XMLSerializer.serialize(table, value);
        } catch (XMLStreamException e) {
            throw new ColumnFormatException(e);
        }
        return res;
    }

    @Override
    public Storeable createFor(Table table) {
        checkClosed();
        if (table == null) {
            throw new IllegalArgumentException("table can't be null");
        }
        return new MyStoreable(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) 
                     throws ColumnFormatException, IndexOutOfBoundsException {
        checkClosed();
        if (table == null || values == null) {
            throw new IllegalArgumentException("table and values can't be null");
        }
        int size = table.getColumnsCount();
        if (size != values.size()) {
            throw new IndexOutOfBoundsException();
        }
        
        Storeable res = createFor(table);
        for (int i = 0; i < size; ++i) {
            res.setColumnAt(i, values.get(i));
        }
        return res;
    }
    
    public static void checkCorrectName(String name) {
        if (name == null || !name.matches(STRING_NAME_FORMAT)) {
            throw new IllegalArgumentException("table name is null or has illegal name");
        }
    }

    @Override
    public void close() {
        if (!isClosed) {
            for (ExtendTable table: tables.values()) {
                table.close();
            }
            isClosed = true;
        }
    }
}
