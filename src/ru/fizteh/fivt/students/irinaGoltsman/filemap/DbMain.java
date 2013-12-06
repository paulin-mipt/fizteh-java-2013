package ru.fizteh.fivt.students.irinaGoltsman.filemap;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBTableProviderFactory;
import ru.fizteh.fivt.students.irinaGoltsman.shell.Code;
import ru.fizteh.fivt.students.irinaGoltsman.shell.MapOfCommands;
import ru.fizteh.fivt.students.irinaGoltsman.shell.Shell;
import ru.fizteh.fivt.students.irinaGoltsman.shell.ShellCommands;

public class DbMain {
    public static void main(String[] args) {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("Error with path to the root directory");
            System.exit(1);
        }
        TableProviderFactory newTableProviderFactory = new DBTableProviderFactory();
        TableProvider newTableProvider = null;
        try {
            newTableProvider = newTableProviderFactory.create(path);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        DataBase myDataBase = new DataBase(newTableProvider);
        MapOfCommands cm = new MapOfCommands();
        cm.addCommand(new ShellCommands.Exit());
        cm.addCommand(new DBCommands.Put());
        cm.addCommand(new DBCommands.Get());
        cm.addCommand(new DBCommands.Remove());
        cm.addCommand(new DBCommands.Commit());
        cm.addCommand(new DBCommands.CreateTable());
        cm.addCommand(new DBCommands.Drop());
        cm.addCommand(new DBCommands.Use());
        cm.addCommand(new DBCommands.Size());
        cm.addCommand(new DBCommands.RollBack());
        Code codeOfShell = Shell.shell(args);
        myDataBase.closeDB();
        if (codeOfShell == Code.SYSTEM_ERROR || codeOfShell == Code.ERROR) {
            System.exit(1);
        }
    }
}
