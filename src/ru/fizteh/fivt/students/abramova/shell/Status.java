package ru.fizteh.fivt.students.abramova.shell;

import ru.fizteh.fivt.students.abramova.filemap.FileMap;
import ru.fizteh.fivt.students.abramova.multifilehashmap.MultiFileMap;

public class Status<Class> {
    Class object;

    public Status(Class object) {
        this.object = object;
    }

    public Stage getStage() {
        return object instanceof Stage ? (Stage) object : null;
    }

    public FileMap getFileMap() {
        return object instanceof FileMap ? (FileMap) object : null;
    }

    public MultiFileMap getMultiFileMap() {
        return object instanceof MultiFileMap ? (MultiFileMap) object : null;
    }

    public boolean isStage() {
        return object instanceof Stage;
    }

    public boolean isFileMap() {
        return object instanceof FileMap;
    }

    public boolean isMultiFileMap() {
        return object instanceof MultiFileMap;
    }
}
