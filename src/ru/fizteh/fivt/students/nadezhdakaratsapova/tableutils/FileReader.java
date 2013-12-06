package ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileReader<ValueType> {

    public static final int MAX_FILE_SIZE = 1024 * 1024;
    public static final int SEPARATOR_SIZE = 1;
    public static final int INT_SIZE = 4;
    private File dataFile;
    private DataInputStream inStream;
    private int curPos = 0;
    private int offset1 = 1;
    private Integer prevOffset = 0;
    private UniversalDataTable dataTable;
    private List<Byte> key = new ArrayList<Byte>();
    private List<Integer> offsets = new ArrayList<Integer>();
    private List<String> keysToMap = new ArrayList<String>();
    private int curValue = 0;


    public FileReader(File file, UniversalDataTable table) throws FileNotFoundException {
        dataFile = file;
        dataTable = table;
        if (file.length() != 0) {
            inStream = new DataInputStream(new FileInputStream(file));
        }
    }

    public boolean checkingLoadingConditions() {
        return (curPos < dataFile.length()) && (offset1 != 0);
    }

    public String getNextKey() throws IOException {
        long fileLength = dataFile.length();
        byte curByte;
        while ((curPos < fileLength) && ((((offset1 != 0)) && (curByte = inStream.readByte()) != '\0'))) {
            key.add(curByte);
            ++curPos;
            if (curPos > MAX_FILE_SIZE) {
                throw new IOException("too big key");
            }
        }
        if (curPos == fileLength) {
            throw new IOException("not allowable format of data");
        }
        if ((offset1 == 0) && (!key.isEmpty())) {
            throw new IOException("the last offset had to be before values");
        }
        int arraySize = key.size();
        byte[] keyInBytes = new byte[arraySize];
        for (int j = 0; j < arraySize; ++j) {
            keyInBytes[j] = key.get(j);
        }
        key.clear();
        String keyToMap = new String(keyInBytes, StandardCharsets.UTF_8);
        keysToMap.add(keyToMap);
        if (prevOffset == 0) {
            offset1 = inStream.readInt();
            curPos += INT_SIZE;
            ++curPos;
            prevOffset = offset1;
            offset1 -= arraySize + SEPARATOR_SIZE + INT_SIZE;
            return keyToMap;
        }
        int offset = inStream.readInt();
        curPos += INT_SIZE;
        offset1 -= arraySize + SEPARATOR_SIZE + INT_SIZE;
        int offsetValue = offset - prevOffset;
        prevOffset = offset;
        offsets.add(offsetValue);
        ++curPos;
        return keyToMap;
    }

    public boolean valuesToReadExists() {
        int offsetsSize = 0;
        if (!offsets.isEmpty()) {
            offsetsSize = offsets.size();
        }
        return (curValue <= offsetsSize);
    }

    public String getNextValue() throws IOException {
        if (curValue == offsets.size()) {
            long fileLength = dataFile.length();
            if (curPos < fileLength) {
                int lastOffset = (int) (fileLength - curPos);
                byte[] b = new byte[lastOffset];
                for (int k = 0; curPos < fileLength; ++k, ++curPos) {
                    b[k] = inStream.readByte();
                }
                return new String(b, StandardCharsets.UTF_8);
            }

        }
        byte[] b = new byte[offsets.get(curValue)];
        inStream.read(b, 0, offsets.get(curValue));
        curPos += offsets.get(curValue);
        return new String(b, StandardCharsets.UTF_8);
    }

    public void putValueToTable(ValueType value) {
        dataTable.put(keysToMap.get(curValue), value);
        if (curValue == offsets.size()) {
            dataTable.commitWithoutWriteToDataBase();
        }
        ++curValue;
    }

    public void closeResources() throws IOException {
        if (curPos > 0) {
            inStream.close();
        }
    }
}
