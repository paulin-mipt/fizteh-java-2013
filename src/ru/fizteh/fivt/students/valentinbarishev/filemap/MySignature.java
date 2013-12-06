package ru.fizteh.fivt.students.valentinbarishev.filemap;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MySignature {
    static final String[] TYPES = {"int", "long", "byte", "float", "double", "boolean", "String"};
    static final Class<?>[] CLASSES = {Integer.class, Long.class, Byte.class, Float.class,
                                       Double.class, Boolean.class, String.class};


    public static List<Class<?>> getSignature(final String dir) throws IOException {
        File file = new File(dir, "signature.tsv");
        if (!file.exists()) {
            throw new IOException("Cannot find file:" + file.getCanonicalPath());
        }

        StringBuilder builder = new StringBuilder();

        try (Scanner input = new Scanner(file)) {
            if (!input.hasNext()) {
                throw new IOException("Empty signature: " + file.getCanonicalPath());
            }
            while (input.hasNext()) {
                builder.append(input.next()).append(" ");
            }
        }

        String[] data = builder.toString().split(" ");

        List<Class<?>> result = new ArrayList<>();

        if (data.length <= 0) {
            throw new IOException("Empty signature: " + file.getCanonicalPath());
        }

        for (int i = 0; i < data.length; ++i) {
            boolean flag = false;
            for (int j = 0; j < TYPES.length; ++j) {
                if (data[i].equals(TYPES[j])) {
                    result.add(CLASSES[j]);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                throw new IOException("Invalid type!");
            }
        }
        return result;
    }

    public static void setSignature(final String dir, List<Class<?>> classesList) throws IOException {
        try (PrintWriter output = new PrintWriter(new File(dir, "signature.tsv"))) {
            for (int i = 0; i < classesList.size(); ++i) {
                boolean flag = false;
                for (int j = 0; j < CLASSES.length; ++j) {
                    if (CLASSES[j].equals(classesList.get(i))) {
                        output.write(TYPES[j]);
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    throw new IllegalArgumentException("Bad TYPES!");
                }
                if (i + 1 != classesList.size()) {
                    output.write(" ");
                }
            }
        }

    }

    public static List<Class<?>> getTypes(final String str) throws IOException {
        List<Class<?>> result = new ArrayList<>();
        byte[] s = str.trim().getBytes();
        if (!(s[0] == '(' && s[str.length() - 1] == ')')) {
            throw new IOException("wrong type (no brackets)");
        }
        for (int i = 1; i < str.length() - 1; ++i) {
            if (s[i] == ' ') {
                continue;
            }

            boolean flag = false;
            for (int j = 0; j < TYPES.length; ++j) {
                if (new String(s, i, Math.min(TYPES[j].length(), str.length() - i)).equals(TYPES[j])) {
                    result.add(CLASSES[j]);
                    i += TYPES[j].length();
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                throw new IOException("Cannot read type! position: " + i);
            }
        }
        return result;
    }
}
