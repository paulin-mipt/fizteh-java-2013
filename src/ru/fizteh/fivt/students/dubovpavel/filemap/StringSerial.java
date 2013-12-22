package ru.fizteh.fivt.students.dubovpavel.filemap;

public class StringSerial implements Serial<String> {
    public String serialize(String obj) {
        return obj;
    }

    public String deserialize(String obj) {
        return obj;
    }
}
