package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        runner.runTest(TestFileMap.class);
        runner.runTest(TestFileMapProvider.class);
        runner.runTest(TestFileMapProviderFactory.class);
        runner.runTest(TestFileMapStoreable.class);
        runner.runTest(TestFileMapParallel.class);
        runner.runTest(TestProxy.class);
    }

    public void runTest(Class testClass) {
        Result result = JUnitCore.runClasses(testClass);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println(result.wasSuccessful());
    }

}
