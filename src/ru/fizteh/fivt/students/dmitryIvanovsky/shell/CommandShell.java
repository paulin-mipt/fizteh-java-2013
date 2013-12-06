package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class CommandShell implements CommandAbstract {

    File currentFile;
    boolean out;
    boolean err;

    public Map<String, Object[]> mapComamnd() {
        Map<String, Object[]> commandList = new HashMap<String, Object[]>(){ {
            put("dir",      new Object[] {"dir",     false,  0 });
            put("mv",       new Object[] {"mv",      false,  2 });
            put("cp",       new Object[] {"cp",      false,  2 });
            put("rm",       new Object[] {"rm",      false,  1 });
            put("pwd",      new Object[] {"pwd",     false,  0 });
            put("mkdir",    new Object[] {"mkdir",   false,  1 });
            put("cd",       new Object[] {"cd",      false,  1 });
        }};
        return commandList;
    }

    public CommandShell() {
        currentFile = new File(".");
        this.out = true;
        this.err = true;
    }

    public CommandShell(String path) {
        currentFile = new File(path);
        this.out = true;
        this.err = true;
    }

    public CommandShell(String path, boolean out, boolean err) {
        currentFile = new File(path);
        this.out = out;
        this.err = err;
    }

    public String getCurrentFile() {
        return currentFile.toString();
    }

    public String startShellString() throws IOException {
        return String.format("%s$ ", currentFile.getCanonicalPath());
    }

    private String joinDir(String name) {
        File tmpFile = new File(name);
        if (tmpFile.isAbsolute()) {
            return tmpFile.getAbsolutePath();
        } else {
            return currentFile.getAbsolutePath() + File.separator + name;
        }
    }

    private void copyMove(String source, String destination, boolean isCopy) throws IOException, ErrorShell {
        String rusDescription = "переместить";
        String command = "mv";
        if (isCopy) {
            rusDescription = "скопировать";
            command = "cp";
        }
        try {
            File fileSource = new File(joinDir(source));
            String nameSource = fileSource.getName();
            File fileDestination = new File(joinDir(destination));
            if (fileSource.isDirectory()) {
                Path pathDestination = Paths.get(joinDir(destination));
                if (fileDestination.isDirectory()) {
                    destination = destination + File.separator + nameSource;
                    pathDestination = Paths.get(joinDir(destination));
                }
                if (isCopy) {
                    Files.copy(Paths.get(joinDir(source)), pathDestination, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.move(Paths.get(joinDir(source)), pathDestination, StandardCopyOption.REPLACE_EXISTING);
                }
                File[] listFiles = fileSource.listFiles();
                if (listFiles != null || listFiles.length != 0) {
                    for (File c : listFiles) {
                        String nameFile = c.getName();
                        copyMove(c.toString(), joinDir(destination) + File.separator + nameFile, isCopy);
                    }
                }
            } else {
                if (fileDestination.getCanonicalFile().equals(fileSource.getCanonicalFile())) {
                    String error;
                    if (err) {
                        error = String.format("%s: \'%s %s\': файлы совпадают", command, source, destination);
                    } else {
                        error = null;
                    }
                    throw new ErrorShell(error);
                }
                Path pathDestination = Paths.get(joinDir(destination));
                if (fileDestination.isDirectory()) {
                    pathDestination = Paths.get(joinDir(destination) + File.separator + nameSource);
                }
                if (isCopy) {
                    Files.copy(Paths.get(joinDir(source)), pathDestination, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.move(Paths.get(joinDir(source)), pathDestination, StandardCopyOption.REPLACE_EXISTING);
                }
            }

        } catch (Exception e) {
            String error;
            if (err) {
                error = String.format("%s: \'%s %s\': не могу %s", command, source, destination, rusDescription);
            } else {
                error = null;
            }
            ErrorShell ex = new ErrorShell(error);
            ex.addSuppressed(e);
            throw ex;
        }
    }

    public void cd(String[] args) throws ErrorShell {
        String newDir = args[0];
        try {
            File tmpFile = new File(newDir);
            if (!tmpFile.isAbsolute()) {
                tmpFile = new File(joinDir(newDir));
            }
            if (!tmpFile.isDirectory()) {
                String error;
                if (err) {
                    error = String.format("cd: \'%s\': нет такого пути", newDir);
                } else {
                    error = null;
                }
                throw new ErrorShell(error);
            } else {
                currentFile = tmpFile;
            }

        } catch (Exception e) {
            String error;
            if (err) {
                error = String.format("cd: \'%s\': нет такого пути", newDir);
            } else {
                error = null;
            }
            e.addSuppressed(new ErrorShell(error));
            throw e;
        }
    }

    public void cp(String[] args) throws IOException, ErrorShell {
        String source = args[0];
        String destination = args[1];
        copyMove(source, destination, true);
    }

    public void mv(String[] args) throws IOException, ErrorShell {
        String source = args[0];
        String destination = args[1];
        copyMove(source, destination, false);
    }

    public void rm(String[] args) throws ErrorShell {
        String path = args[0];
        try {
            File tmpFile = new File(joinDir(path));
            File[] listFiles = tmpFile.listFiles();
            if (listFiles != null) {
                if (tmpFile.isDirectory()) {
                    for (File c : listFiles) {
                        rm(new String[]{c.toString()});
                    }
                }
            }
            if (!tmpFile.delete()) {
                String error;
                if (err) {
                    error = String.format("rm: \'%s\': не могу удалить", path);
                } else {
                    error = null;
                }
                //throw new ErrorShell(error);
            }

        } catch (Exception e) {
            String error;
            if (err) {
                error = String.format("rm: \'%s\': не могу удалить", path);
            } else {
                error = null;
            }
            e.addSuppressed(new ErrorShell(error));
            throw e;
        }
    }

    public void dir(String[] args) {
        try {
            for (String child : currentFile.list()) {
                outPrint(child);
            }
        } catch (Exception e) {
            String error;
            if (err) {
                error = String.format("dir: неправильный путь");
            } else {
                error = null;
            }
            e.addSuppressed(new ErrorShell(error));
            throw e;
        }
    }

    public void pwd(String[] args) throws IOException {
        try {
            outPrint(currentFile.getCanonicalPath());
        } catch (Exception e) {
            String error;
            if (err) {
                error = String.format("pwd: неправильный путь");
            } else {
                error = null;
            }
            e.addSuppressed(new ErrorShell(error));
            throw e;
        }
    }

    public void mkdir(String[] args) throws ErrorShell {
        String directoryName = args[0];
        File theDir = new File(joinDir(directoryName));
        if (!theDir.exists()) {
            try {
                boolean result = theDir.mkdir();
                if (!result) {
                    String error;
                    if (err) {
                        error = String.format("mkdir: \'%s\': не могу создать директорию", directoryName);
                    } else {
                        error = null;
                    }
                    throw new ErrorShell(error);
                }
            } catch (Exception e) {
                String error;
                if (err) {
                    error = String.format("mkdir: \'%s\': не могу создать директорию", directoryName);
                } else {
                    error = null;
                }
                throw new ErrorShell(error);
            }
        } else {
            String error;
            if (err) {
                error = String.format("mkdir: \'%s\': директория существует", directoryName);
            } else {
                error = null;
            }
            throw new ErrorShell(error);
        }
    }

    private void errPrint(String message) {
        if (err) {
            System.err.println(message);
        }
    }

    private void outPrint(String message) {
        if (out) {
            System.out.println(message);
        }
    }

}
