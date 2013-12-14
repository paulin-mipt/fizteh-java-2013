package ru.fizteh.fivt.students.vlmazlov.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.vlmazlov.utils.*;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class StoreableTableProvider implements TableProvider, AutoCloseable {

    private boolean isClosed;
    private Map<String, StoreableTable> tables;
    private final boolean autoCommit;
    private final String root;

    public StoreableTableProvider(String root, boolean autoCommit) throws ValidityCheckFailedException {
        if (root == null) {
            throw new IllegalArgumentException("Directory not specified");
        }

        ValidityChecker.checkMultiTableDataBaseRoot(root);

        this.root = root;
        tables = new HashMap<String, StoreableTable>();
        this.autoCommit = autoCommit;
        isClosed = false;
    }

    public synchronized StoreableTable createTable(String name, List<Class<?>> columnTypes)
    throws IOException {
        
        checkClosed();

        if ((columnTypes == null) || (columnTypes.isEmpty())) {
            throw new IllegalArgumentException("wrong type (column types not specified)");
        }

        if (getTable(name) != null) {
            return null;
        }

        try {
            ValidityChecker.checkMultiTableName(name);
            
            for (Class<?> type : columnTypes) {
                ValidityChecker.checkColumnType(type);
            }
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

        StoreableTable newTable = null;

        if (tables.get(name) != null) {
            return null;
        }

        (new File(root, name)).mkdir();

        newTable = instantiateTable(name, columnTypes);

        tables.put(name, newTable);

        StoreableTableFileManager.writeSignature(newTable, this);

        return newTable;
    }

    public synchronized void removeTable(String name) {
        checkClosed();

        try {
            ValidityChecker.checkMultiTableName(name);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

        StoreableTable oldTable = tables.remove(name);

        if (oldTable == null) {
            throw new IllegalStateException("Table " + name + " doesn't exist");
        }

        FileUtils.recursiveDelete(new File(root, name));
    }

    public String getRoot() {
        checkClosed();
        return root;
    }

    protected StoreableTable instantiateTable(String name, List<Class<?>> columnTypes) {
        checkClosed();

        try {
            return new StoreableTable(this, name, autoCommit, columnTypes);
        } catch (ValidityCheckFailedException ex) {
            throw new RuntimeException("Validity check failed: " + ex.getMessage());
        } catch (IOException ex) {
            throw new RuntimeException("Validity check failed: " + ex.getMessage());
        }
    }

    public StoreableTable getTable(String name) {
        checkClosed();

        try {
            ValidityChecker.checkMultiTableName(name);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }


        StoreableTable table = tables.get(name);

        if (table == null) {
            try {
                table = loadTable(name);
            } catch (IOException ex) {
                throw new RuntimeException("Unable to load table " + name + ": " + ex.getMessage());
            } catch (ValidityCheckFailedException ex) {
                throw new RuntimeException("Unable to load table " + name + ": " + ex.getMessage());
            }
        }

        return table;
    }

    private StoreableTable loadTable(String name) throws IOException, ValidityCheckFailedException {
        if (!Arrays.asList(new File(getRoot()).list()).contains(name)) {
            return null;
        }

        File tableDir = new File(getRoot(), name);  
        ValidityChecker.checkMultiStoreableTableRoot(tableDir);

        StoreableTable table = new StoreableTable(this, name, autoCommit, 
            StoreableTableFileManager.getTableSignature(name, this));
        
        tables.put(name, table);

        return table;
    }

    public Storeable deserialize(StoreableTable table, String value) throws ParseException {
        checkClosed();
        return this.deserialize((Table) table, value);
    }

    public String serialize(StoreableTable table, Storeable value) throws ColumnFormatException {
        checkClosed();
        return this.serialize((Table) table, value);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        checkClosed();

        List<Object> values = new ArrayList<Object>();

        try {
            XMLStoreableReader reader = new XMLStoreableReader(value);
            for (int i = 0; i < table.getColumnsCount(); ++i) {
                values.add(reader.readColumn(table.getColumnType(i)));
            }


        } catch (XMLStreamException ex) {
            throw new ParseException(ex.getMessage(), 0);
        }

        return createFor(table, values);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        checkClosed();

        try {
            ValidityChecker.checkValueFormat(table, value);
        } catch (ValidityCheckFailedException ex) {
            throw new ColumnFormatException(ex.getMessage());
        }

        try {
            return XMLStoreableWriter.serialize(value);
        } catch (XMLStreamException ex) {
            throw new RuntimeException("Unable to write XML: " + ex.getMessage());
        }
    }

    @Override
    public Storeable createFor(Table table) {
        checkClosed();

        if (table == null) {
            throw new IllegalArgumentException("table not specified");
        }

        List<Class<?>> valueTypes = new ArrayList<Class<?>>(table.getColumnsCount());

        for (int i = 0; i < table.getColumnsCount(); ++i) {
            valueTypes.add(table.getColumnType(i));
        }

        return new TableRow(valueTypes);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        checkClosed();

        if (table == null) {
            throw new IllegalArgumentException("table not specified");
        }

        if (values == null) {
            throw new IllegalArgumentException("values not specified");
        }

        if (values.size() > table.getColumnsCount()) {
            throw new IndexOutOfBoundsException("Too many columns passed");
        }

        if (values.size() < table.getColumnsCount()) {
            throw new IndexOutOfBoundsException("Too few columns passed");
        }

        Storeable result = createFor(table);

        for (int i = 0; i < values.size(); ++i) {
            result.setColumnAt(i, values.get(i));
        }

        return result;
    }

    public void read() throws IOException, ValidityCheckFailedException {
        checkClosed();

        ValidityChecker.checkMultiTableDataBaseRoot(getRoot());

        File rootFile = new File(getRoot());
        for (File entry : rootFile.listFiles()) {

            loadTable(entry.getName());

        }
    }

    public void write() throws IOException, ValidityCheckFailedException {
        checkClosed();
        
        File rootDir = new File(getRoot());

        for (File entry : rootDir.listFiles()) {

            StoreableTable curTable = getTable(entry.getName());

            if (curTable == null) {
                throw new IOException(entry.getName() + " doesn't match any database");
            }

            curTable.checkRoot(entry);

            curTable.commit();
        }
    }

    public void closeTable(String name) {
        checkClosed();
        tables.remove(name);
    }

    public void close() {
        //necessary for factory.close() to work
        if (isClosed) {
            return;
        }

        for (Map.Entry<String, StoreableTable> entry : tables.entrySet()) {
            entry.getValue().close();
        }

        isClosed = true;

    }

    public void checkClosed() {
        if (isClosed) {
            throw new IllegalStateException("trying to operate on a closed table provider");
        }
    }

    public String toString() {
        checkClosed();
        StringBuilder builder = new StringBuilder();

        builder.append(getClass().getSimpleName());
        builder.append("[");
        builder.append(getRoot());
        builder.append("]");

        return builder.toString();
    }
}
