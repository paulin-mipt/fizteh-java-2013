package ru.fizteh.fivt.students.vyatkina.database.goodtest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.vyatkina.database.StorableTable;
import ru.fizteh.fivt.students.vyatkina.database.StorableTableProvider;
import ru.fizteh.fivt.students.vyatkina.database.storable.StorableRow;
import ru.fizteh.fivt.students.vyatkina.database.storable.StorableRowShape;
import ru.fizteh.fivt.students.vyatkina.database.storable.StorableTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CloseTest {
    private StorableTableProviderFactory factory;
    private String location;
    private String TABLE_NAME = "GoodTable";
    private final List<Class<?>> classList;
    private final StorableRow SAMPLE_VALUE;
    private final String KEY = "JustAKey";


    public CloseTest() {
        classList = new ArrayList<>();
        classList.add(String.class);
        classList.add(Integer.class);
        StorableRowShape shape = new StorableRowShape(classList);
        ArrayList<Object> valueList = new ArrayList<>();
        valueList.add(new String("Bla"));
        valueList.add(new Integer(2));
        SAMPLE_VALUE = new StorableRow(shape, valueList);
    }
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init () {
        factory = new StorableTableProviderFactory();
        location = folder.getRoot().toPath().resolve("tableProviderPlace").toString();
    }

    @Test
    public void createTableProviderAndCloseFactory () throws IOException {
        TableProvider tableProvider = factory.create(location);
        factory.close();
        thrown.expect(IllegalStateException.class);
        tableProvider.getTable(TABLE_NAME);
    }

    @Test
    public void createTableProviderAndCloseIt () throws IOException {
        StorableTableProvider tableProvider =  (StorableTableProvider) factory.create(location);
        Table table = tableProvider.createTable(TABLE_NAME,classList);
        tableProvider.close();
        thrown.expect(IllegalStateException.class);
        tableProvider.getTable(TABLE_NAME);

    }

    @Test
    public void closeTableProviderTwiceShouldNotFail () throws IOException {
        StorableTableProvider tableProvider =  (StorableTableProvider) factory.create(location);
        tableProvider.close();
        tableProvider.close();
        factory.close();
    }

    @Test
    public void closeTableFromTable () throws IOException {
        StorableTableProvider tableProvider =  (StorableTableProvider) factory.create(location);
        StorableTable table = (StorableTable) tableProvider.createTable(TABLE_NAME,classList);
        table.close();
        thrown.expect(IllegalStateException.class);
        table.put (null,null);
    }

    @Test
    public void closeTableProviderAndThenTryToUseTable () throws IOException {
        StorableTableProvider tableProvider =  (StorableTableProvider) factory.create(location);
        StorableTable table = (StorableTable) tableProvider.createTable(TABLE_NAME,classList);
        tableProvider.close();
        thrown.expect(IllegalStateException.class);
        table.put (null,null);
    }

    @Test
    public void closeFactoryAndThenUseTable () throws IOException {
        StorableTableProvider tableProvider =  (StorableTableProvider) factory.create(location);
        StorableTable table = (StorableTable) tableProvider.createTable(TABLE_NAME,classList);
        factory.close();
        thrown.expect(IllegalStateException.class);
        table.put (null,null);
    }

    @Test
    public void closeTableAndGetAnotherInTableProvider () throws IOException {
        StorableTableProvider tableProvider =  (StorableTableProvider) factory.create(location);
        StorableTable table = (StorableTable) tableProvider.createTable(TABLE_NAME,classList);
        Assert.assertEquals("In tableProvider should be table", table, tableProvider.getTable(TABLE_NAME));
        table.close();
        Assert.assertNotEquals("In tableProvider should  be an other table", table, tableProvider.getTable(TABLE_NAME));
        Assert.assertNotEquals("In tableProvider should  be an other table", null, tableProvider.getTable(TABLE_NAME));
        Assert.assertEquals("In tableProvider should  be an other table with the same name ",
                TABLE_NAME, tableProvider.getTable(TABLE_NAME).getName());
        factory.close();
    }

    @Test
    public void inNewTableWithTheSameNameShouldNotBeOldNotCommitedValues () throws IOException {
        StorableTableProvider tableProvider =  (StorableTableProvider) factory.create(location);
        StorableTable oldTable = (StorableTable) tableProvider.createTable(TABLE_NAME,classList);
        Assert.assertEquals("In tableProvider should be  table", oldTable, tableProvider.getTable(TABLE_NAME));
        Assert.assertEquals("New value added should be null", null,oldTable.put(KEY,SAMPLE_VALUE));
        oldTable.close();
        StorableTable newTable = (StorableTable) tableProvider.getTable(TABLE_NAME);
        Assert.assertEquals("In new table should not be old values", null, newTable.get(KEY));
        factory.close();
    }

    @Test
    public void inNewTableWithTheSameNameShouldNotBeOldCommitedValues () throws IOException {
        StorableTableProvider tableProvider =  (StorableTableProvider) factory.create(location);
        StorableTable oldTable = (StorableTable) tableProvider.createTable(TABLE_NAME,classList);
        Assert.assertEquals("In tableProvider should be  table", oldTable, tableProvider.getTable(TABLE_NAME));
        Assert.assertEquals("New value added should be null", null,oldTable.put(KEY,SAMPLE_VALUE));
        Assert.assertEquals("Commit one value", 1, oldTable.commit());
        oldTable.close();
        StorableTable newTable = (StorableTable) tableProvider.getTable(TABLE_NAME);
        Assert.assertEquals("In new table should not be old values", SAMPLE_VALUE, newTable.get(KEY));
        factory.close();
    }

}
