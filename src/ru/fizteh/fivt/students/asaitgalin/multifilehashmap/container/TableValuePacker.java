package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.container;

public interface TableValuePacker<ValueType> {
    String getValueString(ValueType type) throws Exception;
}
