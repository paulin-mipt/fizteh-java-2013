package ru.fizteh.fivt.students.adanilyak.tests;

import java.util.List;

/**
 * User: Alexander
 * Date: 30.11.13
 * Time: 4:07
 */
public interface InterfaceForProxyTest {
    String methodWithOutArgs();

    String methodMixedArgs(String string, Integer[] intArray, List<String> list);

    List<Object> methodWithCycleReferences(List<Object> list);

    void methodThrowsException(String string, Integer integer, List<String> list) throws IllegalStateException;
}
