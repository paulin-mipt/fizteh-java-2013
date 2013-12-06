package ru.fizteh.fivt.students.adanilyak.tests;

import java.util.List;

/**
 * User: Alexander
 * Date: 30.11.13
 * Time: 4:09
 */
public class ImplementationForProxyTest implements InterfaceForProxyTest {
    @Override
    public String methodWithOutArgs() {
        return new String("methodWithOutArgs result");
    }

    @Override
    public String methodMixedArgs(String string, Integer[] intArray, List<String> list) {
        return "methodMixedArgs result";
    }

    @Override
    public List<Object> methodWithCycleReferences(List<Object> list) {
        return list;
    }

    @Override
    public void methodThrowsException(String string, Integer integer, List<String> list)
            throws IllegalStateException {
        throw new IllegalStateException("implementation method throws exception: ok!");
    }
}
