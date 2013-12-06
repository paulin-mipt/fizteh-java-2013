package ru.fizteh.fivt.students.drozdowsky.database;

import ru.fizteh.fivt.students.drozdowsky.utils.Pair;

import java.awt.geom.IllegalPathStateException;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class FileMap {
    private HashMap<String, String> db;

    static final int BUFFSIZE = 100000;

    public FileMap() {
        db = new HashMap<>();
    }

    private FileMap(HashMap<String, String> db) {
        this.db = (HashMap<String, String>) db.clone();
    }

    public String get(String key) {
        return db.get(key);
    }

    public String put(String key, String value) {
        String result = db.get(key);
        db.put(key, value);
        return result;
    }

    public String remove(String key) {
        String result = db.get(key);
        db.remove(key);
        return result;
    }

    public int size() {
        return db.size();
    }

    public Set<String> getKeys() {
        return db.keySet();
    }

    void read(File dbPath) {
        try (FileInputStream inputDB = new FileInputStream(dbPath)) {
            ArrayList<Pair<String, Integer>> offset = new ArrayList<>();

            byte next;
            int byteRead = 0;
            ByteBuffer key = ByteBuffer.allocate(BUFFSIZE);

            while ((next = (byte) inputDB.read()) != -1) {
                if (next == 0) {
                    byte[] sizeBuf = new byte[4];
                    for (int i = 0; i < 4; i++) {
                        if ((sizeBuf[i] = (byte) inputDB.read()) == -1) {
                            throw new IOException(dbPath.getPath() + ": unexpected end of the database");
                        }
                    }
                    int valueSize = ByteBuffer.wrap(sizeBuf).getInt();

                    byte[] keyArray = new byte[key.position()];
                    byteRead += key.position() + 4 + 1;
                    key.clear();
                    key.get(keyArray);
                    offset.add(new Pair<>(new String(keyArray, "UTF-8"), valueSize));
                    key.clear();

                } else {
                    key.put(next);
                }
            }

            if (offset.size() == 0 && key.position() != 0) {
                throw new IOException("No keys found");
            }

            if (offset.size() == 0) {
                return;
            }

            if (offset.get(0).snd != byteRead) {
                throw new IOException("No valid database format");
            }

            for (int i = 0; i < offset.size() - 1; i++) {
                offset.set(i, new Pair<>(offset.get(i).fst, offset.get(i + 1).snd - byteRead));
            }
            int n = offset.size();
            offset.set(n - 1, new Pair<>(offset.get(n - 1).fst, key.position()));

            int prevOffset = 0;
            for (Pair<String, Integer> now: offset) {
                Integer currentOffset = now.snd;
                if (currentOffset <= prevOffset) {
                    throw new IOException("Not valid format");
                } else {
                    ByteBuffer valueBuf = ByteBuffer.allocate(currentOffset - prevOffset);
                    for (int i = prevOffset; i < currentOffset; i++) {
                        valueBuf.put(key.get(i));
                    }
                    prevOffset = currentOffset;
                    String value = new String(valueBuf.array(), "UTF-8");
                    db.put(now.fst, value);
                }
            }
        } catch (IOException e) {
            throw new IllegalPathStateException(dbPath.getAbsolutePath() + e.getMessage());
        }
    }

    void write(File dbPath) {
        try (FileOutputStream out = new FileOutputStream(dbPath)) {
            ArrayList<Integer> length = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            ArrayList<String> keys = new ArrayList<>();
            int totalLength = 0;
            for (String key:db.keySet()) {
                keys.add(key);
                values.add(db.get(key));
                totalLength += key.getBytes("UTF-8").length;
                length.add(db.get(key).getBytes("UTF-8").length);
                totalLength += 4;
                totalLength += "\0".getBytes("UTF-8").length;
            }
            for (int i = 0; i < keys.size(); i++) {
                out.write(keys.get(i).getBytes("UTF-8"));
                out.write("\0".getBytes("UTF-8"));
                out.write(ByteBuffer.allocate(4).putInt(totalLength).array());
                totalLength += length.get(i);
            }
            for (int i = 0; i < keys.size(); i++) {
                out.write(values.get(i).getBytes("UTF-8"));
            }
        } catch (IOException e) {
            throw new IllegalPathStateException(dbPath.getAbsolutePath() + e.getMessage());
        }
    }

    protected FileMap clone() {
        return new FileMap(db);
    }
}
