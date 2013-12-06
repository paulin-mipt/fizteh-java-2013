package ru.fizteh.fivt.students.demidov.storeable;

import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class StoreableImplementation implements Storeable {
	private List<Object> storableValues;    
	private Table table;
	
	public StoreableImplementation(Table table) {
		storableValues = new ArrayList<Object>(table.getColumnsCount());
		for (int position = 0; position < table.getColumnsCount(); ++position) {
			storableValues.add(null);
		}
		
		this.table = table;
	}

	public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
		if ((value != null) && (!(value.getClass().equals(table.getColumnType(columnIndex))))) {
			throw new ColumnFormatException("incorrect column " + columnIndex + " format");
		}
		storableValues.set(columnIndex, value);
	}

	public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
		if (storableValues.isEmpty()) {
			return null;
		}
		return storableValues.get(columnIndex);
	}

	public Object getObjectOfClassAt(int columnIndex, Object ObjectOfClass) throws ColumnFormatException {
		Object value = storableValues.get(columnIndex);
		if ((value != null) && (!(value.getClass() == ObjectOfClass))) {
			throw new ColumnFormatException();
		}
		return value;
	}
	
	public Integer getIntAt(int columnIndex) throws ColumnFormatException {
		return (Integer)getObjectOfClassAt(columnIndex, Integer.class);
	}

	public Long getLongAt(int columnIndex) throws ColumnFormatException {
		return (Long)getObjectOfClassAt(columnIndex, Long.class);
	}

	public Byte getByteAt(int columnIndex) throws ColumnFormatException {
		return (Byte)getObjectOfClassAt(columnIndex, Byte.class);
	}

	public Float getFloatAt(int columnIndex) throws ColumnFormatException {
		return (Float)getObjectOfClassAt(columnIndex, Float.class);
	}

	public Double getDoubleAt(int columnIndex) throws ColumnFormatException {
		return (Double)getObjectOfClassAt(columnIndex, Double.class);
	}

	public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException {
		return (Boolean)getObjectOfClassAt(columnIndex, Boolean.class);
	}

	public String getStringAt(int columnIndex) throws ColumnFormatException {
		return (String)getObjectOfClassAt(columnIndex, String.class);
	}
}
