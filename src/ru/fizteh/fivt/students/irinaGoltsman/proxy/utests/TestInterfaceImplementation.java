package ru.fizteh.fivt.students.irinaGoltsman.proxy.utests;

import java.io.IOException;
import java.util.List;

public class TestInterfaceImplementation implements TestInterface {
    @Override
    public void emptyFunction() {
    }

    @Override
    public void throwException() throws IOException {
        throw new IOException("test exception");
    }

    @Override
    public Integer sum(Integer one, Integer second) {
        return one + second;
    }

    @Override
    public void listAsArgument(List<?> values) {
    }
}
