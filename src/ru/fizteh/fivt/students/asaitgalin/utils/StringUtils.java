package ru.fizteh.fivt.students.asaitgalin.utils;

public class StringUtils {

    public static String join(Iterable<?> objects, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (Object o : objects) {
            if (o == null) {
                continue;
            }
            if (!isFirst) {
                sb.append(separator);
            } else {
                isFirst = false;
            }
            sb.append(o.toString());
        }
        return sb.toString();
    }

}
