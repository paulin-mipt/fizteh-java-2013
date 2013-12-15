package ru.fizteh.fivt.students.ermolenko.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import javax.xml.stream.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StoreableTableProvider implements TableProvider {

    private Map<String, StoreableTable> mapOfTables;
    private File currentDir;
    private Lock tableProviderLock = new ReentrantLock(true);

    public StoreableTableProvider(File inDir) throws IOException {

            if (inDir == null) {
                throw new IllegalArgumentException("null directory");
            }
            if (!inDir.isDirectory()) {
                throw new IllegalArgumentException("not a directory");
            }
            mapOfTables = new HashMap<String, StoreableTable>();
            currentDir = inDir;
            File[] fileMas = currentDir.listFiles();
            if (fileMas.length != 0) {
                for (File fileMa : fileMas) {
                    if (fileMa.isDirectory()) {
                        mapOfTables.put(fileMa.getName(), new StoreableTable(fileMa, this));
                    }
                }
            }
    }

    @Override
    public StoreableTable getTable(String name) {

        try {
            tableProviderLock.lock();
            if (name == null || !name.matches("[0-9a-zA-Zа-яА-Я]+")) {
                throw new IllegalArgumentException("incorrect name of table");
            }
            if (name.trim().equals("")) {
                throw new IllegalArgumentException("empty table");
            }
            return mapOfTables.get(name);
        } finally {
            tableProviderLock.unlock();
        }
    }

    @Override
    public StoreableTable createTable(String name, List<Class<?>> columnTypes) throws IOException {


            if (name == null) {
                throw new IllegalArgumentException("null name to create");
            }
            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException("empty name to get");
            }
            if (!name.matches("[a-zA-Zа-яА-Я0-9]+")) {
                throw new IllegalArgumentException("incorrect name to create");
            }
            if (columnTypes == null) {
                throw new IllegalArgumentException("null columnTypes to create");
            }
            if (columnTypes.isEmpty()) {
                throw new IllegalArgumentException("empty columnTypes to create");
            }

            tableProviderLock.lock();
            try {
                File tableFile = new File(currentDir, name);

                if (!tableFile.mkdir()) {
                    return null;
                }

                try {
                    StoreableUtils.writeSignature(tableFile, columnTypes);
                } catch (IOException e) {
                    throw new IllegalArgumentException("wrong column type table");
                }

                StoreableTable table = new StoreableTable(tableFile, this);
                StoreableTable tmp = mapOfTables.get(name);
                if (tmp != null) {
                    return null;
                }
                mapOfTables.put(name, table);

                return table;
        } finally {
            tableProviderLock.unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {


            if (name == null) {
                throw new IllegalArgumentException("null name to create");
            }
            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException("empty name to get");
            }
            if (!name.matches("[a-zA-Zа-яА-Я0-9]+")) {
                throw new IllegalArgumentException("incorrect name to create");
            }

            if (mapOfTables.get(name) == null) {
                throw new IllegalStateException("not existing table");
            }

            tableProviderLock.lock();
            try {
                mapOfTables.remove(name);
        } finally {
            tableProviderLock.unlock();
        }
    }

    public static Object getObject(String string, String expectedClassName) throws ParseException {

        try {
            return StoreableEnum.parseValueWithClass(string, StoreableEnum.getClassByName(expectedClassName));
        } catch (NumberFormatException e) {
            throw new ParseException("", 0);
        }
    }

    @Override
    public MyStoreable deserialize(Table table, String value) throws ParseException {

        if (value == null) {
            return null;
        }

        XMLStreamReader reader = null;
        try {
            reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(value));
        } catch (XMLStreamException e) {
            throw new ParseException("xml reading error", 0);
        }
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        int columnCount = table.getColumnsCount();
        for (int i = 0; i < columnCount; ++i) {
            columnTypes.add(table.getColumnType(i));
        }
        MyStoreable storeable = new MyStoreable(columnTypes);
        int i = 0;

        try {
            reader.next();
        } catch (XMLStreamException e) {
            throw new ParseException("xml reading error", 0);
        }
        if (!reader.isStartElement() || !reader.getName().getLocalPart().equals("row")) {
            throw new ParseException("", 0);
        }

        int size = table.getColumnsCount();
        while (i < size) {
            try {
                reader.next();
            } catch (XMLStreamException e) {
                throw new ParseException("xml reading error", 0);
            }
            if (reader.isStartElement() && reader.getName().getLocalPart().equals("col")) {
                try {
                    reader.next();
                } catch (XMLStreamException e) {
                    throw new ParseException("xml reading error", 0);
                }
                if (reader.isCharacters()) {
                    String object = reader.getText();
                    storeable.setColumnAt(i, getObject(object, table.getColumnType(i++).getSimpleName()));
                } else {
                    throw new ParseException("", 0);
                }
            } else if (reader.isStartElement() && reader.getName().getLocalPart().equals("null")) {
                storeable.setColumnAt(i++, null);
            } else {
                throw new ParseException("", 0);
            }

            try {
                reader.next();
            } catch (XMLStreamException e) {
                throw new ParseException("xml reading error", 0);
            }
            if (!reader.isEndElement()) {
                throw new ParseException("", 0);
            }

        }
        try {
            reader.next();
        } catch (XMLStreamException e) {
            throw new ParseException("xml reading error", 0);
        }
        if (!reader.isEndElement()) {
            throw new ParseException("", 0);
        }

        return storeable;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {

        if (value == null) {
            return null;
        }
        StringWriter result = new StringWriter();
        try {

            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(result);
            try {
                writer.writeStartElement("row");
                for (int i = 0; i < table.getColumnsCount(); ++i) {

                    Object element = value.getColumnAt(i);
                    if (element == null) {
                        writer.writeEmptyElement("null");
                    } else {
                        writer.writeStartElement("col");
                        if (element.getClass() != table.getColumnType(i)) {
                            throw new ColumnFormatException("col " + i + " has " + element.getClass()
                                    + " instead of " + table.getColumnType(i));
                        }
                        writer.writeCharacters(element.toString());
                        writer.writeEndElement();
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException("different row size");
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }

        return result.toString();
    }

    @Override
    public Storeable createFor(Table table) {

        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            columnTypes.add(table.getColumnType(i));
        }
        return new MyStoreable(columnTypes);
    }

    @Override
    public MyStoreable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {

        if (values.size() > table.getColumnsCount()) {
            throw new IndexOutOfBoundsException("too many values");
        }
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            columnTypes.add(table.getColumnType(i));
        }
        MyStoreable storeable = new MyStoreable(columnTypes);
        int columnIndex = 0;
        for (Object value : values) {
            storeable.setColumnAt(columnIndex, value);
            columnIndex++;
        }
        return storeable;
    }
}
