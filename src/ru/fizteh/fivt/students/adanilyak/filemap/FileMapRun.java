package ru.fizteh.fivt.students.adanilyak.filemap;

import ru.fizteh.fivt.students.adanilyak.userinterface.GenericShell;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 1:02
 */
public class FileMapRun {
    public static void main(String[] args) {
        GenericShell myShell = new FileMapShell(args, "db.dat");
    }
}
