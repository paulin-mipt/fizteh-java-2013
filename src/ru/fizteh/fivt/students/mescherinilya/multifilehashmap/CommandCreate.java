package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

public class CommandCreate implements Command {

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }

    @Override
    public void execute(String[] args) throws Exception {

        Table newTable = MultiFileHashMap.provider.createTable(args[0]);

        if (newTable == null) {
            System.out.println(args[0] + " exists");
        } else {
            System.out.println("created");
        }

    }
}
