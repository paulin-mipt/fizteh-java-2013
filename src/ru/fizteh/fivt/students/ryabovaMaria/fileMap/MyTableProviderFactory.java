package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import java.io.IOException;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class MyTableProviderFactory implements TableProviderFactory {
    private File curDir;
    
    @Override
    public TableProvider create(String dir) throws IOException {
        if (dir == null || dir.isEmpty()) {
            throw new IllegalArgumentException("Bad directory");
        }
        curDir = new File(dir);
        
        if (!curDir.exists()) {
            throw new IOException("Directory doesn't exists");
        }
        if (!curDir.isDirectory()) {
            throw new IllegalArgumentException("Argument is not a directory");
        }
        return new TableProviderCommands(curDir);
    }
}
