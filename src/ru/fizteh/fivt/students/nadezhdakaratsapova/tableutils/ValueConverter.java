package ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils;

import java.text.ParseException;

public interface ValueConverter<ValueType> {

    String convertValueTypeToString(ValueType value);

    ValueType convertStringToValueType(String value) throws ParseException;
}
