package ru.fizteh.fivt.students.elenarykunova.filemap.tests;

import java.util.List;

public class ClassForProxy implements InterfaceForProxy{

    @Override
    public void methodException() throws IllegalStateException {
        throw new IllegalStateException("i'm exception from void method!");
    }

    @Override
    public int methodInteger(int intArg) {
        return intArg + 1;
    }

    @Override
    public String methodStringException(int intArg) throws IllegalArgumentException {
        throw new IllegalArgumentException("i'm exception from returnable method!");
    }

    @Override
    public int methodIntegerFromList(List<?> list) {
        return 100;
    }

    @Override
    public void methodJustVoid() {
    }

    @Override
    public int methodIntegerFromArray(int i1, int i2) {
        return i1;
    }

}
