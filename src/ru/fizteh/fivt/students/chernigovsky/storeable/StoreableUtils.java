package ru.fizteh.fivt.students.chernigovsky.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class StoreableUtils {

    public static boolean checkValue(Table table, Storeable value) {
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            try {
                value.getColumnAt(i);
            } catch (IndexOutOfBoundsException ex) {
                return false;
            }
        }

        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (value.getColumnAt(i) != null && !table.getColumnType(i).isAssignableFrom(value.getColumnAt(i).getClass())) {
                return false;
            }
        }

        try {
            value.getColumnAt(table.getColumnsCount());
        } catch (IndexOutOfBoundsException ex) {
            return true;
        }

        return false;
    }

    public static void readTable(ExtendedStoreableTable table, ExtendedStoreableTableProvider tableProvider) throws IOException{
        File tableFolder = new File(tableProvider.getDbDirectory(), table.getName());
        File signature = new File(tableFolder, "signature.tsv");
        if (!signature.exists() || !signature.isFile()) {
            throw new IOException("no signature file");
        }
        Scanner scanner = new Scanner(signature);

        List<Class<?>> columnTypeList = new ArrayList<Class<?>>();
        while (scanner.hasNext()) {
            String type = scanner.next();
            if (TypeEnum.getBySignature(type) == null) {
                throw new IOException("wrong type (in read signature)");
            }
            columnTypeList.add(TypeEnum.getBySignature(type).getClazz());
        }

        if (columnTypeList.size() == 0) {
            throw new IOException("empty signature");
        }

        table.setColumnTypeList(columnTypeList);

        for (Integer directoryNumber = 0; directoryNumber < 16; ++directoryNumber) {
            File directory = new File(tableFolder, directoryNumber.toString() + ".dir");
            if (!directory.exists()) {
                continue;
            }
            if (!directory.isDirectory() || directory.list().length == 0) {
                throw new IOException("Corrupted database");
            }

            for (Integer fileNumber = 0; fileNumber < 16; ++fileNumber) {
                File file = new File(directory, fileNumber.toString() + ".dat");
                if (!file.exists()) {
                    continue;
                }
                if (!file.isFile() || file.length() == 0) {
                    throw new IOException("Corrupted database");
                }

                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
                try {
                    while (true) {
                        int keyLength;
                        int valueLength;

                        try {
                            keyLength = dataInputStream.readInt();
                        } catch (EOFException ex) {
                            break;
                        }
                        valueLength = dataInputStream.readInt();

                        if (keyLength <= 0 || valueLength <= 0 || keyLength > 1048576 || valueLength > 1048576) {
                            throw new IOException("Wrong string size");
                        }
                        byte[] keyBytes = new byte[keyLength];
                        byte[] valueBytes = new byte[valueLength];

                        dataInputStream.readFully(keyBytes);
                        dataInputStream.readFully(valueBytes);

                        if (keyBytes.length != keyLength || valueBytes.length != valueLength) {
                            throw new IOException("Corrupted database");
                        }
                        if (Math.abs(keyBytes[0]) % 16 != directoryNumber || Math.abs(keyBytes[0]) / 16 % 16 != fileNumber) {
                            throw new IOException("Corrupted database");
                        }

                        String key = new String(keyBytes, "UTF-8");
                        String value = new String(valueBytes, "UTF-8");

                        Storeable deserializedValue;

                        try {
                            deserializedValue = tableProvider.deserialize(table, value);
                        } catch (ParseException ex) {
                            throw new IOException("Corrupted database");
                        }
                        table.put(key, deserializedValue);
                    }
                } finally {
                    dataInputStream.close();
                }

            }

        }
    }

    public static void writeTable(ExtendedStoreableTable table, ExtendedStoreableTableProvider tableProvider) throws IOException {

        for (Integer directoryNumber = 0; directoryNumber < 16; ++directoryNumber) {
            File tableFolder = new File(tableProvider.getDbDirectory(), table.getName());
            File dir = new File(tableFolder, directoryNumber.toString() + ".dir");

            for (Integer fileNumber = 0; fileNumber < 16; ++fileNumber) {
                HashMap<String, Storeable> currentMap = new HashMap<String, Storeable>();
                for (Map.Entry<String, Storeable> entry : table.getEntrySet()) {
                    if (Math.abs(entry.getKey().getBytes("UTF-8")[0]) % 16 == directoryNumber && Math.abs(entry.getKey().getBytes("UTF-8")[0]) / 16 % 16 == fileNumber) {
                        currentMap.put(entry.getKey(), entry.getValue());
                    }
                }

                File file = new File(dir, fileNumber.toString() + ".dat");

                if (currentMap.size() == 0) {
                    if (file.exists()) {
                        if (!file.delete()) {
                            throw new IOException("Delete error");
                        }
                    }
                    continue;
                }

                if (!dir.exists()) {
                    dir.mkdir();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.getChannel().truncate(0); // Clear file
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
                try {
                    for (Map.Entry<String, Storeable> entry : currentMap.entrySet()) {
                        String serializedValue = tableProvider.serialize(table, entry.getValue());
                        dataOutputStream.writeInt(entry.getKey().getBytes("UTF-8").length);
                        dataOutputStream.writeInt(serializedValue.getBytes("UTF-8").length);
                        dataOutputStream.write(entry.getKey().getBytes("UTF-8"));
                        dataOutputStream.write(serializedValue.getBytes("UTF-8"));
                    }
                } finally {
                    dataOutputStream.close();
                }

            }

            if (dir.exists() && dir.list().length == 0) {
                if (!dir.delete()) {
                    throw new IOException("Delete");
                }
            }
        }
    }
}
