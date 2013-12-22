package ru.fizteh.fivt.students.vlmazlov.shell;

import java.util.Collection;

public class StringUtils {
    public static String join(Collection<?> items, String separator) {
        boolean first = true;
        StringBuilder joinBuilder = new StringBuilder();

        for (Object item : items) {

            if (!first) {
                joinBuilder.append(separator);
            }

            first = false;
            joinBuilder.append(item.toString());
        }

        return joinBuilder.toString();
    }
}
