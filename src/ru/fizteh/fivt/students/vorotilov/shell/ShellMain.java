package ru.fizteh.fivt.students.vorotilov.shell;

import java.io.File;
import java.io.IOException;

public class ShellMain {

    private static File currentDirectory;
    private static boolean interactiveMode;

    static void processCommand(String[] parsedCommand) throws ExitCommand, IOException, WrongCommand {
        try {
            switch (parsedCommand[0]) {
                case "exit":
                    if (parsedCommand.length > 1) {
                        System.out.println("exit: must not get parameter");
                        throw new WrongCommand();
                    } else {
                        throw new ExitCommand();
                    }
                case "pwd":
                    if (parsedCommand.length > 1) {
                        System.out.println("pwd: must not get parameter");
                        throw new WrongCommand();
                    } else {
                        System.out.println(currentDirectory.getCanonicalPath());
                    }
                    break;
                case "mkdir":
                    if (parsedCommand.length != 2) {
                        System.out.println("mkdir: get 1 parameter");
                        throw new WrongCommand();
                    } else {
                        File newDir = FileUtil.convertPath(currentDirectory, parsedCommand[1]);
                        if (newDir.exists()) {
                            System.out.println("mkdir: can't create'"
                                    + newDir.getCanonicalPath() + "' directory is already exists");
                            throw new WrongCommand();
                        } else if (!newDir.mkdirs()) {
                            System.out.println("mkdir: can't create'" + newDir.getCanonicalPath() + "'");
                            throw new WrongCommand();
                        }
                    }
                    break;
                case "dir":
                    if (parsedCommand.length > 1) {
                        System.out.println("dir: must not get parameter");
                        throw new WrongCommand();
                    } else {
                        String[] listOfFiles = currentDirectory.list();
                        for (String i : listOfFiles) {
                            System.out.println(i);
                        }
                    }
                    break;
                case "cd":
                    if (parsedCommand.length != 2) {
                        System.out.println("cd: get 1 parameter");
                        throw new WrongCommand();
                    } else {
                        File newCurrentDirectory = FileUtil.convertPath(currentDirectory,
                                parsedCommand[1]).getCanonicalFile();
                        if (!newCurrentDirectory.exists()) {
                            System.out.println("cd: '" + newCurrentDirectory + "': No such file or directory");
                            throw new WrongCommand();
                        } else if (!newCurrentDirectory.isDirectory()) {
                            System.out.println("cd: '" + newCurrentDirectory.getCanonicalPath() + "' is not directory");
                            throw new WrongCommand();
                        } else {
                            currentDirectory = newCurrentDirectory;
                        }
                    }
                    break;
                case "rm":
                    if (parsedCommand.length != 2) {
                        System.out.println("rm: get 1 parameter");
                        throw new WrongCommand();
                    } else {
                        File elementToDelete = FileUtil.convertPath(currentDirectory, parsedCommand[1]);
                        if (elementToDelete.exists()) {
                            try {
                                currentDirectory =  FileUtil.recursiveDelete(currentDirectory, elementToDelete);
                            } catch (FileWasNotDeleted e) {
                                System.out.println("rm: cannot remove '" + e.getProblematicFile() + "'");
                                throw new WrongCommand();
                            }
                        } else {
                            System.out.println("rm: cannot remove '" + elementToDelete
                                    + "': No such file or directory");
                            throw new WrongCommand();
                        }
                    }
                    break;
                case "cp":
                    if (parsedCommand.length != 3) {
                        System.out.println("cp: get 2 parameters");
                        throw new WrongCommand();
                    } else {
                        File sourceToCp = FileUtil.convertPath(currentDirectory, parsedCommand[1]);
                        File destinationToCp = FileUtil.convertPath(currentDirectory, parsedCommand[2]);
                        if (sourceToCp.equals(destinationToCp)) {
                            System.out.println("cp: destination and source are equal");
                            throw new WrongCommand();
                        } else if (!sourceToCp.exists()) {
                            System.out.println("cp: source '" + sourceToCp.getCanonicalPath() + "' not exists");
                            throw new WrongCommand();
                        } else {
                            if (destinationToCp.exists()) {
                                FileUtil.copy(sourceToCp, destinationToCp);
                            } else if (sourceToCp.getParentFile().equals(destinationToCp.getParentFile())) {
                                FileUtil.copy(sourceToCp, destinationToCp);
                            } else {
                                System.out.println("cp: destination '" + destinationToCp.getCanonicalPath()
                                        + "' not exists");
                                throw new WrongCommand();
                            }
                        }
                    }
                    break;
                case "mv":
                    if (parsedCommand.length != 3) {
                        System.out.println("mv: get 2 parameters");
                        throw new WrongCommand();

                    } else {
                        File sourceToMv = FileUtil.convertPath(currentDirectory, parsedCommand[1]);
                        File destinationToMv = FileUtil.convertPath(currentDirectory, parsedCommand[2]);
                        if (sourceToMv.exists()) {
                            if (!destinationToMv.exists()) {
                                if (!sourceToMv.renameTo(destinationToMv)) {
                                    System.out.println("rm: cannot rename '" + sourceToMv + "'");
                                    throw new WrongCommand();
                                }
                            } else {
                                FileUtil.copy(sourceToMv, destinationToMv);
                                try {
                                    currentDirectory = FileUtil.recursiveDelete(currentDirectory, sourceToMv);
                                } catch (FileWasNotDeleted e) {
                                    System.out.println("rm: cannot remove '" + e.getProblematicFile() + "'");
                                    throw new WrongCommand();
                                }
                            }
                        }
                    }
                    break;
                case "":
                    throw new WrongCommand();
                default:
                    System.out.println("unknown command: '" + parsedCommand[0] + "'");
                    throw new WrongCommand();
            }
        } catch (WrongCommand e) {
            if (!interactiveMode) {
                throw e;
            }
        }
    }

    public static void main(String[] args) {
        interactiveMode = (args.length == 0);
        try {
            currentDirectory = new File(".").getCanonicalFile();
            ConsoleInput shellInputCommands;
            if (interactiveMode) {
                shellInputCommands = new InteractiveInput();
            } else {
                shellInputCommands = new PackageInput(args);
            }
            while (shellInputCommands.hasNext()) {
                processCommand(shellInputCommands.getNext());
            }
        } catch (ExitCommand | NoNextCommand e) {
            System.exit(0);
        } catch (IOException | WrongCommand e) {
            System.exit(1);
        }
    }

}

