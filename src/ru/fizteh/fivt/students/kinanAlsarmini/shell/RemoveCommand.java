package ru.fizteh.fivt.students.kinanAlsarmini.shell;

import java.io.File;

class RemoveCommand extends ExternalCommand {
    public RemoveCommand() {
        super("rm", 1);
    }

    private void recursiveRemove(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();

            for (File f: files) {
                if (f.isDirectory())  {
                    recursiveRemove(f);
                } else {
                    if (!f.delete()) {
                        throw new IllegalArgumentException("rm: unable to delete file: " + f.getName() + ".");
                    }
                }
            }
        }

        if (!file.delete()) {
            throw new IllegalArgumentException("rm: unable to delete file: " + file.getName() + ".");
        }
    }

    public void execute(String[] args, Shell shell) {
        File target = Utilities.getAbsoluteFile(args[0], shell.getCurrentPath());

        if (!target.exists()) {
            throw new IllegalArgumentException("rm: target file / directory doesn't exist.");
        }

        recursiveRemove(target);
    }
}
