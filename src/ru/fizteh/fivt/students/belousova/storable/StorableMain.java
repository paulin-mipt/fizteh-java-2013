package ru.fizteh.fivt.students.belousova.storable;

import java.io.IOException;

public class StorableMain {
    public static void main(String[] args) {
        StorableShell shell = new StorableShell();
        String dir = System.getProperty("fizteh.db.dir");
        StorableTableProviderFactory tableProviderFactory = new StorableTableProviderFactory();
        try {
            StorableState storableState = new StorableState(tableProviderFactory.create(dir), null);
            shell.run(args, storableState);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
