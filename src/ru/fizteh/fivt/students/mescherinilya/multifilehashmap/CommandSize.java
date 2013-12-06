package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

public class CommandSize implements Command {

    @Override
    public String getName() {
        return "size";
    }

    @Override
    public int getArgsCount() {
        return 0;
    }

    @Override
    public void execute(String[] args) throws Exception {
        if (MultiFileHashMap.currentTable == null) {
            System.out.println("no table");
            return;
        }

        System.out.println(MultiFileHashMap.currentTable.size());
        return;
    }
}
