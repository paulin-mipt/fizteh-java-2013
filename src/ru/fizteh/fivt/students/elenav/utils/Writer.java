package ru.fizteh.fivt.students.elenav.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Writer {

    public static void writePair(String key, String value, DataOutputStream out) throws IOException {
        byte[] bkey = key.getBytes(StandardCharsets.UTF_8);
        out.writeInt(bkey.length);
        byte[] bvalue = value.getBytes(StandardCharsets.UTF_8);
        out.writeInt(bvalue.length);
        out.write(bkey);
        out.write(bvalue);
    }

}
