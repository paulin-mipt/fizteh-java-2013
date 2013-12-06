package ru.fizteh.fivt.students.abramova.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChangeDirectoryCommand  extends Command{

    public ChangeDirectoryCommand(String name) {
        super(name);
    }

    @Override
    public int doCommand(String[] args, Status status) throws IOException {
        Stage stage = status.getStage();
        if (stage == null) {
            throw new IllegalStateException(getName() + ": Command do not get stage");
        }
        String argsStr;
        if (args.length != 1) {
            StringBuilder argsCollector = new StringBuilder();
            for (String str : args) {
                argsCollector.append(str).append(" ");
            }
            argsStr = argsCollector.deleteCharAt(argsCollector.length() - 1).toString();
        } else {
            argsStr = args[0];
        }
        if (argsStr.equals("..")) {
            setParent(stage);
        } else {
            if (argsStr.equals(".")) {
                setRoot(stage);
            } else {
                if (argsStr.charAt(0) != '.') {
                    File thisFile = new File(argsStr).getCanonicalFile();
                    if (thisFile.exists() && thisFile.isDirectory()) {
                        stage.setStage(thisFile.getAbsolutePath());
                    } else {
                        System.out.println(getName() + ": No such directory");
                        return 2;
                    }
                } else {
                    String str = argsStr.substring(2);
                    File thisFile = new File(stage.currentDirPath(), str).getCanonicalFile();
                    if (thisFile.exists() && thisFile.isDirectory()) {
                        stage.setStage(thisFile.getAbsolutePath());
                    } else {
                        System.out.println(getName() + ": No such directory");
                        return 2;
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public boolean correctArgs (String[] args) {
        return args != null;
    }

    private void setParent(Stage stage) {
        Path path = Paths.get(stage.currentDirPath()).getParent();
        if (path != null) {
            stage.setStage(path.toAbsolutePath().toString());
        }
    }
    private void setRoot(Stage stage) {
        Path path = Paths.get(stage.currentDirPath()).getRoot();
        if (path != null) {
            stage.setStage(path.toAbsolutePath().toString());
        }
    }
}
