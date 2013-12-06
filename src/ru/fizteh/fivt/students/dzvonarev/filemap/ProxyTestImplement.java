package ru.fizteh.fivt.students.dzvonarev.filemap;

import java.util.List;

public class ProxyTestImplement implements ProxyTestInterface {

    @Override
    public void voidAction() {
    }

    @Override
    public int getInteger(int objInteger) {
        return objInteger;
    }

    @Override
    public Integer getIntFromIterable(List<?> object) {
        return 1023;
    }

    @Override
    public void throwExceptionAction() throws Exception {
        throw new Exception("exception throw success");
    }

}
