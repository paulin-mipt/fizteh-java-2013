package ru.fizteh.fivt.students.elenarykunova.filemap.tests;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.elenarykunova.filemap.*;

public class MyLoggingProxyFactoryTest {

    private StringWriter writer;
    private MyLoggingProxyFactory factory;
    private InterfaceForProxy inter;

    @Before
    public void init() {
        factory = new MyLoggingProxyFactory();
        writer = new StringWriter();
        ClassForProxy clazz = new ClassForProxy();
        inter = (InterfaceForProxy) factory.wrap(writer, clazz, InterfaceForProxy.class);
    }

    public String stripTimestamp(String str) {
        String res = str.substring(0, "<invoke ".length());
        int first = str.indexOf("class");
        return res + str.substring(first, str.length());
    }

    @Test(expected = IllegalStateException.class)
    public void methodExceptionTest() {
        try {
            inter.methodException();
        } finally {
            String result = stripTimestamp(writer.toString());
            assertEquals("<invoke class=\"ru.fizteh.fivt.students.elenarykunova.filemap.tests.ClassForProxy\" "
                    + "name=\"methodException\">" + "<arguments/>"
                    + "<thrown>java.lang.IllegalStateException: i'm exception from void method!</thrown>" 
                    + "</invoke>"
                    + System.lineSeparator(), result);
        }
    }

    @Test
    public void methodIntegerTest() {
        try {
            inter.methodInteger(10);
        } finally {
            String result = stripTimestamp(writer.toString());
            assertEquals("<invoke class=\"ru.fizteh.fivt.students.elenarykunova.filemap.tests.ClassForProxy\" "
                    + "name=\"methodInteger\">" + "<arguments><argument>10</argument></arguments>"
                    + "<return>11</return>" 
                    + "</invoke>" 
                    + System.lineSeparator(), result);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void methodStringExceptionTest() {
        try {
            inter.methodStringException(10);
        } finally {
            String result = stripTimestamp(writer.toString());
            assertEquals("<invoke class=\"ru.fizteh.fivt.students.elenarykunova.filemap.tests.ClassForProxy\" "
                    + "name=\"methodStringException\">" + "<arguments><argument>10</argument></arguments>"
                    + "<thrown>java.lang.IllegalArgumentException: i'm exception from returnable method!</thrown>"
                    + "</invoke>" 
                    + System.lineSeparator(), result);
        }
    }

    @Test
    public void methodIntegerFromListTest() {
        try {
            List<Integer> list = new ArrayList<Integer>();
            list.add(5);
            list.add(10);
            inter.methodIntegerFromList(list);
        } finally {
            String result = stripTimestamp(writer.toString());
            assertEquals("<invoke class=\"ru.fizteh.fivt.students.elenarykunova.filemap.tests.ClassForProxy\" "
                    + "name=\"methodIntegerFromList\">"
                    + "<arguments><argument><list><value>5</value><value>10</value></list></argument></arguments>"
                    + "<return>100</return>" 
                    + "</invoke>" 
                    + System.lineSeparator(), result);
        }
    }

    @Test
    public void methodIntegerFromListCyclicTest() {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(5);
            list.add(list);
            inter.methodIntegerFromList(list);
        } finally {
            String result = stripTimestamp(writer.toString());
            assertEquals("<invoke class=\"ru.fizteh.fivt.students.elenarykunova.filemap.tests.ClassForProxy\" "
                        + "name=\"methodIntegerFromList\">"
                        + "<arguments><argument><list><value>5</value><value><list><value>5</value>"
                        + "<value>cyclic</value></list></value></list></argument></arguments>"
                        + "<return>100</return>" 
                        + "</invoke>" + System.lineSeparator(), result);
        }
    }

    @Test
    public void methodJustVoidTest() {
        try {
            inter.methodJustVoid();
        } finally {
            String result = stripTimestamp(writer.toString());
            assertEquals("<invoke class=\"ru.fizteh.fivt.students.elenarykunova.filemap.tests.ClassForProxy\" "
                    + "name=\"methodJustVoid\">" 
                    + "<arguments/>" 
                    + "</invoke>" 
                    + System.lineSeparator(), result);
        }
    }

    @Test
    public void methodIntegerFromArrayTest() {
        try {
            int a = 7;
            int b = 5;
            inter.methodIntegerFromArray(a, b);
        } finally {
            String result = stripTimestamp(writer.toString());
            assertEquals("<invoke class=\"ru.fizteh.fivt.students.elenarykunova.filemap.tests.ClassForProxy\" "
                    + "name=\"methodIntegerFromArray\">" 
                    + "<arguments><argument>7</argument>"
                    + "<argument>5</argument></arguments>"
                    + "<return>7</return>" 
                    + "</invoke>" 
                    + System.lineSeparator(), result);
        }
    }
}
