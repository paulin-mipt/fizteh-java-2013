package ru.fizteh.fivt.students.elenarykunova.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.students.elenarykunova.shell.Shell;
import ru.fizteh.fivt.storage.structured.Storeable;

public class DataBase {

    private HashMap<String, Storeable> data = new HashMap<String, Storeable>();
    private String filePath = null;
    private String tablePath = null;
    private int ndir;
    private int nfile;
    private MyTable table = null;
    private boolean exists = false;
    public boolean hasChanged = false;

    public boolean hasFile() {
        return exists;
    }

    public int getSize() {
        return data.size();
    }
    
    public void loadDataToMap(HashMap<String, Storeable> map)
            throws IllegalArgumentException, ParseException {
        Set<Map.Entry<String, Storeable>> mySet = data.entrySet();
        for (Map.Entry<String, Storeable> myEntry : mySet) {
            map.put(myEntry.getKey(), myEntry.getValue());       
        }
    }

    public String getFileName(int ndir, int nfile) {
        return ndir + ".dir" + File.separator + nfile + ".dat";
    }

    public void createFile() throws RuntimeException {
        if (filePath != null) {
            File tmpFile = new File(filePath);
            if (!tmpFile.exists()) {
                if (!tmpFile.getParentFile().exists()) {
                    if (!tmpFile.getParentFile().mkdir()) {
                        throw new RuntimeException(filePath
                                + ": can't create file");
                    }
                }
                try {
                    if (!tmpFile.createNewFile()) {
                        throw new RuntimeException(filePath
                                + ": can't create file");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(filePath + ": can't create file");
                }
            }
            exists = true;
        }
    }

    public DataBase(MyTable myTable, int numbDir, int numbFile) throws RuntimeException, ParseException {
        table = myTable;
        tablePath = table.getTablePath();
        ndir = numbDir;
        nfile = numbFile;
        filePath = tablePath + File.separator + getFileName(ndir, nfile);
        File tmpFile = new File(filePath);
        RandomAccessFile dataFile = null;
        
        if (tmpFile.exists()) {
            try {
                dataFile = new RandomAccessFile(filePath, "r");
                exists = true;
                load(dataFile);
            } catch (FileNotFoundException e1) {
                throw new RuntimeException(filePath + ": can't find file", e1);
            } catch (IOException e3) {
                throw new RuntimeException(
                        filePath + ": error in loading file", e3);
            } finally {
                try {
                    closeDataFile(dataFile);
                } catch (Throwable e5) {
                    //
                }
            }
        }
    }

    public void checkOffset(long offset, long currPtr, RandomAccessFile dataFile) throws IOException {
        if (offset < currPtr || offset > dataFile.length()) {
            IOException e = new IOException();
            throw e;
        }
    }

    public boolean isCorrectPlace(String key) {
        int hashcode = Math.abs(key.hashCode());
        int currNumbDir = hashcode % 16;
        int currNumbFile = hashcode / 16 % 16;
        return (currNumbDir == ndir && currNumbFile == nfile);
    }

    public String getKeyFromFile(RandomAccessFile dataFile) throws IOException {
        byte ch = 0;
        ArrayList<Byte> v = new ArrayList<Byte>();
        ch = dataFile.readByte();
        while (ch != 0) {
            v.add(ch);
            ch = dataFile.readByte();
        }
        byte[] res = new byte[v.size()];
        for (int i = 0; i < v.size(); i++) {
            res[i] = v.get(i).byteValue();
        }
        String result = new String(res, StandardCharsets.UTF_8);
        if (!isCorrectPlace(result)) {
            IOException e = new IOException();
            throw e;
        }
        return result;
    }

    public String getValueFromFile(long nextOffset, RandomAccessFile dataFile) throws IOException {
        int beginPtr = (int) dataFile.getFilePointer();
        byte[] res = new byte[(int) (nextOffset - beginPtr)];
        dataFile.read(res);
        String result = new String(res, StandardCharsets.UTF_8);
        return result;
    }

    public void load(RandomAccessFile dataFile)
            throws RuntimeException, IOException, ParseException {
        try {
            if (dataFile.length() == 0) {
                return;
            }

            long currPtr = 0;
            long firstOffset = 0;
            long newOffset = 0;
            long nextOffset = 0;
            String keyFirst = "";
            String keySecond = "";
            String value;

            dataFile.seek(currPtr);
            keyFirst = getKeyFromFile(dataFile);

            newOffset = dataFile.readInt();
            currPtr = dataFile.getFilePointer();
            checkOffset(newOffset, currPtr, dataFile);
            firstOffset = newOffset;
            do {
                dataFile.seek(currPtr);
                if (currPtr < firstOffset) {
                    keySecond = getKeyFromFile(dataFile);
                    nextOffset = dataFile.readInt();
                    currPtr = dataFile.getFilePointer();
                    checkOffset(nextOffset, currPtr, dataFile);
                } else if (currPtr == firstOffset) {
                    nextOffset = dataFile.length();
                    currPtr++;
                }
                if (nextOffset < newOffset) {
                    IOException e1 = new IOException();
                    throw e1;
                }

                dataFile.seek(newOffset);
                value = getValueFromFile(nextOffset, dataFile);

                MyStoreable val;
                try {
                    val = (MyStoreable) table.getProvider().deserialize(table,
                            value);
                } catch (ParseException e) {
                    throw new ParseException(filePath
                            + " can't deserialize values from file "
                            + e.getMessage(), e.getErrorOffset());
                }
                data.put(keyFirst, val);
//                table.getHashMap().put(keyFirst, val);*/

                keyFirst = keySecond;
                newOffset = nextOffset;
            } while (currPtr <= firstOffset);
            hasChanged = true;
        } catch (IOException e1) {
            throw new IOException(filePath + " can't read values from file", e1);
        } catch (OutOfMemoryError e2) {
            throw new RuntimeException(filePath
                    + " can't read values from file: out of memory", e2);
        }
    }

    public Storeable put(String key, Storeable value) {
        return data.put(key, value);
    }

    public Storeable get(String key) {
        return data.get(key);
    }

    public Storeable remove(String key) {
        return data.remove(key);
    }

    private int getLength(String str) {
        
        int curr = 0;

        curr = str.getBytes(StandardCharsets.UTF_8).length;
        return curr;
    }

    protected void closeDataFile(RandomAccessFile dataFile) throws RuntimeException {
        try {
            if (dataFile != null) {
                dataFile.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(filePath + " can't close file", e);
        }
    }

    public void commitChanges() throws IOException, RuntimeException {
        Throwable t = null;
        RandomAccessFile dataFile = null;
        try {
            try {
                dataFile = new RandomAccessFile(filePath, "rw");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(filePath
                        + " can't get access to file", e);
            }
            if (data == null || data.isEmpty()) {
                closeDataFile(dataFile);
                Shell sh = new Shell(tablePath, false);
                if (sh.rm(filePath) != Shell.ExitCode.OK) {
                    throw new RuntimeException(filePath + " can't delete file");
                }
                exists = false;
                return;
            }

            int offset = 0;
            Set<Map.Entry<String, Storeable>> mySet = data.entrySet();
            for (Map.Entry<String, Storeable> myEntry : mySet) {
                offset += getLength(myEntry.getKey()) + 1 + 4;
            }
            int currOffset = offset;
            dataFile.setLength(0);
            dataFile.seek(0);
            for (Map.Entry<String, Storeable> myEntry : mySet) {
                dataFile.write(myEntry.getKey().getBytes());
                dataFile.writeByte(0);
                dataFile.writeInt(currOffset);
                currOffset += getLength(table.getProvider().serialize(table, myEntry.getValue()));
            }
            for (Map.Entry<String, Storeable> myEntry : mySet) {
                dataFile.write(table.getProvider().serialize(table, myEntry.getValue()).getBytes());
            }
        } catch (RuntimeException e5) {
            t = e5;
            throw e5;
        } finally {
            try {
                closeDataFile(dataFile);
            } catch (Throwable e6) {
                if (t != null) {
                    t.addSuppressed(e6);
                }
            }
        }
    }
}
