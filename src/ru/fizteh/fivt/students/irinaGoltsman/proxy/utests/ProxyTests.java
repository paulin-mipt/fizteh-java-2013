package ru.fizteh.fivt.students.irinaGoltsman.proxy.utests;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.irinaGoltsman.proxy.LoggingProxyFactoryImplementation;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ProxyTests {
    LoggingProxyFactory factory = new LoggingProxyFactoryImplementation();
    private TestInterface testInterface;
    private StringWriter stringWriter = new StringWriter();

    @Before
    public void init() {
        testInterface = (TestInterface) factory.wrap(stringWriter, new TestInterfaceImplementation(),
                TestInterface.class);
    }

    private String getUsefulPartOfString() {
        StringBuilder stringBuilder = new StringBuilder(stringWriter.toString());
        //Мистические махинации чтобы убить timestamp=... :
        stringBuilder.delete(0, 10);
        int index = stringBuilder.indexOf(" ");
        stringBuilder.delete(0, index + 1);
        return stringBuilder.toString();
    }

    @Test
    public void emptyFunctionTest() {
        testInterface.emptyFunction();
        Assert.assertEquals("class=\"ru.fizteh.fivt.students.irinaGoltsman.proxy.utests.TestInterfaceImplementation\" "
                + "name=\"emptyFunction\"><arguments/></invoke>\n", getUsefulPartOfString());
    }

    @Test(expected = IOException.class)
    public void throwExceptionTestThrowsTargetException() throws IOException {
        testInterface.throwException();
    }

    @Test
    public void throwExceptionTest() {
        try {
            testInterface.throwException();
        } catch (IOException e) {
            Assert.assertEquals("class=\"ru.fizteh.fivt.students.irinaGoltsman.proxy.utests."
                    + "TestInterfaceImplementation\" name=\"throwException\"><arguments/>"
                    + "<thrown>java.io.IOException: test exception</thrown></invoke>\n",
                    getUsefulPartOfString());
        }
    }

    @Test
    public void sumFunctionTest() {
        testInterface.sum(1, 2);
        Assert.assertEquals("class=\"ru.fizteh.fivt.students.irinaGoltsman.proxy.utests."
                + "TestInterfaceImplementation\" name=\"sum\">"
                + "<arguments><argument>1</argument><argument>2</argument></arguments>"
                + "<return>3</return></invoke>\n",
                getUsefulPartOfString());
    }

    @Test
    public void workWithListAsArgument() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(100);
        testInterface.listAsArgument(list);
        Assert.assertEquals("class=\"ru.fizteh.fivt.students.irinaGoltsman.proxy.utests.TestInterfaceImplementation\" "
                + "name=\"listAsArgument\"><arguments><argument><list><value>1</value>"
                + "<value>100</value></list></argument></arguments></invoke>\n",
                getUsefulPartOfString());
    }

    @Test
    public void listOfListSimpleWork() {
        List<List<?>> list = new ArrayList<>();
        List<?> emptyList = new ArrayList<>();
        list.add(emptyList);
        testInterface.listAsArgument(list);
        Assert.assertEquals("class=\"ru.fizteh.fivt.students.irinaGoltsman.proxy.utests.TestInterfaceImplementation\" "
                + "name=\"listAsArgument\"><arguments><argument><list><value><list/></value></list></argument>"
                + "</arguments></invoke>\n",
                getUsefulPartOfString());
    }

    @Test
    public void cyclicReference() {
        List<List<?>> list = new ArrayList<>();
        list.add(list);
        testInterface.listAsArgument(list);
        Assert.assertEquals("class=\"ru.fizteh.fivt.students.irinaGoltsman.proxy.utests.TestInterfaceImplementation\" "
                + "name=\"listAsArgument\"><arguments><argument><list><value>cyclic"
                + "</value></list></argument></arguments></invoke>\n",
                getUsefulPartOfString());
    }
}
