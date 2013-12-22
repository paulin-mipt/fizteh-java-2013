package ru.fizteh.fivt.students.valentinbarishev.proxy.test;

import java.io.IOException;
import java.util.List;

public interface TestInterface {
    int arrayParameter(List<Object> list);
    void voidNoArgs();
    int exceptionThrow() throws IOException;
}
