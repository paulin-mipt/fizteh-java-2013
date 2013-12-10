package ru.fizteh.fivt.students.vlmazlov.utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.Closeable;

import ru.fizteh.fivt.students.vlmazlov.utils.QuietCloser;

public class StoreableTableFileReader implements Closeable {

    private int initialOffset;
    private int readPosition;
    private String currentKey;
    private String currentValue;
    private final RandomAccessFile file;

    private String readUTFString(int readingPosition, int length) 
    throws IOException {
        byte[] bytes = new byte[length];

        file.seek(readingPosition);
        file.read(bytes);
        return new String(bytes, "UTF-8");
    }

    private int getNextOffset() throws IOException {

        int currentReadPosition = (int) file.getFilePointer();

        while (file.getFilePointer() < file.length()) {
            if (file.readByte() == '\0') {
                break;
            }
        }

        try {
            if (file.getFilePointer() == file.length()) {
                return (int) file.length();
            }

            return (int) file.readInt();
        } finally {
            file.seek(currentReadPosition);
        }
    }

    public StoreableTableFileReader(File file) throws IOException {
        this.file = new RandomAccessFile(file, "r");
        readPosition = 0;
        initialOffset = -1;
        currentKey = null;
        currentValue = null;
    }

    public String nextKey() throws IOException, ValidityCheckFailedException {

        if (((initialOffset != -1) && (initialOffset <= readPosition)) ||
        (file.length() == 0)) {
            currentKey = null;
            currentValue = null;

            return null;
        }

        //System.out.println(file.length());

        file.seek(readPosition);

        while (file.getFilePointer() < file.length()) {
            if (file.readByte() == '\0') {
                break;
            }
        }

        int keyLength = (int) file.getFilePointer() - readPosition - 1;

        int currentOffset = (int) file.readInt();

        ValidityChecker.checkTableOffset(currentOffset);

        if (initialOffset == -1) {
            initialOffset = currentOffset;
        }
        
        currentValue = readUTFString(currentOffset, getNextOffset() - currentOffset);
            
        ValidityChecker.checkTableValue(currentValue);
            
        currentKey = readUTFString(readPosition, keyLength);
        ValidityChecker.checkTableKey(currentKey);

        readPosition = (int) file.getFilePointer() + 5;

        return currentKey;
    }

    public String getCurrentSerializedValue() {
        return currentValue;
    }

    public void close() {
        QuietCloser.closeQuietly(file);
    }
}
