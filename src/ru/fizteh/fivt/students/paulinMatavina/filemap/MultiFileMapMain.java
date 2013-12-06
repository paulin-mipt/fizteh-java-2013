package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.CommandRunner;

public class MultiFileMapMain {
    public static void main(String[] args) {
        try {
            String property = System.getProperty("fizteh.db.dir");
            MyTableProvider state = new MyTableProvider(property);  
            CommandRunner.run(args, state);
        } catch (DbExitException e) {
            System.exit(Integer.parseInt(e.getMessage()));
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
