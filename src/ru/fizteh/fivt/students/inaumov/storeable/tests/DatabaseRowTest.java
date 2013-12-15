package ru.fizteh.fivt.students.inaumov.storeable.tests;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.inaumov.storeable.base.DatabaseRow;

import java.util.ArrayList;
import java.util.List;

public class DatabaseRowTest {
    Storeable storeable = null;

    @Before
    public void setup() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(String.class);
        columnTypes.add(String.class);
        columnTypes.add(Integer.class);

        storeable = new DatabaseRow(columnTypes);
    }

    @After
    public void after() {
        storeable = null;
    }

    @Test(expected = ColumnFormatException.class)
    public void putIncorrectTypeFirstTestShouldFail() {
        storeable.setColumnAt(2, "joke");
    }

    @Test(expected = ColumnFormatException.class)
    public void putIncorrectTypeSecondTestShouldFail() {
        storeable.setColumnAt(1, 666);
    }

    @Test
    public void putCorrectTypesShouldNotFail() {
        storeable.setColumnAt(0, "string1");
        storeable.setColumnAt(1, "string2");
        storeable.setColumnAt(2, 3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void putValueIndexOutOfBoundShouldFail() {
        storeable.setColumnAt(666, 6666);
    }
}
