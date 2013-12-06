package ru.fizteh.fivt.students.piakovenko.filemap;

import ru.fizteh.fivt.students.piakovenko.filemap.strings.DataBasesFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 19.10.13
 * Time: 20:03
 * To change this template use File | Settings | File Templates.
 */

public class Start {
    public static int main(String[] args) {
        try {
            DataBasesFactory dbf = new DataBasesFactory();
            dbf.create(System.getProperty("fizteh.db.dir"));
            dbf.start(args);
        } catch (IllegalArgumentException e) {
            System.err.println("Error! " + e.getMessage());
            System.exit(1);
        }
        return 0;
    }
}
