package ru.fizteh.fivt.students.irinaGoltsman.proxy.utests;

import java.io.IOException;
import java.util.List;

public interface TestInterface {
    void emptyFunction();

    void throwException() throws IOException;

    Integer sum(Integer one, Integer second);

    void listAsArgument(List<?> values);
}
