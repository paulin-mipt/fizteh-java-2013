package ru.fizteh.fivt.students.belousova.multifilehashmap;

public class MultiFileHashMap {
    public static void main(String[] args) {
        MultiFileShell shell = new MultiFileShell();
        String dir = System.getProperty("fizteh.db.dir");
        MultiFileTableProviderFactory tableProviderFactory = new MultiFileTableProviderFactory();
        try {
            MultiFileShellState multiFileShellState = new MultiFileShellState(tableProviderFactory.create(dir), null);
            shell.run(args, multiFileShellState);
        } catch (IllegalArgumentException e) {
            //System.err.println(e.getMessage());
            System.exit(1);
        }

    }
}
