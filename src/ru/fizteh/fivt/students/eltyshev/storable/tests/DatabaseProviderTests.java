package ru.fizteh.fivt.students.eltyshev.storable.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseTableProvider;
import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseProviderTests {
    private static final String DATABASE = "C:\\temp\\storeable_test";

    TableProviderFactory factory = new DatabaseTableProviderFactory();
    DatabaseTableProvider provider;

    List<Class<?>> columnTypes = new ArrayList<Class<?>>() {{
        add(Integer.class);
        add(String.class);
    }};

    @Before
    public void setUp() throws Exception {
        provider = (DatabaseTableProvider) factory.create(DATABASE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createProviderEmptyShouldFail() {
        try {
            factory.create("");
        } catch (IOException e) {

        }
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseCreate() throws Exception {
        provider.close();
        provider.createTable("DSADAS", null);
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseCreateFor() throws Exception {
        Table table = provider.createTable("NEWTABLE", columnTypes);
        provider.close();
        provider.createFor(table, new ArrayList<>());
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseCreateForWithoutValues() throws Exception {
        Table table = provider.createTable("NEWTABLE", columnTypes);
        provider.close();
        provider.createFor(table);
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseGetTable() throws Exception {
        provider.close();
        provider.getTable("ASDAS");
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseRemove() throws Exception {
        Table table = provider.createTable("NEWTABLE", columnTypes);
        provider.close();
        provider.removeTable("ASDASD");
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseDeserialize() throws Exception {
        Table table = provider.createTable("NEWTABLE", columnTypes);
        provider.close();
        provider.deserialize(table, getXml(1, "SADA"));
    }

    private String getXml(int value1, String value2) {
        return String.format("<row><col>%d</col><col>%s</col></row>", value1, value2);
    }
}
