package ru.fizteh.fivt.students.vlmazlov.utils;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.Closeable;

import ru.fizteh.fivt.students.vlmazlov.utils.QuietCloser;

public class StoreableTableFileReader {

    private Map.Entry<String, String> curEntry;
    private final Map<String, String> data;
    private final Iterator<Map.Entry<String, String>> iterator;

    private String readUTFString(RandomAccessFile dataBaseStorage, int readingPosition, int length) 
    throws IOException {
        byte[] bytes = new byte[length];

        dataBaseStorage.seek(readingPosition);
        dataBaseStorage.read(bytes);
        return new String(bytes, "UTF-8");
    }

    public StoreableTableFileReader(File file) throws IOException, ValidityCheckFailedException {
        data = new HashMap<String, String>();
        loadFile(file);
        iterator = data.entrySet().iterator();
    }

    private void loadFile(File file) throws IOException, ValidityCheckFailedException {
        
        if ((!file.exists()) || (file.length() == 0)) {
            return;
        }


        RandomAccessFile dataBaseStorage = new RandomAccessFile(file, "r");

        try {
            String key = null;
            int readPosition = 0;
            int initialOffset = -1;
            int prevOffset = -1;

            do {

                dataBaseStorage.seek(readPosition);

                while (dataBaseStorage.getFilePointer() < dataBaseStorage.length()) {
                    if (dataBaseStorage.readByte() == '\0') {
                        break;
                    }
                }

                int keyLen = (int) dataBaseStorage.getFilePointer() - readPosition - 1;

                int curOffset = (int) dataBaseStorage.readInt();

                ValidityChecker.checkTableOffset(curOffset);

                if (prevOffset == -1) {
                    initialOffset = curOffset;
                } else {
                    String value = readUTFString(dataBaseStorage, prevOffset, curOffset - prevOffset);

                    ValidityChecker.checkTableValue(value);

                    
                    data.put(key, value);
                }
                prevOffset = curOffset;
                //read key      
                key = readUTFString(dataBaseStorage, readPosition, keyLen);
                ValidityChecker.checkTableKey(key);

                readPosition = (int) dataBaseStorage.getFilePointer() + 5;

            } while (readPosition < initialOffset);

            String value = readUTFString(dataBaseStorage, prevOffset, (int) dataBaseStorage.length() - prevOffset);

            ValidityChecker.checkTableValue(value);

            
            data.put(key, value);
        } finally {
            QuietCloser.closeQuietly(dataBaseStorage);
        }
    }

    public String nextKey() throws IOException, ValidityCheckFailedException {
        if (iterator.hasNext()) {
           curEntry = iterator.next();
           return curEntry.getKey();
        } else {
            return null;
        }
    }

    public String getCurrentSerializedValue() {
        return curEntry.getValue();
    }
}
