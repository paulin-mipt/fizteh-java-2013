package ru.fizteh.fivt.students.valentinbarishev.proxy.test;

import java.io.IOException;
import java.util.List;

public class TestClass implements TestInterface {

    public TestClass() {
    }

    @Override
    public int arrayParameter(List<Object> list) {
        return 10000;
    }

    @Override
    public void voidNoArgs() {

    }

    @Override
    public int exceptionThrow() throws IOException {
        throw new IOException("IO EXCEPTION MESSAGE");
    }
}
