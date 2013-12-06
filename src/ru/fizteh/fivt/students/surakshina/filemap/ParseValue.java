package ru.fizteh.fivt.students.surakshina.filemap;

public class ParseValue {
    public static String[] parse(String input) {
        StringBuilder str = new StringBuilder(input);
        str = str.deleteCharAt(0);
        str = str.deleteCharAt(str.length() - 1);
        String[] result = str.toString().split("\\s+");
        return result;
    }
}
