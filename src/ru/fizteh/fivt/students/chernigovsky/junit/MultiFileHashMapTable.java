package ru.fizteh.fivt.students.chernigovsky.junit;

public class MultiFileHashMapTable extends AbstractTable<String> implements ExtendedMultiFileHashMapTable {
    public MultiFileHashMapTable(String name, boolean flag) {
        super(name, flag);
    }

    public String put(String key, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        return super.put(key, value);
    }

    public boolean valuesEqual(String firstValue, String secondValue) {
        return firstValue.equals(secondValue);
    }
}
