package ru.fizteh.fivt.students.adanilyak.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.adanilyak.commands.CmdExit;
import ru.fizteh.fivt.students.adanilyak.commands.CmdGet;
import ru.fizteh.fivt.students.adanilyak.commands.CmdPut;
import ru.fizteh.fivt.students.adanilyak.commands.CmdRemove;
import ru.fizteh.fivt.students.adanilyak.tools.ShellLogic;
import ru.fizteh.fivt.students.adanilyak.userinterface.GenericCmdList;
import ru.fizteh.fivt.students.adanilyak.userinterface.GenericShell;

import java.io.File;
import java.io.IOException;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 11:21
 */
public class FileMapShell extends GenericShell {
    private final Integer parserAndExecutor = 1;

    public FileMapShell(String[] args, String datFileName) {
        File datFile = new File(System.getProperty("fizteh.db.dir"), datFileName);
        try {
            Table currentTable = new FileMapTable(datFile);
            FileMapGlobalState state = new FileMapGlobalState(currentTable);
            runFileMapShell(args, makeUpCmdList(state));
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
            System.exit(1);
        }
    }

    private void runFileMapShell(String[] args, GenericCmdList cmdList) {
        if (args.length == 0) {
            ShellLogic.interactiveMode(System.in, cmdList.getCmdList(), System.out, System.err, parserAndExecutor);
        } else {
            ShellLogic.packageMode(args, cmdList.getCmdList(), System.out, System.err, parserAndExecutor);
        }
    }

    private GenericCmdList makeUpCmdList(FileMapGlobalState state) {
        GenericCmdList stockFileMapShellCmdList = new GenericCmdList();
        stockFileMapShellCmdList.addCommand(new CmdPut(state));
        stockFileMapShellCmdList.addCommand(new CmdGet(state));
        stockFileMapShellCmdList.addCommand(new CmdRemove(state));
        stockFileMapShellCmdList.addCommand(new CmdExit(state));
        return stockFileMapShellCmdList;
    }
}
