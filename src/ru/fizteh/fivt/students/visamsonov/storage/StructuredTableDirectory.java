package ru.fizteh.fivt.students.visamsonov.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.io.*;
import java.text.ParseException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import java.util.concurrent.locks.*;

public class StructuredTableDirectory implements StructuredTableProviderInterface {

	private final String dbDirectory;
	private final HashMap<String, StructuredTableInterface> tables = new HashMap<String, StructuredTableInterface>();
	private static final String VALID_TABLENAME_REGEXP = "[A-Za-zА-Яа-я0-9\\._-]+";
	private static Map<String, Class<?>> allowedTypes = new HashMap();
	private final Lock tablesLock = new ReentrantLock();
	static {
		allowedTypes.put("int", Integer.class);
		allowedTypes.put("long", Long.class);
		allowedTypes.put("float", Float.class);
		allowedTypes.put("byte", Byte.class);
		allowedTypes.put("double", Double.class);
		allowedTypes.put("boolean", Boolean.class);
		allowedTypes.put("String", String.class);
	}

	public StructuredTableDirectory (String dbDirectory) {
		this.dbDirectory = dbDirectory;
		String[] names = new File(dbDirectory).list();
		for (String name : names) {
			StructuredTableInterface table = getTable(name);
			if (table != null) {
				tables.put(name, table);
			}
		}
	}

	static public Class<?> getTypeByName (String name) {
		return allowedTypes.get(name);
	}

	static public String getNameByType (Class<?> type) {
		for (Map.Entry<String, Class<?>> entry : allowedTypes.entrySet()) {
			if (entry.getValue().isAssignableFrom(type)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public StructuredTableInterface getTable (String name) {
		if (name == null || !name.matches(VALID_TABLENAME_REGEXP)) {
			throw new IllegalArgumentException();
		}
		tablesLock.lock();
		File table = new File(dbDirectory, name);
		if (!table.isDirectory()) {
			tablesLock.unlock();
			return null;
		}
		try {
			StructuredTableInterface savedTable = tables.get(name);
			if (savedTable == null) {
				savedTable = new StructuredStorage(table.getCanonicalPath(), name, null, this);
				tables.put(name, savedTable);
			}
			return savedTable;
		}
		catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		finally {
			tablesLock.unlock();
		}
	}

	public StructuredTableInterface createTable (String name, List<Class<?>> columnTypes) throws IOException {
		if (name == null || columnTypes == null || columnTypes.size() == 0 || !name.matches(VALID_TABLENAME_REGEXP)) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < columnTypes.size(); i++) {
			if (columnTypes.get(i) == null || getNameByType(columnTypes.get(i)) == null) {
				throw new IllegalArgumentException();
			}
		}
		tablesLock.lock();
		if (tables.get(name) != null) {
			tablesLock.unlock();
			return null;
		}
		File table = new File(dbDirectory, name);
		if (table.isFile()) {
			tablesLock.unlock();
			throw new IllegalArgumentException();
		}
		try {
			table.mkdir();
			StructuredTableInterface savedTable = new StructuredStorage(table.getCanonicalPath(), name, columnTypes, this);
			tables.put(name, savedTable);
			return savedTable;
		}
		catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		finally {
			tablesLock.unlock();
		}
	}

	private boolean delete (String parent, String name) throws IOException {
		if (name == null) {
			return true;
		}
		File file = new File(parent, name);
		String[] content = file.list();
		if (content != null) {
			for (int i = 0; i < content.length; i++) {
				if (!delete(file.getCanonicalPath(), content[i])) {
					return false;
				}
			}
		}
		return file.delete();
	}

	public void removeTable (String name) throws IOException {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException();
		}
		tablesLock.lock();
		File table = new File(dbDirectory, name);
		if (!table.isDirectory()) {
			tablesLock.unlock();
			throw new IllegalStateException(name + " not exists");
		}
		if (!delete(dbDirectory, name)) {
			tablesLock.unlock();
			throw new IllegalArgumentException();
		}
		tables.remove(name);
		tablesLock.unlock();
	}

	public Storeable deserialize (Table table, String value) throws ParseException {
		if (value == null) {
			return null;
		}
		value = value.trim();
		Storeable struct = createFor(table);
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			builder.setErrorHandler(new ErrorHandler() {
				public void fatalError (SAXParseException exception) throws SAXException {}
				public void error (SAXParseException exception) throws SAXException {}
				public void warning (SAXParseException exception) throws SAXException {}
			});
			Document document = builder.parse(new InputSource(new StringReader(value)));
			document.getDocumentElement().normalize();
			if (!document.getDocumentElement().getNodeName().equals("row")) {
				throw new ParseException("not a valid format", 0);
			}
			NodeList nodes = document.getDocumentElement().getChildNodes();
			if (nodes.getLength() != table.getColumnsCount()) {
				throw new ParseException("invalid number of columns", 0);
			}
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if (node.getNodeName().equals("null")) {
					continue;
				}
				if (!node.getNodeName().equals("col")) {
					throw new ParseException("not a valid format", 0);
				}
				String innerContent = node.getTextContent();
				String type = getNameByType(table.getColumnType(i));
				switch (type) {
					case "int":
						struct.setColumnAt(i, Integer.parseInt(innerContent));
						break;
					case "long":
						struct.setColumnAt(i, Long.parseLong(innerContent));
						break;
					case "float":
						struct.setColumnAt(i, Float.parseFloat(innerContent));
						break;
					case "byte":
						struct.setColumnAt(i, Byte.parseByte(innerContent));
						break;
					case "double":
						struct.setColumnAt(i, Double.parseDouble(innerContent));
						break;
					case "boolean":
						struct.setColumnAt(i, Boolean.parseBoolean(innerContent));
						break;
					case "String":
						struct.setColumnAt(i, innerContent);
						break;
					default:
						throw new ParseException("unknown type", 0);
				}
			}
		}
		catch (SAXException | IOException | ParserConfigurationException | NumberFormatException e) {
			throw new ParseException(e.getMessage(), 0);
		}
		catch (ParseException e) {
			throw e;
		}
		return struct;
	}

	public String serialize (Table table, Storeable value) throws ColumnFormatException {
		if (value == null) {
			return null;
		}
		String result = "<row>";
		try {
			for (int i = 0; i < table.getColumnsCount(); i++) {
				switch (getNameByType(table.getColumnType(i))) {
					case "int":
						value.getIntAt(i);
						break;
					case "long":
						value.getLongAt(i);
						break;
					case "float":
						value.getFloatAt(i);
						break;
					case "byte":
						value.getByteAt(i);
						break;
					case "double":
						value.getDoubleAt(i);
						break;
					case "boolean":
						value.getBooleanAt(i);
						break;
					case "String":
						value.getStringAt(i);
						break;
				}
				Object obj = value.getColumnAt(i);
				if (obj == null) {
					result += "<null/>";
				}
				else {
					result += "<col>" + obj.toString() + "</col>";
				}
			}
		}
		catch (IndexOutOfBoundsException e) {
			throw new ColumnFormatException(e);
		}
		try {
			value.getColumnAt(table.getColumnsCount());
			throw new ColumnFormatException();
		}
		catch (IndexOutOfBoundsException e) {}
		result += "</row>";
		return result;
	}

	public Storeable createFor (Table table) {
		return new StoreableInstance(table);
	}

	public Storeable createFor (Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
		return new StoreableInstance(table, values);
	}
};