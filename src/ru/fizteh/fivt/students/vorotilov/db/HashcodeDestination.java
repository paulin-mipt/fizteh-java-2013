package ru.fizteh.fivt.students.vorotilov.db;

public class HashcodeDestination {
    private int ndirectory;
    private int nfile;

    public HashcodeDestination(String key) {
        int hashcode = Math.abs(key.hashCode());
        ndirectory = hashcode % 16;
        nfile = hashcode / 16 % 16;
    }

    public int getDir() {
        return ndirectory;
    }

    public int getFile() {
        return nfile;
    }

}
