package ru.fizteh.fivt.students.elenarykunova.filemap.tests;

import java.util.List;

public interface InterfaceForProxy {
    
    void methodException() throws IllegalStateException;
    
    void methodJustVoid();
    
    int methodInteger(int intArg);
    
    String methodStringException(int intArg) throws IllegalArgumentException;
    
    int methodIntegerFromList(List<?> list);
    
    int methodIntegerFromArray(int int1, int int2);

}
