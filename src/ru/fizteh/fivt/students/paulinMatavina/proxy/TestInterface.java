package ru.fizteh.fivt.students.paulinMatavina.proxy;

import java.util.List;

public interface TestInterface {
    int getIntFromIterable(List<?> list);
    void takeStringDoNothing(String s);
    int getIntThrowException(int i) throws Exception;
    void justThrowException() throws Exception;
}
