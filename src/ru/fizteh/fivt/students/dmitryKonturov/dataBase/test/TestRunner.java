package ru.fizteh.fivt.students.dmitryKonturov.dataBase.test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
    public static void main(String[] args) {
        runTest(TestTableProviderFactoryImplementation.class);
        runTest(TestTableProviderImplementation.class);
        runTest(TestTableImplementation.class);
        runTest(TestStoreableImplementation.class);
    }

    public static void runTest(Class testClass) {
        Result result = JUnitCore.runClasses(testClass);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        String verdict = result.wasSuccessful() ? "OK" : "FAIL";
        System.out.println(String.format("%s: %s", testClass.getSimpleName(), verdict));
        System.out.println("------------------------------------------------");
        System.out.println();
    }
}
