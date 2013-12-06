package ru.fizteh.fivt.students.abramova.shell;

import ru.fizteh.fivt.students.abramova.filemap.*;
import ru.fizteh.fivt.students.abramova.multifilehashmap.*;

public class CommandGetter {
    public static Command getCommand(String commandName, Status status) {
        if (status.isStage()) {
            return getCommandShell(commandName);
        }
        if (status.isFileMap()) {
            return getCommandFileMap(commandName);
        }
        if (status.isMultiFileMap()) {
            return getCommandMultiFileMap(commandName);
        }
        return null;
    }

    private static Command getCommandShell(String commandName) {
        if (commandName.equals("cd")) {
            return new ChangeDirectoryCommand("cd");
        }
        if (commandName.equals("rm")) {
            return new RemoveCommand("rm");
        }
        if (commandName.equals("cp")) {
            return new CopyToCommand("cp");
        }
        if (commandName.equals("mv")) {
            return new MoveToCommand("mv");
        }
        if (commandName.equals("dir")) {
            return new DirectoryCommand("dir");
        }
        if (commandName.equals("mkdir")) {
            return new MakeDirectoryCommand("mkdir");
        }
        if (commandName.equals("pwd")) {
            return new PrintWorkingDirectoryCommand("pwd");
        }
        if (commandName.equals("exit")) {
            return new ExitCommand("exit");
        }
        return null;
    }

    private static Command getCommandFileMap(String commandName) {
        if (commandName.equals("put")) {
            return new PutCommand("put");
        }
        if (commandName.equals("get")) {
            return new GetCommand("get");
        }
        if (commandName.equals("remove")) {
            return new RemoveFromMapCommand("remove");
        }
        if (commandName.equals("exit")) {
            return new ExitCommand("exit");
        }
        return null;
    }

    public static Command getCommandMultiFileMap(String commandName) {
        if (commandName.equals("put")) {
            return new PutMultiCommand("put");
        }
        if (commandName.equals("get")) {
            return new GetMultiCommand("get");
        }
        if (commandName.equals("remove")) {
            return new RemoveMultiCommand("remove");
        }
        if (commandName.equals("exit")) {
            return new ExitCommand("exit");
        }
        if (commandName.equals("create")) {
            return new CreateCommand("create");
        }
        if (commandName.equals("drop")) {
            return new DropCommand("drop");
        }
        if (commandName.equals("use")) {
            return new UseCommand("use");
        }
        return null;
    }
}
