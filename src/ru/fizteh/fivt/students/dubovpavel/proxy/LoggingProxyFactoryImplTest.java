package ru.fizteh.fivt.students.dubovpavel.proxy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.util.HashSet;

public class LoggingProxyFactoryImplTest {
    private StringWriter xml;
    private LoggingProxyFactoryImpl log;
    @Before
    public void setUp() {
        xml = new StringWriter();
        log = new LoggingProxyFactoryImpl();
    }
    @After
    public void tearDown() {
        /* System.out.println(xml.toString()); */
    }

    @Test
    public void testLaunch() {
        TestInterface<Integer> testObj =
                (TestInterface<Integer>) log.wrap(xml, new TestClass<Integer>(), TestInterface.class);
        testObj.test(5);
    }

    @Test
    public void testCyclic() {
        TestInterface<HashSet<HashSet>> testObj =
                (TestInterface<HashSet<HashSet>>) log.wrap(xml, new TestClass<HashSet<HashSet>>(), TestInterface.class);
        HashSet<HashSet> a = new HashSet<>();
        HashSet<HashSet> b = new HashSet<>();
        a.add(b);
        b.add(a);
        testObj.test(a);
    }

    @Test(expected = RuntimeException.class)
    public void testThrowing() {
        TestInterface<Integer> testObj =
                (TestInterface<Integer>) log.wrap(xml, new TestClassThrows(), TestInterface.class);
        testObj.test(10);
    }

    class TestClass<T> implements TestInterface<T> {
        public int test(T i) {
            return -1;
        }
    }

    class TestClassThrows implements TestInterface<Integer> {
        public int test(Integer i) {
            throw new RuntimeException("message");
        }
    }

    interface TestInterface<T> {
        int test(T i);
    }
}
