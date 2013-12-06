package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

public class StringMethods {

    public static String join(Iterable<?> objects, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object o: objects) {
            if (!first) {
                sb.append(separator);
            } else {
                first = false;
            }
            sb.append(o.toString());
        }
        return sb.toString();

    }
}
