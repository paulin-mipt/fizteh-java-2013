package ru.fizteh.fivt.students.anastasyev.shell;

public class DirCommand implements Command<Shell> {
    private static void dir() {
        String[] fileList = Shell.getUserDir().list();
        for (String files : fileList) {
            System.out.println(files);
        }
    }

    @Override
    public final boolean exec(Shell state, final String[] command) {
        if (command.length != 1) {
            System.err.println("dir: Usage - dir");
            return false;
        }
        try {
            dir();
        } catch (Exception e) {
            System.err.println("dir: wrong path");
            return false;
        }
        return true;
    }

    @Override
    public final String commandName() {
        return "dir";
    }
}
