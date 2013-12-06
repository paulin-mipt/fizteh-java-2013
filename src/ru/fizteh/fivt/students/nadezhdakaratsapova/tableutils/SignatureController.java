package ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SignatureController {
    public static final String SIGNATURE_FILE_NAME = "signature.tsv";

    public List<Class<?>> getSignature(File dataDir) throws IllegalArgumentException, IOException {
        File[] files = dataDir.listFiles();
        List<Class<?>> types = null;
        for (File f : files) {
            if (f.getName().equals(SIGNATURE_FILE_NAME)) {
                if (!f.isFile()) {
                    throw new IllegalArgumentException(SIGNATURE_FILE_NAME + " should be a directory");
                }
                DataInputStream inStream = new DataInputStream(new FileInputStream(f));
                long fileLength = f.length();
                if (fileLength == 0) {
                    throw new IllegalArgumentException(SIGNATURE_FILE_NAME + " is empty");
                }
                int curPos = 0;
                byte curByte;
                List<Byte> type = new ArrayList<Byte>();
                types = new ArrayList<Class<?>>();
                while (curPos < fileLength) {
                    while ((curPos < fileLength) && ((curByte = inStream.readByte()) != ' ')) {
                        type.add(curByte);
                        ++curPos;
                    }
                    int arraySize = type.size();
                    byte[] typeInBytes = new byte[arraySize];
                    for (int j = 0; j < arraySize; ++j) {
                        typeInBytes[j] = type.get(j);
                    }
                    String typeToReturn = new String(typeInBytes, StandardCharsets.UTF_8);
                    type.clear();
                    types.add(StoreableColumnType.getClassFromPrimitive(typeToReturn));
                    ++curPos;
                }
            }
        }
        return types;
    }

    public void checkSignatureValidity(List<Class<?>> columnTypes) {
        for (Class<?> cls : columnTypes) {
            if (cls == null) {
                throw new IllegalArgumentException("Not allowed type of signature");
            }
            StoreableColumnType.getPrimitive(cls);
        }
    }


    public String convertStoreableFieldToString(Storeable value, int columnIndex, Class<?> columnType) {
        String ret = null;
        if (value == null) {
            throw new IllegalArgumentException("Not allowed type of signature");
        }
        if (value.getColumnAt(columnIndex) != null) {
            ret = StoreableColumnType.getStoreableField(columnIndex, value, columnType).toString();
        }
        return ret;
    }

    public static List<Class<?>> getColumnTypes(Table table) throws IllegalArgumentException {
        if (table == null) {
            throw new IllegalArgumentException("The null table is not allowed");
        }
        int columnCount = table.getColumnsCount();
        List<Class<?>> types = new ArrayList<Class<?>>();
        for (int i = 0; i < columnCount; ++i) {
            types.add(table.getColumnType(i));
        }
        return types;
    }

    public void writeSignatureToFile(File file, List<Class<?>> columnTypes) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(file));
        for (Class<?> cls : columnTypes) {
            outputStream.write((StoreableColumnType.getPrimitive(cls) + ' ').getBytes(StandardCharsets.UTF_8));

        }
        outputStream.close();
    }

    public void checkValueForTable(int columnIndex, Table table, Storeable value) throws IndexOutOfBoundsException,
            ColumnFormatException {
        StoreableColumnType.getStoreableField(columnIndex, value, table.getColumnType(columnIndex));
    }

    public static List<Class<?>> getSignatureFromArgs(String[] args) throws IOException {
        SignatureController signatureController = new SignatureController();
        int argsCount = args.length;
        StringBuilder signature = new StringBuilder();
        for (int i = 2; i < argsCount; ++i) {
            signature.append(args[i]);
        }
        if ((args[2].charAt(0) != '(') || (args[argsCount - 1].charAt(args[argsCount - 1].length() - 1) != ')')) {
            throw new IOException("The wrong type of command arguments. They should be in brackets");
        }
        List<Class<?>> types = new ArrayList<>();
        String firstType;
        if ((argsCount - 2) == 1) {
            if (args[2].length() < 2 || args[2].charAt(args[2].length() - 1) != ')') {
                throw new IllegalArgumentException("wrong type " + signature);
            }
            firstType = new String(args[2].substring(1, args[2].length() - 1));
            try {
                types.add(StoreableColumnType.getClassFromPrimitive(firstType.trim()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("wrong type " + signature);
            }
            return types;
        }
        String lastType = null;
        if (args[argsCount - 1].length() == 1) {
            if (!args[argsCount - 1].equals(")")) {
                throw new IOException("The wrong type of command arguments. They should be in brackets");
            }
        } else {
            lastType = new String(args[argsCount - 1].substring(0, args[argsCount - 1].length() - 1));
        }
        firstType = new String(args[2].substring(1));
        try {
            types.add(StoreableColumnType.getClassFromPrimitive(firstType.trim()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("wrong type " + signature);
        }
        for (int i = 3; i < argsCount - 1; ++i) {
            try {
                types.add(StoreableColumnType.getClassFromPrimitive(args[i].trim()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("wrong type " + signature);
            }
        }
        if (lastType != null) {
            try {
                types.add(StoreableColumnType.getClassFromPrimitive(lastType.trim()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage() + ')');
            }
        }
        return types;
    }
}

