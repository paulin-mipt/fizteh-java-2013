package ru.fizteh.fivt.students.baldindima.junit;


import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.baldindima.shell.ExitException;
import ru.fizteh.fivt.students.baldindima.shell.Shell;

public class Main {
    private static Shell shell;


    private static boolean checkDirectory() {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null){
        	System.err.println("Choose a directory!");
            return false;
        }
        new File(path).mkdirs();
        if ((!(new File(path)).isDirectory())) {
            System.err.println("Choose a directory!");
            return false;
        }
        return true;
    }

    private static void makeShell() throws IOException {

        shell = new Shell();

        TableProviderFactory factory = new MyTableProviderFactory();
        Context context = new Context(factory.create(System.getProperty("fizteh.db.dir")));

        shell.addCommand(new ShellDbPut(context));
        shell.addCommand(new ShellDbJUnitExit(context));
        shell.addCommand(new ShellDbGet(context));
        shell.addCommand(new ShellDbRemove(context));
        shell.addCommand(new ShellDbCreateTable(context));
        shell.addCommand(new ShellDbDropTable(context));
        shell.addCommand(new ShellDbUseTable(context));
        shell.addCommand(new ShellDbSize(context));
        shell.addCommand(new ShellDbCommit(context));
        shell.addCommand(new ShellDbRollback(context));
    }

    public static void main(String[] args) throws IOException {


        try {
            if (!checkDirectory()) {
            	System.exit(1); ;
            }
            makeShell();
           
                   
            if (args.length > 0) {
                shell.nonInteractiveMode(args);

            } else {
                shell.interactiveMode();
            }

        } catch (ExitException e) {
            //dataBaseTable.saveTable();
            System.exit(0);
        } catch (DataBaseException e){
        	System.err.println(e.getMessage());
            System.exit(2);
        }
          catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }


}
