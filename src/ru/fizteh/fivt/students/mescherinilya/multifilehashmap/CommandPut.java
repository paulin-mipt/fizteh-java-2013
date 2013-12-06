package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

public class CommandPut implements Command {

    @Override
    public int getArgsCount() {
        return 2;
    }

    @Override
    public void execute(String[] args) {
        if (MultiFileHashMap.currentTable == null) {
            System.out.println("no table");
            return;
        }
        String oldValue = MultiFileHashMap.currentTable.put(args[0], args[1]);
        if (oldValue != null) {
            System.out.println("overwrite\n" + oldValue);
        } else {
            System.out.println("new");
        }

    }

    @Override
    public String getName() {
        return "put";
    }
}
