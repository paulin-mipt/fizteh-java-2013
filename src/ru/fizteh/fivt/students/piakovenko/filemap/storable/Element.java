package ru.fizteh.fivt.students.piakovenko.filemap.storable;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 09.11.13
 * Time: 7:48
 * To change this template use File | Settings | File Templates.
 */

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class Element implements Storeable {
    private List<Class<?>> storageClasses = null;
    private List<Object> storage = null;

    private boolean columnChecker(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= storageClasses.size()) {
            return false;
        }
        return true;
    }

    private void classChecker(int columnIndex, Class<?> value) {
        if (!value.isAssignableFrom(storageClasses.get(columnIndex))) {
            throw new ColumnFormatException(String.format("Incorrect type: expected %s, but is %s",
                    storageClasses.get(columnIndex).getName(), value.getName()));
        }
    }


    public Element(List<Class<?>> classes) {
        storageClasses = new ArrayList<Class<?>>(classes);
        storage = new ArrayList<Object>();
        for (int i = 0; i < storageClasses.size(); ++i) {
            storage.add(null);
        }
    }

    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        classChecker(columnIndex, value.getClass());
        if (!columnChecker(columnIndex)) {
            throw  new IndexOutOfBoundsException("setColumnAt - wrong index!");
        }
        if (value != null) {
            classChecker(columnIndex, value.getClass());
            if ((value.getClass().getName().equals("java.lang.String"))
                    && ((String) value).trim().isEmpty()) {
                storage.set(columnIndex, value);
                return;
            }
            classChecker(columnIndex, value.getClass());
        }
        storage.set(columnIndex, value);
    }

    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (!columnChecker(columnIndex)) {
            throw  new IndexOutOfBoundsException("getColumnAt - wrong index!");
        }
        return storage.get(columnIndex);
    }

    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!columnChecker(columnIndex)) {
            throw  new IndexOutOfBoundsException("getIntAt - wrong index!");
        }
        classChecker(columnIndex, Integer.class);
        return (Integer) storage.get(columnIndex);
    }

    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!columnChecker(columnIndex)) {
            throw  new IndexOutOfBoundsException("getLongAt - wrong index!");
        }
        classChecker(columnIndex, Long.class);
        return (Long) storage.get(columnIndex);
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!columnChecker(columnIndex)) {
            throw  new IndexOutOfBoundsException("getByteAt - wrong index!");
        }
        classChecker(columnIndex, Byte.class);
        return (Byte) storage.get(columnIndex);
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!columnChecker(columnIndex)) {
            throw  new IndexOutOfBoundsException("getFloatAt - wrong index!");
        }
        classChecker(columnIndex, Float.class);
        return (Float) storage.get(columnIndex);
    }

    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!columnChecker(columnIndex)) {
            throw  new IndexOutOfBoundsException("getDoubleAt - wrong index!");
        }
        classChecker(columnIndex, Double.class);
        return (Double) storage.get(columnIndex);
    }

    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!columnChecker(columnIndex)) {
            throw  new IndexOutOfBoundsException("getBooleanAt - wrong index!");
        }
        classChecker(columnIndex, Boolean.class);
        return (Boolean) storage.get(columnIndex);
    }

    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!columnChecker(columnIndex)) {
            throw  new IndexOutOfBoundsException("getStringAt - wrong index!");
        }
        classChecker(columnIndex, String.class);
        return (String) storage.get(columnIndex);
    }

    @Override
     public boolean equals(Object obj) {
        Element row = (Element) obj;
        return row.storageClasses.equals(storageClasses) && row.storage.equals(storage);
    }

    @Override
    public int hashCode() {
        return 0;
    }

}
