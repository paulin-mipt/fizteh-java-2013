package ru.fizteh.fivt.students.vyatkina.database.superior;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DatabaseUtils {

    public static final int MAX_SUPPORTED_SIZE = 1024 * 1024;

    public static class KeyValue {

        public String key;
        public String value;

        public KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }


    public static void writeKeyValue(KeyValue pair, DataOutputStream out) throws IOException {
        byte[] keyBytes = pair.key.getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = pair.value.getBytes(StandardCharsets.UTF_8);
        out.writeInt(keyBytes.length);
        out.writeInt(valueBytes.length);
        out.write(keyBytes);
        out.write(valueBytes);
    }

    public static KeyValue readKeyValue(DataInputStream in) throws IOException, IllegalArgumentException {
        int keySize = in.readInt();
        validSizeCheck(keySize);
        int valueSize = in.readInt();
        validSizeCheck(valueSize);

        byte[] keyBytes = new byte[keySize];
        byte[] valueBytes = new byte[valueSize];
        in.read(keyBytes);
        in.read(valueBytes);
        String key = new String(keyBytes, StandardCharsets.UTF_8);
        String value = new String(valueBytes, StandardCharsets.UTF_8);

        return new KeyValue(key, value);
    }

    protected static void validSizeCheck(int size) throws IllegalArgumentException {
        if ((size > MAX_SUPPORTED_SIZE) || (size < 0)) {
            throw new IllegalArgumentException("Invalid size");
        }
    }

}
