package ru.fizteh.fivt.students.kislenko.junit;

public class TwoLayeredString {
    private String key;
    private byte[] bytes;

    public TwoLayeredString(String name) {
        key = name;
        bytes = name.getBytes();
    }

    public String getKey() {
        return key;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
