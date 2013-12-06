package ru.fizteh.fivt.students.anastasyev.proxy.tests;

import java.util.List;

public interface MyInterface {
    String run();
    String runWithArguments(String str, Integer integer, List<String> list);
    List<Object> runCycle(List<Object> list);
    void runThrowable(String str, Integer integer, List<String> list) throws IllegalStateException;
}
