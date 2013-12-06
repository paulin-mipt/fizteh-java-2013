package ru.fizteh.fivt.students.surakshina.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class CommandCp extends AbstractCommand {
    public CommandCp(State stateNew) {
        super(stateNew);
        name = "cp";
        numberOfArguments = 2;
    }

    private boolean isRoot(File currentFile) {
        for (int i = 0; i < File.listRoots().length; ++i) {
            if (currentFile.equals(File.listRoots()[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIsRoot(File currentFile, File file) {
        File tmp = new File(file.getParent());
        while (!isRoot(tmp)) {
            tmp = new File(tmp.getParent());
            if (tmp.equals(currentFile)) {
                return true;
            }
        }
        return false;
    }

    private File unionWithCurrentPath(String curr) {
        File curr1 = new File(curr);
        if (!curr1.isAbsolute()) {
            curr1 = new File(state.getCurrentDirectory(), curr);
            try {
                curr1 = curr1.getCanonicalFile();
            } catch (IOException exception) {
                state.printError(curr1.toString());
            }
        }
        return curr1;
    }

    @Override
    public void executeProcess(String[] input) {
        String source = input[1];
        String destination = input[2];
        if (input[2].contains(" ")) {
            state.printError("Incorrect number of arguments");
        } else {
            File currentFile = unionWithCurrentPath(source);
            File file = unionWithCurrentPath(destination);
            File destinationFile = unionWithCurrentPath(destination + File.separator + source);
            if (!currentFile.exists()) {
                state.printError("cp: cannot copy: '" + source + "': No such file or Directory");
            } else {
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException exception) {
                        state.printError("cp: cannot create a file '" + source + "'");
                    }
                    file = unionWithCurrentPath(file.toString());
                }
                try {
                    if (checkIsRoot(currentFile, destinationFile)) {
                        state.printError("cp: cannot copy: '" + source + "': It is a root or a parent of destination");
                    } else {
                        if (currentFile.getCanonicalPath().equals(file.getCanonicalPath())) {
                            state.printError("cp: cannot copy: '" + source + "': It is the same");
                        } else {
                            if (currentFile.isFile() && file.isFile()) {
                                Files.copy(currentFile.toPath(), file.toPath(), StandardCopyOption.COPY_ATTRIBUTES,
                                        StandardCopyOption.REPLACE_EXISTING);
                            } else {
                                Files.copy(currentFile.toPath(), destinationFile.toPath(),
                                        StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    }
                } catch (IOException exception) {
                    state.printError("cp: cannot copy '" + source + "'");
                }
            }
        }
    }
}
