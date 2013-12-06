package ru.fizteh.fivt.students.dzvonarev.filemap;

import java.util.List;

public interface ProxyTestInterface {

    void voidAction();

    int getInteger(int objInt);

    Integer getIntFromIterable(List<?> object);

    void throwExceptionAction() throws Exception;

}
