package ru.fizteh.fivt.students.dmitryKonturov.dataBase.utils;

import ru.fizteh.fivt.students.dmitryKonturov.dataBase.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *  Loads simpleDatabase from a file
 */

final class SimpleDatabaseLoaderWriter {

    private SimpleDatabaseLoaderWriter() { }

    private static void readSomeBytes(InputStream input, byte[] toRead, int len) throws DatabaseException, IOException {
        int wasRead = 0;
        try {
            while (wasRead < len) {
                int tmp = input.read(toRead, wasRead, len - wasRead);
                if (tmp == -1) {
                    throw new DatabaseException("Input was shorter then demanded length");
                }
                wasRead += tmp;
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    private static int readInt(InputStream input) throws DatabaseException, IOException {
        byte[] number = new byte[4];
        readSomeBytes(input, number, 4);
        return ByteBuffer.wrap(number).getInt();
    }

    private static String readString(InputStream input, int len, long restToRead) throws DatabaseException,
                                                                                         IOException {
        if (len < 0) {
            throw new DatabaseException("Invalid database file", "Negative string length");
        } else if ((long) len > restToRead) {
            throw new DatabaseException("Invalid database file", "Length of file to read is greater "
                                        + "than unread part of file");
        }
        byte[] stringInBytes = new byte[len];
        readSomeBytes(input, stringInBytes, len);
        return new String(stringInBytes, StandardCharsets.UTF_8);
    }

    public static void databaseLoadFromFile(Map<String, String> dataBase, Path fileToRead) throws DatabaseException,
                                                                                             IOException {
        long fileLength;
        String exceptionPrefix = String.format("Load from \'%s\'", fileToRead.toString());
        try {
            if (!Files.exists(fileToRead)) {
                return;
            }
            fileLength = Files.size(fileToRead);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException(exceptionPrefix, e);
        }

        try (InputStream input = Files.newInputStream(fileToRead)) {
            HashMap<String, String> tmpBase = new HashMap<>();
            while (fileLength > 0) {
                int keyLen = readInt(input);
                int valueLen = readInt(input);
                fileLength -= 8;
                String key = readString(input, keyLen, fileLength);
                fileLength -= keyLen;
                String value = readString(input, valueLen, fileLength);
                fileLength -= valueLen;
                tmpBase.put(key, value);
            }
            Set<Map.Entry<String, String>> mapSet = tmpBase.entrySet();
            for (Map.Entry<String, String> entry : mapSet) {
                dataBase.put(entry.getKey(), entry.getValue());
            }
        } catch (DatabaseException dbe) {
            throw new DatabaseException(exceptionPrefix, dbe);
        } catch (IOException ioexc) {
            throw ioexc;
        } catch (Exception e) {
            throw new DatabaseException(exceptionPrefix, e);
        }
    }

    private static void writeKeyValuePair(OutputStream output, String key, String value) throws DatabaseException,
                                                                                                IOException {
        try {
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
            byte[] keyLen = ByteBuffer.allocate(4).putInt(keyBytes.length).array();
            byte[] valueLen = ByteBuffer.allocate(4).putInt(valueBytes.length).array();
            output.write(keyLen);
            output.write(valueLen);
            output.write(keyBytes);
            output.write(valueBytes);
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public static void databaseWriteToFile(Map<String, String> database, Path fileToWrite) throws DatabaseException,
                                                                                             IOException {

        String exceptionPrefix = String.format("Write to \'%s\'", fileToWrite.toString());
        try (OutputStream output = Files.newOutputStream(fileToWrite)) {
            for (Map.Entry<String, String> entry : database.entrySet()) {
                writeKeyValuePair(output, entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException(exceptionPrefix, e);
        }
    }
}
