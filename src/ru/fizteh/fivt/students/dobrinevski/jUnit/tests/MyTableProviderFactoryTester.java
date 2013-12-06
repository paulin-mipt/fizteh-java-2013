package ru.fizteh.fivt.students.dobrinevski.jUnit.tests;

import org.junit.*;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dobrinevski.jUnit.MyTableProviderFactory;

public class MyTableProviderFactoryTester {
    public static TableProviderFactory factory;

    @Before
    public void init() {
        factory = new MyTableProviderFactory();
    }

    @Test
    public void legalCreateTest() {
        TableProvider provider = factory.create(System.getProperty("user.dir"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void illegalNullCreateTest() {
        TableProvider provider = factory.create(null);
    }
}
