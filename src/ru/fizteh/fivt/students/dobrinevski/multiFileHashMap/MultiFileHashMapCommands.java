package ru.fizteh.fivt.students.dobrinevski.multiFileHashMap;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class MultiFileHashMapCommands {

    public static class Put extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            if (parent.curTable == null) {
                returnValue = new String[1];
                returnValue[0] = "no table";
                return;
            }

            if ((args[2] == null) || (args[1] == null) || (args[2].isEmpty()) || (args[1].isEmpty())
                    || args[1].contains(" ") || args[1].contains("\t")) {
                throw new Exception(args[1] + " shouldn't be a key");
            }

            Integer hashCode = args[1].hashCode();
            hashCode = Math.abs(hashCode);
            Integer nDirectory = hashCode % 16;
            Integer nFile = hashCode / 16 % 16;

            File dbFile = new File(parent.curTable.getCanonicalPath() + File.separator + nDirectory.toString() + ".dir"
                    + File.separator + nFile.toString() + ".dat");
            parent.parseFile(dbFile, nDirectory, nFile);

            String value = parent.dataBase.get(nDirectory * 16 + nFile).put(args[1], args[2]);
            if (value == null) {
                returnValue = new String[1];
                returnValue[0] = "new";
            } else {
                returnValue = new String[2];
                returnValue[0] = "overwrite";
                returnValue[1] = value;
            }
        }

        public Put(MyMultiHashMap parent, File root) {
            super(3, parent, root);
        }
    }

    public static class Get extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            if (parent.curTable == null) {
                returnValue = new String[1];
                returnValue[0] = "no table";
                return;
            }
            Integer hashCode = args[1].hashCode();
            hashCode = Math.abs(hashCode);
            Integer nDirectory = hashCode % 16;
            Integer nFile = hashCode / 16 % 16;

            File dbFile = new File(parent.curTable.getCanonicalPath() + File.separator + nDirectory.toString() + ".dir"
                    + File.separator + nFile.toString() + ".dat");
            parent.parseFile(dbFile, nDirectory, nFile);

            String value = parent.dataBase.get(nDirectory * 16 + nFile).get(args[1]);
            if (value == null) {
                returnValue = new String[1];
                returnValue[0] = "not found";
            } else {
                returnValue = new String[2];
                returnValue[0] = "found";
                returnValue[1] = value;
            }
        }

        public Get(MyMultiHashMap parent, File root) {
            super(2, parent, root);
        }
    }

    public static class Use extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            Path dbsDir = root.toPath().resolve(args[1]).normalize();
            if (Files.notExists(dbsDir) || !Files.isDirectory(dbsDir)) {
                returnValue = new String[1];
                returnValue[0] = args[1] + " not exists";
                return;
            }
            if (parent.curTable != null) {
                parent.writeOut();
            }
            parent.curTable = new File(root.getCanonicalPath() + File.separator + args[1]);
            returnValue = new String[1];
            returnValue[0] = "using " + args[1];
        }

        public Use(MyMultiHashMap parent, File root) {
            super(2, parent, root);
        }
    }

    public static class Drop extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            if (parent.curTable != null && parent.curTable.getCanonicalPath()
                    .equals(root.getCanonicalPath() + File.separator + args[1])) {
                parent.writeOut();
                parent.curTable = null;
            }
            Path pathToRemove = root.toPath().resolve(args[1]).normalize();
            if (!Files.exists(pathToRemove)) {
                returnValue = new String[1];
                returnValue[0] = args[1] + " not exists";
                return;
            }
            File fileToRemove = new File(args[1]);
            if (!fileToRemove.isAbsolute()) {
                fileToRemove = new File(root.getCanonicalPath() + File.separator + args[1]);
            }
            File[] filesToRemove = fileToRemove.listFiles();
            if (filesToRemove != null) {
                for (File file : filesToRemove) {
                    String[] toRemove = new String[2];
                    toRemove[0] = args[0];
                    toRemove[1] = file.getPath();
                    removeFile(toRemove, root);
                }
            }
            if (!Files.deleteIfExists(pathToRemove)) {
                throw new Exception("\'" + fileToRemove.getCanonicalPath()
                        + "\' : File cannot be removed ");
            }
            returnValue = new String[1];
            returnValue[0] = "dropped";
        }

        public Drop(MyMultiHashMap parent, File root) {
            super(2, parent, root);
        }
    }

    public static class Create extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            File tmpFile = new File(args[1]);
            if (!tmpFile.isAbsolute()) {
                tmpFile = new File(root.getCanonicalPath() + File.separator + args[1]);
            }
            if (tmpFile.exists() && tmpFile.isDirectory()) {
                returnValue = new String[1];
                returnValue[0] = args[1] + " exists";
                return;
            }
            if (!tmpFile.mkdir()) {
                throw new Exception("\'" + args[1] + "\': Table wasn't created");
            }
            returnValue = new String[1];
            returnValue[0] = "created";
        }

        public Create(MyMultiHashMap parent, File root) {
            super(2, parent, root);
        }
    }

    public static class Remove extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            if (parent.curTable == null) {
                returnValue = new String[1];
                returnValue[0] = "no table";
                return;
            }
            Integer hashCode = args[1].hashCode();
            hashCode = Math.abs(hashCode);
            Integer nDirectory = hashCode % 16;
            Integer nFile = hashCode / 16 % 16;

            File dbFile = new File(root.getCanonicalPath() + File.separator + nDirectory.toString() + ".dir"
                    + File.separator + nFile.toString() + ".dat");
            parent.parseFile(dbFile, nDirectory, nFile);

            if (parent.dataBase.get(nDirectory * 16 + nFile).remove(args[1]) == null) {
                returnValue = new String[1];
                returnValue[0] = "not found";
            } else {
                returnValue = new String[1];
                returnValue[0] = "removed";
            }
        }

        public Remove(MyMultiHashMap parent, File root) {
            super(2, parent, root);
        }
    }

    public static class Exit extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            parent.writeOut();
            System.exit(0);
        }

        public Exit(MyMultiHashMap parent, File root) {
            super(1, parent, root);
        }
    }

    public static void removeFile(String[] args, File currentDir) throws Exception {
        Path pathToRemove = currentDir.toPath().resolve(args[1]).normalize();
        if (!Files.exists(pathToRemove)) {
            throw new Exception("Cannot be removed: File does not exist");
        }
        if (currentDir.toPath().normalize().startsWith(pathToRemove)) {
            throw new Exception("\'" + args[1]
                    + "\': Cannot be removed: First of all, leave this directory");
        }

        File fileToRemove = new File(args[1]);
        if (!fileToRemove.isAbsolute()) {
            fileToRemove = new File(currentDir.getCanonicalPath() + File.separator + args[1]);
        }
        File[] filesToRemove = fileToRemove.listFiles();
        if (filesToRemove != null) {
            for (File file : filesToRemove) {
                try {
                    String[] toRemove = new String[2];
                    toRemove[0] = args[0];
                    toRemove[1] = file.getPath();
                    removeFile(toRemove, currentDir);
                } catch (Exception e) {
                    throw new Exception("\'" + file.getCanonicalPath()
                            + "\' : File cannot be removed: " + e.getMessage() + " ");
                }
            }
        }

        if (!Files.deleteIfExists(pathToRemove)) {
            throw new Exception("\'" + fileToRemove.getCanonicalPath()
                    + "\' : File cannot be removed ");
        }
    }
}
