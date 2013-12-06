package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class TableProviderImplementation implements TableProvider, AutoCloseable {
    
    Path databaseDirectory;
    private Map<String, TableImplementation> tables = new HashMap<String, TableImplementation>();
    
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();
    
    private volatile boolean isClosed = false;
    
    public TableProviderImplementation(Path databaseDirectory) throws IOException {
        this.databaseDirectory = databaseDirectory;
        
        List<Class<?>> columnTypes;
        for (String tableName : databaseDirectory.toFile().list()) {
            columnTypes = getSignature(databaseDirectory.resolve(tableName).resolve("signature.tsv"));
            tables.put(tableName, new TableImplementation(this, databaseDirectory, tableName, columnTypes));
        }
    }
    
    @Override
    public Table getTable(String name) {
        isClosed();
        
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("Invalid table name");
        }
        
        readLock.lock();
        try {
            return tables.get(name);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        isClosed();
        
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("Invalid table name");
        }
        if (!areValidColumnTypes(columnTypes)) {
            throw new ColumnFormatException("Invalid column types");
        }
        
        writeLock.lock();
        try {
            if (tables.containsKey(name)) {
                return null;
            }
        
            Path tablePath = databaseDirectory.resolve(name);
            try {
                Files.createDirectory(tablePath);
            } catch (IOException e) {
                throw new IOException("Error while creating a directory: " + tablePath + " "
                        + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
            }
            
            Path signatureFilePath = tablePath.resolve("signature.tsv");
            try {
                Files.createFile(signatureFilePath);
            } catch (IOException e) {
                throw new IOException("Error while creating a signature file: " + signatureFilePath + " "
                        + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
            }
            
            try (FileWriter signatureFile = new FileWriter(signatureFilePath.toString())) {
                for (int i = 0; i < columnTypes.size(); ++i) {
                    if (i != 0) {
                        signatureFile.write(" ");
                    }
                    signatureFile.write(toName(columnTypes.get(i)));
                }
            } catch (IOException e) {
                throw new IOException("Error while writing to the signature file: "
                        + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
            }
            
            tables.put(name, new TableImplementation(this, databaseDirectory, name, columnTypes));
            
            return tables.get(name);
        } finally {
            writeLock.unlock();
        }
    }
    
    private static String toName(Class<?> className) {
        Map<Class<?>, String> types = new HashMap<Class<?>, String>();
        types.put(Integer.class, "int");
        types.put(Long.class, "long");
        types.put(Byte.class, "byte");
        types.put(Float.class, "float");
        types.put(Double.class, "double");
        types.put(Boolean.class, "boolean");
        types.put(String.class, "String");
        
        return types.get(className);
    }

    @Override
    public void removeTable(String name) throws IOException {
        isClosed();
        
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("Invalid table name");
        }
        
        writeLock.lock();
        try {
            if (!tables.containsKey(name)) {
                throw new IllegalStateException("No such table");
            }
        
            tables.remove(name);
            
            Path tablePath = databaseDirectory.resolve(name);
            try {
                FileUtils.recursiveDelete(tablePath);
            } catch (IOException e) {
                throw new IOException("Error while deleting a directory: "
                        + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
            }
        } finally {
            writeLock.unlock();
        }
    }
    

    public Storeable deserialize(Table table, String value) throws ParseException {
        isClosed();
        
        if (value == null) {
            return null;
        }
        List<Object> values = new ArrayList<Object>();
        
        XMLStreamReader xmlReader;
        try {
            xmlReader = XMLInputFactory.newInstance()
                    .createXMLStreamReader(new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8)));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            throw new RuntimeException("Error while deserializing: " + e.getMessage(), e);
        }
        
        try {
            if (!xmlReader.hasNext()) {
                throw new ParseException("Empty value", 0);
            }
            
            int columnIndex = 0;
            int nodeType = xmlReader.next();
            if (nodeType == XMLStreamConstants.START_ELEMENT && xmlReader.getLocalName().equals("row")) {
                while (xmlReader.hasNext()) {
                    nodeType = xmlReader.next();
                    if (nodeType == XMLStreamConstants.START_ELEMENT && xmlReader.getLocalName().equals("col")) {
                        String text = xmlReader.getElementText();
                        values.add(parseToColumn(table, columnIndex, text));
                        columnIndex += 1;
                    } else if (nodeType == XMLStreamConstants.START_ELEMENT 
                            && xmlReader.getLocalName().equals("null")) {
                        values.add(null);
                        columnIndex += 1;
                    } else if (nodeType == XMLStreamConstants.END_ELEMENT && xmlReader.getLocalName().equals("row")) {
                        break;
                    } else if (nodeType == XMLStreamConstants.COMMENT) {
                        continue;
                    }
                }
            }
            
            if (xmlReader.next() != XMLStreamConstants.END_DOCUMENT) {
                throw new ParseException("Error while parsing " + value, xmlReader.getLocation().getCharacterOffset());
            }
        } catch (XMLStreamException e) {
            throw new ParseException("Error while parsing " + value, xmlReader.getLocation().getCharacterOffset());
        }

        return new StoreableImplementation(table, values);
    }
    

    private Object parseToColumn(Table table, int columnIndex, String text) {
        if (table.getColumnType(columnIndex) == Integer.class) {
            return Integer.parseInt(text);
        } else if (table.getColumnType(columnIndex) == Long.class) {
            return Long.parseLong(text);
        } else if (table.getColumnType(columnIndex) == Byte.class) {
            return Byte.parseByte(text);
        } else if (table.getColumnType(columnIndex) == Float.class) {
            return Float.parseFloat(text);
        } else if (table.getColumnType(columnIndex) == Double.class) {
            return Double.parseDouble(text);
        } else if (table.getColumnType(columnIndex) == Boolean.class) {
            return Boolean.parseBoolean(text);
        } else { // String
            return text;
        }
    }
        
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        isClosed();
        
        if (value == null) {
            return null;
        }
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document;
        try {
            document = factory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Error while serializing value: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
        
        Element row = document.createElement("row");
        Element col;
        Element nullElement;
        Object currentValue;
        for (int columnIndex = 0; columnIndex < table.getColumnsCount(); ++columnIndex) {
            currentValue = value.getColumnAt(columnIndex);
            if (currentValue != null) {
                col = document.createElement("col");
                col.setTextContent(currentValue.toString());
                row.appendChild(col);
            } else {
                nullElement = document.createElement("null");
                row.appendChild(nullElement);
            }
        }
        document.appendChild(row);
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            String result = writer.getBuffer().toString();
            return result;
        } catch (TransformerException e) {
            throw new RuntimeException("Error while serializing value: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }        
    }

    public Storeable createFor(Table table) {
        isClosed();
        
        return new StoreableImplementation(table);
    }

    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        isClosed();
        
        return new StoreableImplementation(table, values);
    }
    
    private boolean isValidTableName(final String tableName) {
        if (tableName == null || tableName.isEmpty() || tableName.contains("\\") || tableName.contains("/") 
                || tableName.contains(".") || tableName.contains("*") || tableName.contains("?") 
                || tableName.contains(":") || tableName.contains("\"") || tableName.contains("\0")) {
            return false;
        }
        return true;
    }
    
    private boolean isValidColumnType(final String columnType) {
        List<String> validTypes = Arrays.asList("int", "long", "byte", "float", "double", "boolean", 
                "String", "Integer", "Long", "Byte", "Float", "Double", "Boolean"); // simpleName
        if (validTypes.contains(columnType)) {
            return true;
        }
        return false;
    }
    
    private boolean areValidColumnTypes(final List<Class<?>> columnTypes) {
        if (columnTypes == null) {
            return false;
        }
        
        if (columnTypes.isEmpty()) {
            return false;
        }
        
        for (Class<?> columnType : columnTypes) {
            if (columnType == null) {
                return false;
            }
            if (!isValidColumnType(columnType.getSimpleName())) {
                return false;
            }
        }
        
        return true;
    }
    
    private List<Class<?>> getSignature(Path signatureFilePath) throws IOException {
        Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
        classes.put("int", Integer.class);
        classes.put("long", Long.class);
        classes.put("byte", Byte.class);
        classes.put("float", Float.class);
        classes.put("double", Double.class);
        classes.put("boolean", Boolean.class);
        classes.put("String", String.class);
        
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        
        try (Scanner signatureFile = new Scanner(signatureFilePath)) {
            String currentType;
            while (signatureFile.hasNext()) {
                currentType = signatureFile.next();
                if (!isValidColumnType(currentType)) {
                    throw new IllegalArgumentException("Invalid column type in signature.tsv: " + currentType);
                }
                columnTypes.add(classes.get(currentType));
            }
        } catch (IOException e) {
            throw new IOException("Error while reading from the signature file: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
        
        if (columnTypes.isEmpty()) {
            throw new IOException("Empty or invalid signature.tsv");
        }
        
        return columnTypes;
    }
    
    @Override
    public String toString() {
        isClosed();
        
        String result = "";
        result += this.getClass().getSimpleName();
        result += "[" + databaseDirectory.normalize() + "]";
        return result;
    }

    @Override
    public void close() throws Exception {
        if (!isClosed) {
            writeLock.lock();
            try {
                if (!isClosed) {
                    for (String tableName : tables.keySet()) {
                        tables.get(tableName).close();
                    }
                    isClosed = true;
                }
            } finally {
                writeLock.unlock();
            }
        }
    }
    
    void reinitialize(String tableName) throws IOException {
        writeLock.lock();
        try {
            Table prevTable = tables.remove(tableName);
            
            List<Class<?>> columnTypes = new ArrayList<Class<?>>();
            for (int columnIndex = 0; columnIndex < prevTable.getColumnsCount(); ++columnIndex) {
                columnTypes.add(prevTable.getColumnType(columnIndex));
            }
            
            tables.put(tableName, new TableImplementation(this, databaseDirectory, tableName, columnTypes));
        } finally {
            writeLock.unlock();
        }
    }
    
    private void isClosed() {
        if (isClosed) {
            throw new IllegalStateException("TableProvider object is closed");
        }
    }
}
