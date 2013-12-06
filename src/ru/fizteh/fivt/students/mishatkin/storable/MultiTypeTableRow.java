package ru.fizteh.fivt.students.mishatkin.storable;

import org.json.JSONArray;
import org.json.JSONException;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.lang.ref.WeakReference;

/**
 * Created by Vladimir Mishatkin on 11/11/13
 */
public class MultiTypeTableRow implements Storeable {

	private JSONArray data = new JSONArray();

	private WeakReference<MultiTypeFileMapTableReceiver> delegate = null;

	public MultiTypeTableRow(MultiTypeFileMapTableReceiver aDelegate) {
		setDelegate(aDelegate);
	}

	public String encode() throws ColumnFormatException {
		for (int i = 0; i < data.length(); ++i) {
			if (!getColumnType(i).isAssignableFrom(getColumnAt(i).getClass())) {
				throw new ColumnFormatException();
			}
		}
		return data.toString();
	}

	public MultiTypeFileMapTableReceiver getDelegate() {
		return delegate.get();
	}

	public void setDelegate(MultiTypeFileMapTableReceiver delegate) {
		this.delegate = new WeakReference<>(delegate);
	}

	private Class<?> getColumnType(int columnIndex) {
		return getDelegate().getColumnType(columnIndex);
	}

	private int getColumnsCount() {
		assert data.length() == getDelegate().getColumnsCount();
		return getDelegate().getColumnsCount();
	}

	private boolean isGoodIndex(int index) {
		return index >= 0 && index < getColumnsCount();
	}

	@Override
	public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
		if (!isGoodIndex(columnIndex)) {
			throw new IndexOutOfBoundsException(String.valueOf(columnIndex));
		}
		Class<?> clazz = getColumnType(columnIndex);
		if (clazz.isAssignableFrom(value.getClass())) {
			data.put(columnIndex, value);
		} else {
			throw new ColumnFormatException();
		}
	}

	@Override
	public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
		if (!isGoodIndex(columnIndex)) {
			throw new IndexOutOfBoundsException();
		}
		return data.get(columnIndex);
	}

	@Override
	public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		if (!isGoodIndex(columnIndex)) {
			throw new IndexOutOfBoundsException();
		}
		try {
			return data.getInt(columnIndex);
		} catch (JSONException e) {
			throw new ColumnFormatException(e);
		}
	}

	@Override
	public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		if (!isGoodIndex(columnIndex)) {
			throw new IndexOutOfBoundsException();
		}
		try {
			return data.getLong(columnIndex);
		} catch (JSONException e) {
			throw new ColumnFormatException(e);
		}
	}

	@Override
	public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		Object value = getColumnAt(columnIndex);
		if (Byte.class.isAssignableFrom(value.getClass())) {
			return (Byte)value;
		} else {
			throw new ColumnFormatException();
		}
	}

	@Override
	public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		Object value = getColumnAt(columnIndex);
		if (Float.class.isAssignableFrom(value.getClass())) {
			return (Float)value;
		} else {
			throw new ColumnFormatException();
		}
	}

	@Override
	public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		if (!isGoodIndex(columnIndex)) {
			throw new IndexOutOfBoundsException();
		}
		try {
			return data.getDouble(columnIndex);
		} catch (JSONException e) {
			throw new ColumnFormatException(e);
		}
	}

	@Override
	public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		if (!isGoodIndex(columnIndex)) {
			throw new IndexOutOfBoundsException();
		}
		try {
			return data.getBoolean(columnIndex);
		} catch (JSONException e) {
			throw new ColumnFormatException(e);
		}
	}

	@Override
	public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		if (!isGoodIndex(columnIndex)) {
			throw new IndexOutOfBoundsException();
		}
		try {
			return data.getString(columnIndex);
		} catch (JSONException e) {
			throw new ColumnFormatException(e);
		}
	}
}
