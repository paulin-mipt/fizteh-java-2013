package ru.fizteh.fivt.students.surakshina.filemap;

import java.util.List;

public class TestImplementation implements InterfaceTest {
    public TestImplementation() {
    }

    @Override
    public String getStringFromIterable(List<?> list) {
        return "blabla";
    }

    @Override
    public void doNothing(int index) {
    }

    @Override
    public void onlyThrowException() throws Exception {
        throw new Exception("Everything is bad");
    }

    @Override
    public boolean getBooleanThrowException(boolean s) throws Exception {
        throw new Exception("Boolean is not good  " + s);
    }

}
