package ru.fizteh.fivt.students.piakovenko.filemap.storable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.piakovenko.shell.Shell;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 26.10.13
 * Time: 23:58
 * To change this template use File | Settings | File Templates.
 */
public class DataBasesFactory implements TableProviderFactory {
    private Shell shell = null;

    public synchronized TableProvider create(String dir) throws IllegalArgumentException, IOException {
        Checker.stringNotEmpty(dir);
        File fileMapStorage = null;
        fileMapStorage = new File(dir);
        if (fileMapStorage.isFile()) {
            throw new IllegalArgumentException("try create provider on file");
        }
        if (!fileMapStorage.exists()) {
            if (!fileMapStorage.mkdir()) {
                throw new IOException("Can't create the directory " + fileMapStorage.getCanonicalPath());
            }
        }
        shell = new Shell();
        return new DataBasesCommander(shell, fileMapStorage);
    }

    public void start(String[] args) {
        shell.start(args);
    }
}
