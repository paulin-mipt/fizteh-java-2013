package ru.fizteh.fivt.students.surakshina.filemap;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProxyTest {
    private ProxyFactory factory;
    private InterfaceTest test;
    private StringWriter writer;

    private String getStringFromWriter() {
        String[] str = writer.toString().split("\\s");
        StringBuffer str1 = new StringBuffer();
        for (int i = 2; i < str.length; ++i) {
            str1.append(str[i]);
            str1.append(" ");
        }
        str1.delete(str1.length() - 10, str1.length());
        return str1.toString();

    }

    @Before
    public void start() {
        factory = new ProxyFactory();
        writer = new StringWriter();
        TestImplementation impl = new TestImplementation();
        test = (InterfaceTest) factory.wrap(writer, impl, InterfaceTest.class);
    }

    @Test
    public void getStringTest() {
        ArrayList<String> list = new ArrayList<>();
        list.add("3");
        test.getStringFromIterable(list);
        String str = getStringFromWriter();
        assertEquals(str, "class=\"ru.fizteh.fivt.students.surakshina.filemap.TestImplementation\" "
                + "name=\"getStringFromIterable\"><arguments><argument>"
                + "<list><value>3</value></list></argument></arguments><return>blabla</return>");
    }

    @Test
    public void doNothingTest() {
        test.doNothing(0);
        String str = getStringFromWriter();
        assertEquals(str, "class=\"ru.fizteh.fivt.students.surakshina.filemap.TestImplementation\""
                + " name=\"doNothing\"><arguments><argument>0</argument></arguments>");

    }

    @Test(expected = Exception.class)
    public void onlyThrowExceptionTest() throws Exception {
        test.onlyThrowException();
        String str = getStringFromWriter();
        assertEquals(str, "class=\"ru.fizteh.fivt.students.surakshina.filemap.TestImplementation\""
                + " name=\"onlyThrowException\"><arguments/><thrown>java.lang.Exception: Everything is bad</thrown>");
    }

    @Test(expected = Exception.class)
    public void getBooleanThrowExceptionTest() throws Exception {
        test.getBooleanThrowException(true);
        String str = getStringFromWriter();
        assertEquals(str, "class=\"ru.fizteh.fivt.students.surakshina.filemap.TestImplementation\" "
                + "name=\"getBooleanThrowException\"><arguments><argument>true</argument></arguments>"
                + "<thrown>java.lang.Exception: Boolean is not good  true</thrown>");

    }

    @After
    public void finish() throws IOException {
        writer.close();
    }

}
