package ru.fizteh.fivt.students.demidov.storeable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class StoreableUtils {
	private StoreableUtils() {}
	
	public static List<Class<?>> parseArguments(String[] arguments) throws IOException {
		if ((!(arguments[0].startsWith("("))) || (!(arguments[arguments.length - 1].endsWith(")")))) {
			throw new IOException("incorrect create format");
		}

		arguments[0] = arguments[0].substring(1);
		arguments[arguments.length - 1] = arguments[arguments.length - 1].substring(0,
		        arguments[arguments.length - 1].length() - 1);

		List<Class<?>> columnType = new ArrayList<>();
		for (int column = 0; column < arguments.length; ++column) {
			columnType.add(TypeName.getAppropriateClass(arguments[column]));
		}

		return columnType;
	}
	
	public static List<Class<?>> getClasses(String directory) throws IOException {
		List<Class<?>> gotColumns = new ArrayList<>();
		try (Scanner formatData = new Scanner(new File(directory, "signature.tsv"))) {
			while (formatData.hasNext()) {
				Class<?> currentClass = TypeName.getAppropriateClass(formatData.next());
				gotColumns.add(currentClass);
			}
			if (gotColumns.isEmpty()) {
				throw new IOException("empty signature");
			}
		} catch (FileNotFoundException catchedException) {
			throw new IOException("file with description of signature isn't found");
		}
		return gotColumns;
	}
	
	public static String serialize(Table table, Storeable value) throws XMLStreamException {
		if (value == null) {
			return null;
		}
		StringWriter builtString = new StringWriter();
		XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(builtString);
		try {
			writer.writeStartElement("row");
			for (int column = 0; column < table.getColumnsCount(); ++column) {
				Object columnValue = value.getColumnAt(column);
				if (columnValue != null) {
					writer.writeStartElement("col");
					if (table.getColumnType(column) != columnValue.getClass()) {
						throw new ColumnFormatException("column " + column + " type mismatch");
					}
					writer.writeCharacters(columnValue.toString());
					writer.writeEndElement();
				} else {
					writer.writeEmptyElement("null");
				}
			}
			writer.writeEndElement();
		} catch (IndexOutOfBoundsException catchedException) {
			throw new ColumnFormatException("incorrect size");
		}

		return builtString.toString();
	}

	public static StoreableImplementation deserialize(Table table, String value)
	        throws XMLStreamException, ParseException {
		if (value == null) {
			return null;
		}
		XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(value));
		StoreableImplementation builtStoreable = new StoreableImplementation(table);

		reader.next();
		if ((!(reader.isStartElement())) || (!(reader.getName().getLocalPart().equals("row")))) {
			throw new ParseException("", 0);
		}
		
		reader.next();
		for (int column = 0; column < table.getColumnsCount(); ++column) {
			if ((reader.isStartElement()) && (reader.getName().getLocalPart().equals("col"))) {
				reader.next();
				if (reader.isCharacters()) {
					builtStoreable.setColumnAt(column, parseObject(reader.getText(),
					        table.getColumnType(column).getSimpleName()));
				} else {
					throw new ParseException("", 0);
				}
			} else if ((reader.isStartElement()) && (reader.getName().getLocalPart().equals("null"))) {
				builtStoreable.setColumnAt(column, null);
			} else {
				throw new ParseException("", 0);
			}

			reader.next();
			if (!(reader.isEndElement())) {
				throw new ParseException("", 0);
			}
			
			reader.next();
		}		

		if (!(reader.isEndElement())) {
			throw new ParseException("", 0);
		}

		return builtStoreable;
	}

	public static Object parseObject(String string, String expectedClassName) throws ParseException {
		try {
			switch (expectedClassName) {
			case "Boolean":
				return Boolean.parseBoolean(string);
			case "Byte":
				return Byte.parseByte(string);
			case "Float":
				return Float.parseFloat(string);
			case "Double":
				return Double.parseDouble(string);
			case "Integer":
				return Integer.parseInt(string);
			case "Long":
				return Long.parseLong(string);
			case "String":
				return string;
			default:
				throw new ParseException("", 0); 					
			}
		} catch (NumberFormatException catchedException) {
			throw new ParseException("", 0);
		}
	}
}
