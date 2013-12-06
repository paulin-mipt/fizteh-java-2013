package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.students.dubovpavel.filemap.StringSerial;

public class StringTransformer extends StringSerial implements ObjectTransformer<String> {
    public String copy(String obj) {
        String result = obj;
        return result;
    }

    public boolean equal(String left, String right) {
        return left.equals(right);
    }
}
