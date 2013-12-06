package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.AbstractFrame;
import ru.fizteh.fivt.students.fedoseev.filemap.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class AbstractMultiFileHashMap extends AbstractFrame<MultiFileHashMapState> {
    public AbstractMultiFileHashMap(File dir) throws IOException {
        state = new MultiFileHashMapState(dir);

        File newDir = state.getCurDir();

        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        if (newDir.listFiles() != null) {
            for (File file : newDir.listFiles()) {
                state.createTable(file.getName());
            }
        }
    }

    @Override
    public Map<String, AbstractCommand> getCommands() {
        final MultiFileHashMapCreateCommand create = new MultiFileHashMapCreateCommand();
        final MultiFileHashMapDropCommand drop = new MultiFileHashMapDropCommand();
        final MultiFileHashMapUseCommand use = new MultiFileHashMapUseCommand();
        final FileMapPutCommand put = new FileMapPutCommand();
        final FileMapGetCommand get = new FileMapGetCommand();
        final FileMapRemoveCommand remove = new FileMapRemoveCommand();
        final FileMapExitCommand exit = new FileMapExitCommand();
        final MultiFileHashMapSizeCommand size = new MultiFileHashMapSizeCommand();
        final MultiFileHashMapCommitCommand commit = new MultiFileHashMapCommitCommand();
        final MultiFileHashMapRollbackCommand rollback = new MultiFileHashMapRollbackCommand();

        return new HashMap<String, AbstractCommand>() {
            {
                put(create.getCmdName(), create);
                put(drop.getCmdName(), drop);
                put(use.getCmdName(), use);
                put(put.getCmdName(), put);
                put(get.getCmdName(), get);
                put(remove.getCmdName(), remove);
                put(exit.getCmdName(), exit);
                put(size.getCmdName(), size);
                put(commit.getCmdName(), commit);
                put(rollback.getCmdName(), rollback);
            }
        };
    }

    public static void readTableOff(MultiFileHashMapTable table) throws IOException {
        File curDir = new File(table.getName());

        if (curDir.listFiles() != null) {
            for (File dir : curDir.listFiles()) {
                if (dir.listFiles() != null) {
                    for (File file : dir.listFiles()) {
                        RandomAccessFile raf = new RandomAccessFile(file, "r");

                        checkOpenFile(table, raf);
                    }
                }
            }
        }
    }

    public static void checkOpenFile(MultiFileHashMapTable curTable, RandomAccessFile curFile) throws IOException {
        curTable.ifUnfitCurFileSize(curFile);
        curTable.ifUnfitCurTableSize();

        curTable.setTableSize(curTable.getTableSize() + curFile.length());

        Map<String, String> map = AbstractFileMap.readFile(curFile);
        curTable.putMapTable(map);

        curFile.close();
    }

    public static void saveTable(MultiFileHashMapTable curTable) throws IOException {
        if (curTable == null) {
            return;
        }

        curTable.setTableSize(0);

        File[] directories = new File[curTable.getDirsNumber()];
        Map<Integer, File> files = new HashMap<>();
        Map<Integer, RandomAccessFile> rAFiles = new HashMap<>();
        Set<String> keySet = curTable.getMapContent().keySet();

        curTable.setUsedDirs();

        for (int i = 0; i < curTable.getDirsNumber(); i++) {
            directories[i] = new File(curTable.getCurTableDir(), i + ".dir");

            if (!directories[i].exists() && curTable.getBoolUsedDirs()[i]) {
                directories[i].mkdirs();
            }

            for (String key : keySet) {
                if (curTable.getBoolUsedDirs()[i]) {
                    curTable.getBoolUsedFiles()[i][curTable.fileHash(key)] = true;
                }
            }

            for (int j = 0; j < curTable.getDirFilesNumber(); j++) {
                if (curTable.getBoolUsedFiles()[i][j]) {
                    File curFile = new File(
                            curTable.getCurTableDir().toPath().resolve(i + ".dir").toFile(), j + ".dat"
                    );

                    int numb = curTable.getDirsNumber() * i + j;
                    files.put(numb, curFile);

                    if (!files.get(numb).exists()) {
                        files.get(numb).createNewFile();
                    }
                }
            }
        }

        Set<String> curFileKeySet = new HashSet<>();

        for (int i = 0; i < curTable.getDirsNumber(); i++) {
            for (int j = 0; j < curTable.getDirFilesNumber(); j++) {
                if (curTable.getBoolUsedFiles()[i][j]) {
                    RandomAccessFile raf = new RandomAccessFile(files.get(curTable.getDirsNumber() * i + j), "rw");

                    curFileKeySet.clear();

                    for (String curFileKey : keySet) {
                        if (j == curTable.fileHash(curFileKey) && i == curTable.dirHash(curFileKey)) {
                            curFileKeySet.add(curFileKey);
                        }
                    }

                    AbstractFileMap.commitFile(raf, curFileKeySet, curTable.getMapContent());

                    raf.close();
                }
            }
        }

        curTable.clearUsedDirs();
        curTable.clearUsedFiles();

        for (int i : rAFiles.keySet()) {
            rAFiles.get(i).close();
        }

        for (int i : files.keySet()) {
            if (files.get(i).length() == 0) {
                files.get(i).delete();
            }
        }

        for (File dir : directories) {
            if (dir.exists()) {
                if (dir.listFiles().length == 0) {
                    dir.delete();
                }
            }
        }
    }
}
