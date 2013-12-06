package ru.fizteh.fivt.students.piakovenko.filemap.strings;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.piakovenko.shell.Shell;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 26.10.13
 * Time: 23:58
 * To change this template use File | Settings | File Templates.
 */
public class DataBasesFactory implements TableProviderFactory {
    private Shell shell = null;

    public TableProvider create(String dir) throws IllegalArgumentException {
        if (dir == null || dir.trim().isEmpty()) {
            throw new IllegalArgumentException("Directory path is invalid");
        }
        File fileMapStorage = new File(dir);
        if (fileMapStorage.isFile()) {
            throw new IllegalArgumentException("try create provider on file");
        }
        if (!fileMapStorage.exists()) {
            fileMapStorage.mkdir();
        }
        shell = new Shell();
        return new DataBasesCommander(shell, fileMapStorage);
    }

    public void start(String[] args) {
        shell.start(args);
    }
}
