package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

public class CommandCommit implements Command {

    @Override
    public String getName() {
        return "commit";
    }

    @Override
    public int getArgsCount() {
        return 0;
    }

    @Override
    public void execute(String[] args) throws Exception {
        if (MultiFileHashMap.currentTable == null) {
            System.out.println("no table");
        }

        System.out.println(MultiFileHashMap.currentTable.commit());

    }
}
