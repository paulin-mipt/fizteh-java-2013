package ru.fizteh.fivt.students.chernigovsky.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.chernigovsky.junit.AbstractTable;

import java.util.List;

public class StoreableTable extends AbstractTable<Storeable> implements ExtendedStoreableTable {
    List<Class<?>> columnTypeList;
    ExtendedStoreableTableProvider tableProvider;

    public void setColumnTypeList(List<Class<?>> newColumnTypeList) {
        columnTypeList = newColumnTypeList;
    }

    public StoreableTable(String name, boolean flag, List<Class<?>> newColumnTypeList, ExtendedStoreableTableProvider newTableProvider) {
        super(name, flag);
        columnTypeList = newColumnTypeList;
        tableProvider = newTableProvider;
    }

    public int getColumnsCount() {
        return columnTypeList.size();
    }

    /**
     * Возвращает тип значений в колонке.
     *
     * @param columnIndex Индекс колонки. Начинается с нуля.
     * @return Класс, представляющий тип значения.
     *
     * @throws IndexOutOfBoundsException - неверный индекс колонки
     */
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return columnTypeList.get(columnIndex);
    }

    public Storeable put(String key, Storeable value) {
        if (value == null) { // maybe need to check: value.trim().isEmpty()
            throw new IllegalArgumentException("value is null");
        }
        if (!StoreableUtils.checkValue(this, value)) {
            throw new ColumnFormatException("invalid value");
        }
        return super.put(key, value);
    }


    public boolean valuesEqual(Storeable firstValue, Storeable secondValue) {
        return tableProvider.serialize(this, firstValue).equals(tableProvider.serialize(this, secondValue));
    }



}
