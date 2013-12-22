package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.CommandUtils;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.SignatureController;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.StoreableColumnType;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalDataTable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalTableProvider;

import javax.xml.stream.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StoreableTableProvider implements TableProvider, UniversalTableProvider, AutoCloseable {
    public static final String TABLE_NAME = "[a-zA-Zа-яА-Я0-9]+";
    private File workingDirectory;
    public StoreableTable curDataBaseStorage = null;
    private boolean closed = false;
    private Map<String, StoreableTable> dataBaseTables = new HashMap<String, StoreableTable>();
    private SignatureController signatureController = new SignatureController();
    private final ReadWriteLock tableWorkController = new ReentrantReadWriteLock();

    public StoreableTableProvider(File dir) throws IOException {
        workingDirectory = dir;
        File[] tables = workingDirectory.listFiles();
        if (tables.length != 0) {
            for (File f : tables) {
                if (f.isDirectory()) {
                    List<Class<?>> columnTypes = null;
                    tableWorkController.readLock().lock();
                    try {
                        columnTypes = signatureController.getSignature(f.getCanonicalFile());
                    } finally {
                        tableWorkController.readLock().unlock();
                    }
                    if (columnTypes == null) {
                        throw new IOException("signature.tsv is not found");
                    }
                    StoreableTable dataTable = new StoreableTable(f.getName(), workingDirectory, columnTypes, this);
                    dataBaseTables.put(f.getName(), dataTable);
                }
            }
        }
    }

    public StoreableTable setCurTable(String newTable) throws IOException {
        try {
            StoreableTable dataTable = null;
            if (!dataBaseTables.isEmpty()) {
                dataTable = dataBaseTables.get(newTable);
                if (dataTable != null) {
                    tableWorkController.readLock().lock();
                    try {
                        dataTable.load();
                    } finally {
                        tableWorkController.readLock().unlock();
                    }
                    if (curDataBaseStorage != null) {
                        tableWorkController.writeLock().lock();
                        try {
                            curDataBaseStorage.writeToDataBase();
                        } finally {
                            tableWorkController.writeLock().unlock();
                        }
                    }
                }
            }
            curDataBaseStorage = dataTable;
            return dataTable;
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }

    public StoreableTable getTable(String name) throws IllegalArgumentException {
        checkNotClosed();
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException("The table has not allowed name");
        }
        if (!name.matches(TABLE_NAME)) {
            throw new IllegalArgumentException("Not correct file name");
        }
        tableWorkController.readLock().lock();
        try {
            checkNotClosed();
            StoreableTable dataTable = dataBaseTables.get(name);
            if (dataTable != null) {
                if (dataTable.isTableClosed()) {
                    try {
                        StoreableTable newDataTable = new StoreableTable(dataTable);
                        return newDataTable;
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e.getMessage());
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
            }
            return dataBaseTables.get(name);
        } finally {
            tableWorkController.readLock().unlock();
        }
    }

    public StoreableTable createTable(String name, List<Class<?>> columnTypes) throws IOException,
            IllegalArgumentException {
        checkNotClosed();
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException("create: the table has not allowed name");
        }
        if (!name.matches(TABLE_NAME)) {
            throw new IllegalArgumentException("create: not correct file name");
        }
        if (columnTypes == null) {
            throw new IllegalArgumentException("create: the null column type is not allowed");
        }
        if (columnTypes.isEmpty()) {
            throw new IllegalArgumentException("create: not correct types");
        }
        signatureController.checkSignatureValidity(columnTypes);
        tableWorkController.writeLock().lock();
        try {
            checkNotClosed();
            if (dataBaseTables.get(name) != null) {
                return null;
            } else {
                File newTableFile = new File(workingDirectory, name);
                try {
                    newTableFile = newTableFile.getCanonicalFile();
                } catch (IOException e) {
                    throw new IllegalArgumentException("create: programme's mistake in getting canonical file");
                }
                newTableFile.mkdir();
                StoreableTable newTable = new StoreableTable(name, workingDirectory, columnTypes, this);
                dataBaseTables.put(name, newTable);
                File sign = new File(newTableFile, "signature.tsv");
                sign.createNewFile();
                signatureController.writeSignatureToFile(sign, columnTypes);
                return newTable;

            }
        } finally {
            tableWorkController.writeLock().unlock();
        }
    }

    public void removeTable(String name) throws IOException, IllegalArgumentException {
        checkNotClosed();
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException("The table has not allowed name");
        }
        if (!name.matches(TABLE_NAME)) {
            throw new RuntimeException("Not correct file name");
        }
        if (dataBaseTables.get(name) != null) {
            File table = new File(workingDirectory, name);
            try {
                checkNotClosed();
                table = table.getCanonicalFile();
            } catch (IOException e) {
                throw new IllegalArgumentException("Programme's mistake in getting canonical file");
            }
            tableWorkController.writeLock().lock();
            try {
                checkNotClosed();
                CommandUtils.recDeletion(table);
                dataBaseTables.remove(name);
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            } finally {
                tableWorkController.writeLock().unlock();
            }
        } else {
            throw new IllegalStateException(name + " not exists");
        }
    }

    public Storeable deserialize(Table table, String value) throws ParseException {
        try {
            checkNotClosed();
            XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(value));
            Storeable ret = createFor(table);
            int columnCounter = 0;
            int node = 0;
            if (xmlReader.hasNext()) {
                int startNode = xmlReader.next();
                if (!(startNode == XMLStreamConstants.START_ELEMENT)
                        || !(xmlReader.getName().getLocalPart().equals("row"))) {
                    throw new ParseException("there is a mistake in getting first tag. <row> is expected", 0);
                } else {
                    while (xmlReader.hasNext()) {
                        if (xmlReader.hasNext()) {
                            node = xmlReader.next();
                            if (node == XMLStreamConstants.END_ELEMENT) {
                                break;
                            } else {
                                if ((node == XMLStreamConstants.START_ELEMENT)
                                        && (xmlReader.getName().getLocalPart().equals("col"))) {
                                    if (xmlReader.hasNext()) {
                                        node = xmlReader.next();
                                        if (node == XMLStreamConstants.CHARACTERS) {
                                            String parseValue = xmlReader.getText();
                                            ret.setColumnAt(columnCounter, StoreableColumnType.
                                                    convertStringToAnotherObject(parseValue,
                                                            table.getColumnType(columnCounter)));
                                            ++columnCounter;
                                        }

                                    } else {
                                        throw new ParseException("Not managed to convert xml value. "
                                                + "The text is expected", 0);
                                    }

                                    if (xmlReader.hasNext()) {
                                        node = xmlReader.next();
                                        if (node != XMLStreamConstants.END_ELEMENT) {
                                            throw new ParseException("Not managed to convert xml value."
                                                    + " End tag is expected", 0);
                                        }
                                    } else {
                                        throw new ParseException("Not managed to convert xml value."
                                                + " End tag is expected", 0);
                                    }

                                } else {
                                    if ((node != XMLStreamConstants.START_ELEMENT)
                                            || (!(xmlReader.getName().getLocalPart().equals("null")))) {
                                        throw new ParseException("Not managed to convert xml value. "
                                                + "Start tag is expected", 0);
                                    }
                                    ++columnCounter;
                                    if (xmlReader.hasNext()) {
                                        node = xmlReader.next();
                                        if (node != XMLStreamConstants.END_ELEMENT) {
                                            throw new ParseException("Not managed to convert xml value. "
                                                    + "End tag is expected", 0);
                                        }
                                    }
                                }
                            }
                        } else {
                            throw new ParseException("Not managed to convert xml value. Start tag is expected", 0);
                        }
                    }
                }
            }
            if (node != XMLStreamConstants.END_ELEMENT) {
                throw new ParseException("XML document structures must start and end within the same entity", 0);
            }
            xmlReader.close();
            return ret;
        } catch (XMLStreamException e) {
            throw new ParseException("Programme's mistake in xmlStreamReader", 0);
        }
    }

    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        try {
            checkNotClosed();
            if (value == null) {
                throw new IllegalArgumentException("Null value is not allowed");
            }
            if ((table == null)) {
                throw new IllegalArgumentException("Null table is not allowed");
            }
            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(stringWriter);
            xmlWriter.writeStartElement("row");
            int columnCount = table.getColumnsCount();
            for (int i = 0; i < columnCount; ++i) {

                String s = signatureController.convertStoreableFieldToString(value, i, table.getColumnType(i));
                if (s == null) {
                    xmlWriter.writeEmptyElement("null");
                } else {
                    xmlWriter.writeStartElement("col");
                    xmlWriter.writeCharacters(s);
                    xmlWriter.writeEndElement();
                }

            }
            xmlWriter.writeEndElement();
            xmlWriter.flush();
            xmlWriter.close();
            return stringWriter.toString();
        } catch (XMLStreamException e1) {
            throw new ColumnFormatException("Programme's mistake in getting the xmlStreamWriter");
        }

    }

    public Storeable createFor(Table table) {
        checkNotClosed();
        return new StoreableDataValue(SignatureController.getColumnTypes(table));
    }

    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        checkNotClosed();
        if (values == null) {
            throw new IllegalArgumentException("It's impossible to create storeable from null list");
        }
        Storeable storeable = createFor(table);
        int columnsCount = table.getColumnsCount();
        if (columnsCount != values.size()) {
            throw new IndexOutOfBoundsException("Not valid count of values");
        }
        for (int i = 0; i < columnsCount; ++i) {
            storeable.setColumnAt(i, values.get(i));
        }
        return storeable;
    }

    public UniversalDataTable getCurTable() {

        return curDataBaseStorage;
    }

    public String toString() {
        checkNotClosed();
        return new String(this.getClass().getSimpleName() + "[" + workingDirectory.toString() + "]");
    }

    public void close() throws IOException {
        if (!closed) {
            Set<String> tableNames = dataBaseTables.keySet();
            for (String name : tableNames) {
                if (!dataBaseTables.get(name).isTableClosed()) {
                    dataBaseTables.get(name).close();
                }
            }
            closed = true;
        }
    }

    public void checkNotClosed() {
        if (closed) {
            throw new IllegalStateException("TableProvider is closed");
        }
    }

    public boolean isTableProviderClosed() {
        return closed;
    }
}


