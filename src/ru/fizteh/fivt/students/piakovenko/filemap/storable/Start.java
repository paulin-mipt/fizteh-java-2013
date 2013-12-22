package ru.fizteh.fivt.students.piakovenko.filemap.storable;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 19.10.13
 * Time: 20:03
 * To change this template use File | Settings | File Templates.
 */

public class Start {
    public static void  main(String[] args) {
        try {
            DataBasesFactory dbf = new DataBasesFactory();
            try {
                dbf.create(System.getProperty("fizteh.db.dir"));
                dbf.start(args);
            } catch (IllegalArgumentException e) {
                System.err.println("Error! " + e.getMessage());
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Error! " + e.getMessage());
                System.exit(1);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error! " + e.getMessage());
            System.exit(1);
        }
    }
}
