package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class TestsForProxy {
    TableProviderFactory tableProviderFactory;
    TableProvider tableProvider;
    Table table;
    
    LoggingProxyFactory loggingProxyFactory;
    
    Storeable value1;
    
    StringWriter writer;
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Before
    public void createTable() throws IOException {
        writer = new StringWriter();
        loggingProxyFactory = new LoggingProxyFactoryImplementation();
    }
    
    @Test
    public void arrayListCyclic() {
        ArrayList<Object> array = new ArrayList<Object>();
        array.add(array);
        
        List<Object> wrappedList = (List<Object>) loggingProxyFactory.wrap(writer, array, List.class); 
        wrappedList.addAll(array);
        
        JSONObject parsed = new JSONObject(writer.toString());
        Assert.assertEquals(parsed.getJSONArray("arguments").getJSONArray(0).getString(0), "cyclic");
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void arrayListInvalidIndex() {
        ArrayList<Integer> array = new ArrayList<Integer>();
        array.add(1);
        
        List<Integer> wrappedList = (List<Integer>) loggingProxyFactory.wrap(writer, array, List.class); 
        wrappedList.get(2);
        
        JSONObject parsed = new JSONObject(writer.toString());
        Assert.assertTrue(parsed.getJSONArray("thrown").getString(0).startsWith("java.lang.IndexOutOfBoundsException"));
    }
    
    @Test
    public void intArrayIsNotIterable() {
        ArrayList<Object> array = new ArrayList<Object>();
        array.add(5);
        
        List<Object> wrappedList = (List<Object>) loggingProxyFactory.wrap(writer, array, List.class);
        
        wrappedList.indexOf(new int[0]);
        
        JSONObject parsed = new JSONObject(writer.toString());
        Assert.assertTrue(parsed.getJSONArray("arguments").getString(0).startsWith("[I"));
    }
    
    @Test
    public void primitiveTypes() {
        ArrayList<Object> array = new ArrayList<Object>();
        int a = 5;
        
        List<Object> wrappedList = (List<Object>) loggingProxyFactory.wrap(writer, array, List.class);
        wrappedList.add(a);
        
        JSONObject parsed = new JSONObject(writer.toString());
        Assert.assertTrue(parsed.getJSONArray("arguments").getInt(0) == a);
    }
    
    @Test
    public void newLine() {
        ArrayList<Object> array = new ArrayList<Object>();
        int a = 5;
        
        List<Object> wrappedList = (List<Object>) loggingProxyFactory.wrap(writer, array, List.class);
        wrappedList.add(a);
        wrappedList.add(a);

        Assert.assertTrue(writer.toString().matches(".*" + System.lineSeparator() + ".*" + System.lineSeparator()));
    }
    
    @Test
    public void voidNoReturnValue() {
        ArrayList<Object> array = new ArrayList<Object>();
        
        List<Object> wrappedList = (List<Object>) loggingProxyFactory.wrap(writer, array, List.class);
        wrappedList.clear();
        
        Assert.assertTrue(!writer.toString().contains("returnValue"));
    }
    
    @Test
    public void databaseTest() throws IOException {
        
        File databaseDirectory = folder.newFolder("database");
        TableProviderFactoryImplementation tableProviderFactoryImpl = new TableProviderFactoryImplementation();
        tableProviderFactory = (TableProviderFactory) loggingProxyFactory
                .wrap(writer, tableProviderFactoryImpl, TableProviderFactory.class);
        TableProvider tableProviderImplementation = tableProviderFactory.create(databaseDirectory.toString());
        
        JSONObject parsed = new JSONObject(writer.toString());
        Assert.assertTrue(parsed.get("returnValue")
                .equals(String.format("TableProviderImplementation[%s]", databaseDirectory.toString())));
        
        writer = new StringWriter();
        
        TableProvider tableProvider = (TableProvider) loggingProxyFactory
                .wrap(writer, tableProviderImplementation, TableProvider.class);
        
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Boolean.class);
        columnTypes.add(String.class);
        columnTypes.add(Integer.class);
        
        table = tableProvider.createTable("tableName", columnTypes);
        
        parsed = new JSONObject(writer.toString());
        Assert.assertTrue(parsed.get("returnValue").equals(String.format("TableImplementation[%s]",
                databaseDirectory.toPath().resolve("tableName").toString())));
    }
}
