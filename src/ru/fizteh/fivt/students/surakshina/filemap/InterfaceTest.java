package ru.fizteh.fivt.students.surakshina.filemap;

import java.util.List;

public interface InterfaceTest {
    String getStringFromIterable(List<?> list);

    void doNothing(int index);

    void onlyThrowException() throws Exception;

    boolean getBooleanThrowException(boolean s) throws Exception;

}
