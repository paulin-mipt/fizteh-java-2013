package ru.fizteh.fivt.students.belousova.shell;

import ru.fizteh.fivt.students.belousova.utils.ShellUtils;

import java.util.HashMap;
import java.util.Map;

public class MainShell {
    public static Map<String, Command> commandList = new HashMap<String, Command>();

    public static void main(String[] args) {
        ShellState state = new ShellState();
        makeCommandList(state);

        if (args.length == 0) {
            ShellUtils.interactiveMode(System.in, commandList);
        } else {
            ShellUtils.batchMode(args, commandList);
        }
    }

    private static void makeCommandList(ShellState state) {
        addCommand(new CommandCd(state));
        addCommand(new CommandPwd(state));
        addCommand(new CommandDir(state));
        addCommand(new CommandCp(state));
        addCommand(new CommandMkdir(state));
        addCommand(new CommandRm(state));
        addCommand(new CommandExit(state));
        addCommand(new CommandMv(state));
    }

    private static void addCommand(Command command) {
        commandList.put(command.getName(), command);
    }
}
