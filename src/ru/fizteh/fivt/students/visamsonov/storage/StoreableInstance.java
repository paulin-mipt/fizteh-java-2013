package ru.fizteh.fivt.students.visamsonov.storage;

import ru.fizteh.fivt.storage.structured.*;
import java.util.List;
import java.util.ArrayList;

public class StoreableInstance implements Storeable {

	private final List<Object> values;
	private final Table table;

	private void checkCorrectType (int index, Class<?> classType) throws ColumnFormatException {
		if (!classType.isAssignableFrom(table.getColumnType(index))) {
			throw new ColumnFormatException();
		}
	}

	private void checkCorrectIndex (int index) throws IndexOutOfBoundsException {
		if (index < 0 || index >= table.getColumnsCount()) {
			throw new IndexOutOfBoundsException();
		}
	}

	public StoreableInstance (Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
		this.table = table;
		this.values = new ArrayList(values);
		if (values.size() != table.getColumnsCount()) {
			throw new IndexOutOfBoundsException();
		}
		for (int i = 0; i < values.size(); i++) {
			checkCorrectType(i, values.get(i).getClass());
		}
	}

	public StoreableInstance (Table table) {
		this.table = table;
		values = new ArrayList(table.getColumnsCount());
		for (int i = 0; i < table.getColumnsCount(); i++) {
			values.add(null);
		}
	}

	public void setColumnAt (int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
		checkCorrectIndex(columnIndex);
		if (value != null) {
			checkCorrectType(columnIndex, value.getClass());
		}
		values.set(columnIndex, value);
	}

	public Object getColumnAt (int columnIndex) throws IndexOutOfBoundsException {
		checkCorrectIndex(columnIndex);
		return values.get(columnIndex);
	}

	public Integer getIntAt (int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		checkCorrectIndex(columnIndex);
		checkCorrectType(columnIndex, Integer.class);
		return (Integer) values.get(columnIndex);
	}

	public Long getLongAt (int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		checkCorrectIndex(columnIndex);
		checkCorrectType(columnIndex, Long.class);
		return (Long) values.get(columnIndex);
	}

	public Byte getByteAt (int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		checkCorrectIndex(columnIndex);
		checkCorrectType(columnIndex, Byte.class);
		return (Byte) values.get(columnIndex);
	}

	public Float getFloatAt (int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		checkCorrectIndex(columnIndex);
		checkCorrectType(columnIndex, Float.class);
		return (Float) values.get(columnIndex);
	}

	public Double getDoubleAt (int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		checkCorrectIndex(columnIndex);
		checkCorrectType(columnIndex, Double.class);
		return (Double) values.get(columnIndex);
	}

	public Boolean getBooleanAt (int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		checkCorrectIndex(columnIndex);
		checkCorrectType(columnIndex, Boolean.class);
		return (Boolean) values.get(columnIndex);
	}

	public String getStringAt (int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		checkCorrectIndex(columnIndex);
		checkCorrectType(columnIndex, String.class);
		return (String) values.get(columnIndex);
	}
}