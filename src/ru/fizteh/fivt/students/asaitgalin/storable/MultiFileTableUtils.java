package ru.fizteh.fivt.students.asaitgalin.storable;

import java.util.ArrayList;
import java.util.List;

public class MultiFileTableUtils {
    public static List<Class<?>> getColumnTypes(String[] values) {
        List<Class<?>> columnsList = new ArrayList<>();
        for (String s : values) {
            columnsList.add(MultiFileTableTypes.getClassByName(s));
        }
        return columnsList;
    }

}
