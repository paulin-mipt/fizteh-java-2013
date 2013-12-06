package ru.fizteh.fivt.students.drozdowsky.commands;

import ru.fizteh.fivt.students.drozdowsky.database.FileHashMap;
import ru.fizteh.fivt.students.drozdowsky.database.MultiFileHashMap;

import java.awt.geom.IllegalPathStateException;

public class MfhmController {

    FileHashMap currentdb;
    MultiFileHashMap multiFileHashMap;

    public MfhmController(MultiFileHashMap multiFileHashMap) {
        this.multiFileHashMap = multiFileHashMap;
        currentdb = null;
    }

    public boolean create(String name) {
        try {
            if (multiFileHashMap.createTable(name) != null) {
                System.out.println("created");
            } else {
                System.out.println(name + " exists");
            }
            return true;
        } catch (IllegalStateException | IllegalPathStateException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean drop(String name) {
        try {
            multiFileHashMap.removeTable(name);
            if (name.equals(currentdb.getName())) {
                currentdb = null;
            }
            System.out.println("dropped");
            return true;
        } catch (IllegalPathStateException e) {
            System.err.println(e.getMessage());
            return false;
        } catch (IllegalStateException e) {
            System.out.println(name + " not exists");
            return true;
        }
    }

    public boolean use(String name) {
        try {
            if (currentdb != null) {
                System.err.println(currentdb.difference() + " unsaved changes");
                return false;
            }
            if (multiFileHashMap.getTable(name) != null) {
                if (currentdb != null) {
                    multiFileHashMap.stopUsing(currentdb.getName());
                }
                currentdb = multiFileHashMap.getTable(name);
                System.out.println("using " + name);
            } else {
                System.out.println(name + " not exists");
            }
            return true;
        } catch (IllegalStateException | IllegalPathStateException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean size() {
        if (currentdb == null) {
            System.out.println("no table");
            return false;
        }
        System.out.println(currentdb.size());
        return true;
    }

    public boolean put(String key, String value) {
        if (currentdb == null) {
            System.out.println("no table");
            return false;
        }
        String result = currentdb.put(key, value);
        if (result != null) {
            System.out.println("overwrite " + result);
        } else {
            System.out.println("new");
        }
        return true;
    }

    public boolean get(String key) {
        if (currentdb == null) {
            System.out.println("no table");
            return false;
        }
        String result = currentdb.get(key);
        if (result != null) {
            System.out.println("found " + result);
        } else {
            System.out.println("not found");
        }
        return true;
    }

    public boolean remove(String key) {
        if (currentdb == null) {
            System.out.println("no table");
            return false;
        }
        String result = currentdb.remove(key);
        if (result != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return true;
    }

    public boolean exit() {
        if (currentdb != null) {
            currentdb.close();
        }
        System.exit(0);
        return true;
    }

    public boolean commit() {
        if (currentdb == null) {
            System.out.println("no table");
            return false;
        }
        System.out.println(currentdb.commit());
        return true;
    }

    public boolean rollback() {
        if (currentdb == null) {
            System.out.println("no table");
            return false;
        }
        System.out.println(currentdb.rollback());
        return true;
    }
}
