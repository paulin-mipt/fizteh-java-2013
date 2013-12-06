package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

public class CommandDrop implements Command {

    @Override
    public String getName() {
        return "drop";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }

    @Override
    public void execute(String[] args) throws Exception {

        if (MultiFileHashMap.provider.getTable(args[0]) == null) {
            System.out.println(args[0] + " not exists");
            return;
        }


        MultiFileHashMap.provider.removeTable(args[0]);

        if (MultiFileHashMap.currentTable.getName().equals(args[0])) {
            MultiFileHashMap.currentTable = null;
        }

        System.out.println("dropped");

    }
}
