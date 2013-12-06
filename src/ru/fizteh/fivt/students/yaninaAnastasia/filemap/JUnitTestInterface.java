package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import java.util.List;

public interface JUnitTestInterface {
    void exec();

    void supportFunc() throws Exception;

    int getAmount();

    String arrayGetter(List<String> types, int number);
}
