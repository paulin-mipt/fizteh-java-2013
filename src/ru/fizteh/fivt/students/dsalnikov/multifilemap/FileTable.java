package ru.fizteh.fivt.students.dsalnikov.multifilemap;


import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.dsalnikov.filemap.FileMap;
import ru.fizteh.fivt.students.dsalnikov.filemap.FileMapState;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class FileTable {
    private String state;
    int size = 0;

    public HashMap<String, HashMap<String, FileMap>> directories = new HashMap<String, HashMap<String, FileMap>>();

    public FileTable() {
    }

    public FileTable(String directory) throws IOException {
        state = directory;
        File f = new File(state);
        String[] dirs = f.list();
        if (dirs != null) {
            for (String s : dirs) {
                if (s != null) {
                    File file = new File(f.getAbsolutePath(), s);
                    String[] subdirs = file.list();
                    HashMap<String, FileMap> temp = new HashMap<String, FileMap>();
                    for (String sub : subdirs) {
                        if (sub != null) {
                            File newfile = new File(file.getAbsolutePath(), sub);
                            FileMapState fms = new FileMapState(newfile.getAbsolutePath());
                            FileMap fm = new FileMap(fms);
                            temp.put(newfile.getName(), fm);
                        }
                    }
                    directories.put(file.getName(), temp);
                }
            }
        }
    }

    public void flush() throws IOException {
        for (String s : directories.keySet()) {
            File f = new File(state, s);
            if (!f.exists()) {
                f.mkdir();
            }
        }
        for (HashMap<String, FileMap> s : directories.values()) {
            for (FileMap fi : s.values()) {
                fi.deletefile();
                fi.deleteiIfEmpty();
            }
        }
        for (String s : directories.keySet()) {
            File f = new File(state, s);
            if (f.isDirectory() && f.list().length == 0) {
                f.delete();
            }
        }
    }

    public void insert(String ndirectory, String nfile, FileMap toinsert) throws IOException {
        HashMap<String, FileMap> hash = directories.get(ndirectory);
        if (hash == null) {
            HashMap<String, FileMap> tempmap = new HashMap<String, FileMap>();
            tempmap.put(nfile, toinsert);
            directories.put(ndirectory, tempmap);
        } else {
            FileMap filemap = hash.get(nfile);
            if (filemap == null) {
                hash.put(nfile, toinsert);
                directories.put(ndirectory, hash);
            }
        }
    }

    public FileMap getFileMap(String ndir, String nfile) {
        HashMap<String, FileMap> fmh = directories.get(ndir);
        if (fmh == null) {
            return null;
        } else {
            FileMap fm = fmh.get(nfile);
            return fm;
        }
    }

    public String getState() {
        return state;
    }

    public String getFileMapDirectory(String s) {
        return String.valueOf(Math.abs(s.hashCode()) % 16) + ".dir";
    }

    public String getFileMapFile(String s) {
        return String.valueOf(Math.abs(s.hashCode()) / 16 % 16) + ".dat";
    }
}
