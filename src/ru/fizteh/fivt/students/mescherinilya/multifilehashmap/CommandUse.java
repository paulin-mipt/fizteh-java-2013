package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

public class CommandUse implements Command {

    @Override
    public String getName() {
        return "use";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }

    @Override
    public void execute(String[] args) throws Exception {

        if (MultiFileHashMap.currentTable != null
                && MultiFileHashMap.currentTable.changesCount() != 0) {
            System.out.println(MultiFileHashMap.currentTable.changesCount() + " uncommitted changes");
            return;
        }

        if (MultiFileHashMap.provider.getTable(args[0]) == null) {
            System.out.println(args[0] + " not exists");
            return;
        }

        MultiFileHashMap.currentTable = MultiFileHashMap.provider.getTable(args[0]);
        System.out.println("using " + args[0]);
    }
}
