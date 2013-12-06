package ru.fizteh.fivt.students.fedoseev.common;

public class Utils {
    private Utils() {
    }

    public static String join(String[] items, String sep) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (String item : items) {
            if (!first) {
                sb.append(sep);
            }
            first = false;
            sb.append(item);
        }

        return sb.toString();
    }

    public static String[] getCommandArguments(String inputString) {
        int begin;

        if ((begin = inputString.indexOf(" ")) == -1) {
            return new String[0];
        }

        return inputString.trim().substring(begin + 1, inputString.length()).trim().split("\\s+");
    }
}
