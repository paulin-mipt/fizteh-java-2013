package ru.fizteh.fivt.students.asaitgalin.filemap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Map;

public class TableEntryReader {
    private RandomAccessFile fileStream;
    private boolean isStreamValid;
    private long firstValueOffset = -1;

    private String nextKey;
    private long nextValueOffset;

    public TableEntryReader(File input) throws IOException {
        isStreamValid = true;
        try {
            fileStream = new RandomAccessFile(input, "r");
            if (fileStream.length() == 0) {
                throw new IOException("empty file");
            }
        } catch (FileNotFoundException fnfe) {
            isStreamValid = false;
        }
        if (isStreamValid) {
            readNext();
        }
    }

    public boolean hasNextEntry() throws IOException {
        return isStreamValid && (fileStream.getFilePointer() <= firstValueOffset);
    }

    private void readNext() throws IOException {
        ByteArrayOutputStream keyBuffer = new ByteArrayOutputStream();
        boolean partialRead = false;
        try {
            byte readByte = fileStream.readByte();
            if (readByte == 0) {             // no key in input
                throw new IOException("Corrupted input file");
            }
            while (readByte != 0) {
                keyBuffer.write(readByte);
                readByte = fileStream.readByte();
            }
            partialRead = true;
            nextKey = keyBuffer.toString(StandardCharsets.UTF_8.toString());
            nextValueOffset = fileStream.readInt();

            if (nextValueOffset >= fileStream.length() || nextValueOffset < 0) {
                throw new IOException("Corrupted input file");
            }

            if (firstValueOffset == -1) {
                firstValueOffset = nextValueOffset;
            }
            partialRead = false;
        } catch (EOFException eofe) {
            if (partialRead) {
                throw new IOException("Corrupted input file");
            }
            isStreamValid = false;
        }
    }

    public Map.Entry<String, String> readNextEntry() throws IOException {
        try {
            long offset = nextValueOffset;
            String key = nextKey;
            readNext();
            long savedPos = fileStream.getFilePointer();
            fileStream.seek(offset);
            byte[] valueData;
            if (key != null && !isStreamValid) {
                valueData = new byte[(int) (fileStream.length() - offset)];
            } else {
                valueData = new byte[(int) (nextValueOffset - offset)];
            }
            fileStream.read(valueData);
            String value = new String(valueData, StandardCharsets.UTF_8.toString());
            fileStream.seek(savedPos);
            return new AbstractMap.SimpleEntry<>(key, value);
        } catch (EOFException eofe) {
            isStreamValid = false;
        }
        return null;
    }

}
