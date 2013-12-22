package ru.fizteh.fivt.students.baranov.shell;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        String str = System.getProperty("user.dir");
        Path path = Paths.get(str).toAbsolutePath();
        Shell shell = new Shell(path);

        shell.commands.put("cd", new Cd());
        shell.commands.put("exit", new Exit());
        shell.commands.put("mkdir", new Mkdir());
        shell.commands.put("pwd", new Pwd());
        shell.commands.put("rm", new Remove());
        shell.commands.put("cp", new Copy());
        shell.commands.put("dir", new Dir());
        shell.commands.put("mv", new Move());

        if (args.length == 0) {
            shell.interactiveMode();
        } else {
            shell.pocketMode(args);
        }
    }
}
