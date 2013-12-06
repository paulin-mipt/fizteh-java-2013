package ru.fizteh.fivt.students.paulinMatavina.proxy;

import java.util.List;

public class ClassImplementingTestInterface implements TestInterface {
    String name;
    
    public ClassImplementingTestInterface() {
        name = "right testing class implementing TestInterface";
    }

    @Override
    public int getIntFromIterable(List<?> list) {
        return 42;
    }

    @Override
    public void takeStringDoNothing(String s) {
        // does nothing?       
    }

    @Override
    public int getIntThrowException(int i) throws Exception {
        throw new RuntimeException("passed int " + i);
    }

    @Override
    public void justThrowException() throws Exception {
        throw new Exception("what if i throw it?");
    }
    
    
}
