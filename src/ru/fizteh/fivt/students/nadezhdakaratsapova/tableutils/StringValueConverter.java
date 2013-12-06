package ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils;

import java.text.ParseException;

public class StringValueConverter implements ValueConverter<String> {

    public String convertValueTypeToString(String value) {
        return value;
    }

    public String convertStringToValueType(String value) throws ParseException {
        return value;
    }
}
