package ru.fizteh.fivt.students.elenav.proxy;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.elenav.storeable.StoreableTableProvider;

public class ProxyTest {

    LoggingProxyFactory proxy = new JSONLogger();
    TableProvider provider = null;
    List<Class<?>> list = new ArrayList<>();
    File file = null;
    StringWriter writer = new StringWriter();
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Before
    public void init() throws Exception {
        file = folder.newFolder();
        provider = new StoreableTableProvider(file, System.out);
        list.add(Integer.class);
        list.add(String.class);
        provider = (TableProvider) proxy.wrap(writer, provider, TableProvider.class);        
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void proxyWithNullWriter() {
        proxy.wrap(null, provider, TableProvider.class);
    }    
    
    @Test (expected = IllegalArgumentException.class)
    public void proxyWithNullImplementation() {
        proxy.wrap(writer, null, TableProvider.class);
    }   
    
    @Test (expected = IllegalArgumentException.class)
    public void proxyWithNullInterface() {
        proxy.wrap(writer, provider, null);
    }   
    
    @Test (expected = IllegalArgumentException.class)
    public void proxyNotImplementedInterfaceWriter() {
        proxy.wrap(writer, provider, Table.class);
    }   
    
    @Test (expected = IllegalArgumentException.class)
    public void proxyNotInterface() {
        proxy.wrap(writer, provider, StoreableTableProvider.class);
    } 
    
    @Test
    public void proxyCreateTable() throws IOException {
        provider.createTable("myTable", list);
        JSONObject json = new JSONObject(writer.toString());
        File f = new File(file, "myTable");
        Assert.assertEquals(json.get("class"), 
                "ru.fizteh.fivt.students.elenav.storeable.StoreableTableProvider");
        Assert.assertEquals(json.get("returnValue"), "StoreableTableState[" + f.getAbsolutePath() + "]");
        Assert.assertEquals(json.get("method"), "createTable");
        Assert.assertEquals(json.getJSONArray("arguments").toString(),
                "[\"myTable\",[\"class java.lang.Integer\",\"class java.lang.String\"]]");
        json.getLong("timestamp");
    } 
    
    @Test (expected = IllegalArgumentException.class)
    public void proxyCreateTableNullNull() throws IOException {
        try {
            provider.createTable(null, null);
            
        } catch (IllegalArgumentException e) {
            JSONObject json = new JSONObject(writer.toString());
            File f = new File(file, "myTable");
            Assert.assertEquals(json.get("class"), 
                    "ru.fizteh.fivt.students.elenav.storeable.StoreableTableProvider");
            Assert.assertEquals(json.get("method"), "createTable");
            Assert.assertEquals(json.getJSONArray("arguments").toString(), "[null,null]");
            Assert.assertEquals(json.get("thrown"), e.toString());
            json.getLong("timestamp");
            throw e;
        }
    }
    
    @Test (expected = JSONException.class)
    public void proxyWithVoidReturnValue() throws Exception {
        provider.createTable("myTable", list);
        StringWriter anotherWriter = new StringWriter();
        provider = (TableProvider) proxy.wrap(anotherWriter, provider, TableProvider.class);
        provider.removeTable("myTable");
        JSONObject jsonObject = new JSONObject(anotherWriter.toString());
        jsonObject.get("returnValue");
    }
    
    @Test
    public void proxyHashCode() {
        provider.hashCode();
        Assert.assertEquals(writer.toString(), "");
    }
    
    @Test
    public void proxyEquals() {
        provider.equals(provider);
        Assert.assertEquals(writer.toString(), "");
    }
    
    @Test
    public void proxyToString() {
        provider.toString();
        Assert.assertEquals(writer.toString(), "");
    }
    
}
