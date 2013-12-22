package ru.fizteh.fivt.students.anastasyev.proxy.tests;

import java.util.List;

class Impl implements MyInterface {
    @Override
    public String run() {
        return "Run!";
    }

    @Override
    public String runWithArguments(String str, Integer integer, List<String> list) {
        return "Run anyway?";
    }

    @Override
    public List<Object> runCycle(List<Object> list) {
        return null;
    }

    @Override
    public void runThrowable(String str, Integer integer, List<String> list) throws IllegalStateException {
        throw new IllegalStateException("Do not run. :(");
    }
}
