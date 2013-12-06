package ru.fizteh.fivt.students.mishatkin.storable;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.mishatkin.junit.JUnitReceiver;
import ru.fizteh.fivt.students.mishatkin.multifilehashmap.MultiFileHashMapException;
import ru.fizteh.fivt.students.mishatkin.multifilehashmap.MultiFileHashMapTableReceiver;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Vladimir Mishatkin on 11/11/13
 */
public class MultiTypeStorageReceiver /*extends JUnitReceiver*/ implements TableProvider {

	private JUnitReceiver stringReceiver;

	public MultiTypeStorageReceiver(PrintStream out, boolean interactiveMode, String dbDirectory) {
		stringReceiver = new JUnitReceiver(out, interactiveMode, dbDirectory);
	}

	public MultiTypeStorageReceiver(String dbDirectory) {
		stringReceiver = new JUnitReceiver(dbDirectory);
	}

	public void createCommand(String tableName, String types[]) throws IOException{
		List<Class<?>> classes = new ArrayList<>();
		for (String type : types) {
			classes.add(PrimitiveClasses.classForName(type));
		}
		createTable(tableName, classes);
		File configFile = new File(new File(stringReceiver.getDbDirectoryName()), tableName);
		if (configFile.createNewFile()) {
			throw new IOException();
		}
	}

	@Override
	public Table getTable(String name) {
		MultiFileHashMapTableReceiver stringTable = (MultiFileHashMapTableReceiver) stringReceiver.getTable(name);
		if (stringTable == null) {
			return null;
		}
		return new MultiTypeFileMapTableReceiver(stringTable);
	}

	@Override
	public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
		try {
			stringReceiver.createCommand(name);
			MultiTypeFileMapTableReceiver newTable = (MultiTypeFileMapTableReceiver) getTable(name);
			newTable.setCorrespondingClasses(columnTypes);
			return newTable;
		} catch (MultiFileHashMapException e) {
			System.err.println("Something went wrong.");
		}
		return null;
	}

	@Override
	public void removeTable(String name) throws IOException {
		stringReceiver.removeTable(name);
	}

	@Override
	public Storeable deserialize(Table table, String value) throws ParseException {
		Storeable retValue = createFor(table);
		JSONArray parsedValues = new JSONArray(value);
		for (int index = 0; index < parsedValues.length(); ++index) {
			retValue.setColumnAt(index, parsedValues.get(index));
		}
		return retValue;
	}

	@Override
	public String serialize(Table table, Storeable value) throws ColumnFormatException {
		return ((MultiTypeTableRow) value).encode();
	}

	@Override
	public Storeable createFor(Table table) {
		Storeable retValue = new MultiTypeTableRow((MultiTypeFileMapTableReceiver) table);
		return retValue;
	}

	@Override
	public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
		Storeable retValue = createFor(table);
		for (int index = 0; index < values.size(); ++index) {
			retValue.setColumnAt(index, values.get(index));
		}
		return retValue;
	}
}
