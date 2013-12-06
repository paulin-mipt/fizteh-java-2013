package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

public class CommandRemove implements Command {

    @Override
    public int getArgsCount() {
        return 1;
    }

    @Override
    public void execute(String[] args) {
        if (MultiFileHashMap.currentTable == null) {
            System.out.println("no table");
            return;
        }

        if (MultiFileHashMap.currentTable.get(args[0]) != null) {
            MultiFileHashMap.currentTable.remove(args[0]);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }

    }

    @Override
    public String getName() {
        return "remove";
    }
}
