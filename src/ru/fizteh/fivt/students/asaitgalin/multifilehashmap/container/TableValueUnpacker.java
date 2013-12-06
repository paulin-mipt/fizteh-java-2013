package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.container;

public interface TableValueUnpacker<ValueType> {
    ValueType getValueFromString(String value) throws Exception;
}
