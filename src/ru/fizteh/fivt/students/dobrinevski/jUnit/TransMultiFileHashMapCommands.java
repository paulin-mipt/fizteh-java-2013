package ru.fizteh.fivt.students.dobrinevski.jUnit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import ru.fizteh.fivt.students.dobrinevski.multiFileHashMap.MyMultiHashMap;
import ru.fizteh.fivt.students.dobrinevski.multiFileHashMap.MultiFileHashMapCommands;
import ru.fizteh.fivt.students.dobrinevski.multiFileHashMap.MultiFileHashMapCommand;
import ru.fizteh.fivt.students.dobrinevski.shell.Command;

public class TransMultiFileHashMapCommands {
    public static Integer numOfChanges(MyMultiHashMap hMap) throws Exception {
        Integer changes = 0;
        HashMap<Integer, HashMap<String, String>> newDataBase = new HashMap<Integer, HashMap<String, String>>();
        for (int i = 0; i < 256; i++) {
            newDataBase.put(i, new HashMap<String, String>(hMap.dataBase.get(i)));
            hMap.dataBase.get(i).clear();
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (hMap.check[i * 16 + j]) {
                    hMap.check[i * 16 + j] = false;
                    hMap.parseFile(new File(hMap.curTable, i + ".dir" + File.separator + j + ".dat"), i, j);
                    int nOldValues = hMap.dataBase.get(i * 16 + j).size();
                    for (Map.Entry<String, String> pair : newDataBase.get(i * 16 + j).entrySet()) {
                        String str = hMap.dataBase.get(i * 16 + j).get(pair.getKey());
                        if (str != null) {
                            nOldValues--;
                            if (!str.equals(pair.getValue())) {
                                changes++;
                            }
                        } else {
                            changes++;
                        }
                    }
                    changes += nOldValues;
                }
            }
        }
        hMap.dataBase = newDataBase;
        return changes;
    }

    public static class Size extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            Integer size = 0;
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    parent.parseFile(new File(parent.curTable, i + ".dir" + File.separator + j + ".dat"), i, j);
                    size += parent.dataBase.get(i * 16 + j).size();
                }
            }
            returnValue = new String[1];
            returnValue[0] = size.toString();
        }

        Size(MyMultiHashMap prnt, File root) {
            super(1, prnt, root);
        }
    }

    public static class Commit extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            returnValue = new String[1];
            returnValue[0] = numOfChanges(parent).toString();
            parent.writeOut();
        }

        Commit(MyMultiHashMap prnt, File root) {
            super(1, prnt, root);
        }
    }

    public static class RollBack extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            returnValue = new String[1];
            returnValue[0] = numOfChanges(parent).toString();
            for (int i = 0; i < 256; i++) {
                parent.dataBase.get(i).clear();
                parent.check[i] = false;
            }
        }

        RollBack(MyMultiHashMap prnt, File root) {
            super(1, prnt, root);
        }
    }

    public static class Use extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            int buf = numOfChanges(parent);
            if (buf != 0) {
                returnValue = new String[1];
                returnValue[0] = buf + " unsaved changes";
            } else {
                Command use = new MultiFileHashMapCommands.Use(parent, root);
                use.innerExecute(args);
                returnValue = use.returnValue;
            }
        }

        Use(MyMultiHashMap prnt, File root) {
            super(2, prnt, root);
        }
    }

    public static class Exit extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            System.exit(0);
        }

        Exit(MyMultiHashMap prnt, File root) {
            super(1, prnt, root);
        }
    }
}

