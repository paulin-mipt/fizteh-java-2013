package ru.fizteh.fivt.students.msandrikova.proxy;

import static org.junit.Assert.*;

import java.io.File;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;
import ru.fizteh.fivt.students.msandrikova.storeable.ChangesCountingTable;
import ru.fizteh.fivt.students.msandrikova.storeable.ChangesCountingTableProvider;
import ru.fizteh.fivt.students.msandrikova.storeable.StoreableTableProvider;

public class LoggingProxyFactoryImplementationTest {
    private static LoggingProxyFactory factory;
    private ChangesCountingTable table;
    private File path;
    private ChangesCountingTableProvider tableProvider;
    private StringWriter writer;
    private Object proxy;
    
    private String deleteTimeStamp(String s) {
        String answer = s.substring(8);
        answer = answer.substring(answer.indexOf(" "));
        answer =  "<invoke" + answer;
        return answer;
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        factory = new LoggingProxyFactoryImplementation();
        
    }
    
    @After
    public void clear() {
        if (path.exists()) {
            try {
                Utils.remover(path, "test", false);
            } catch (Exception e) {
                System.err.println("Can not remove something");
            }
        }
    }
    
    @Before
    public void setUp() throws Exception {
        writer = new StringWriter();
        path = new File(System.getProperty("user.home"), "sandbox");
        clear();
        path.mkdirs();
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        columnTypes.add(Boolean.class);
        columnTypes.add(String.class);
        tableProvider = new StoreableTableProvider(path);
        table = tableProvider.createTable("tableName", columnTypes);
    }

    @Test
    public void testTableSize() {
        proxy = factory.wrap(writer, this.table, Table.class);
        ((Table) proxy).size();
        assertEquals("<invoke class=\"ru.fizteh.fivt.students.msandrikova.storeable.StoreableTable\""
                + " name=\"size\">"
                + "<arguments></arguments><return>0</return></invoke>\n",
                this.deleteTimeStamp(writer.toString()));
    }
    
    @Test
    public void testTableGetStoreable() throws ParseException {
         Storeable value = null;
         Storeable getValue = null;
         value = tableProvider.deserialize(table, "[1 , true, \" value 1 \"]");
         table.put("key", value);
         proxy = factory.wrap(writer, this.table, Table.class);
         getValue = ((Table) proxy).get("key");
         assertEquals(value, getValue);
         assertEquals("<invoke class=\"ru.fizteh.fivt.students.msandrikova.storeable.StoreableTable\""
                 + " name=\"get\">"
                    + "<arguments><argument>key</argument></arguments><return>TableRow[1,true, value 1 ]</return>"
                    + "</invoke>\n",
                    this.deleteTimeStamp(writer.toString()));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullThrownInXMLAndException() {
        proxy = factory.wrap(writer, this.tableProvider, TableProvider.class);
        try {
            ((TableProvider) proxy).getTable(null);
        } catch (IllegalArgumentException e) {
            assertEquals("<invoke class=\"ru.fizteh.fivt.students.msandrikova.storeable.StoreableTableProvider\""
                    + " name=\"getTable\">"
                    + "<arguments><argument><null></null></argument></arguments>"
                    + "<thrown>java.lang.IllegalArgumentException: Table name can not be null "
                    + "or empty or contain bad symbols</thrown></invoke>\n",
                    this.deleteTimeStamp(writer.toString()));
            throw e;
        }
    }
    
    @Test
    public void testCyclicListAndList() {
        List<Object> list = new ArrayList<Object>();
        list.add(list);
        proxy = factory.wrap(writer, list, List.class);
        assertEquals(0, ((List<?>) proxy).indexOf(list));
        assertEquals("<invoke class=\"java.util.ArrayList\" name=\"indexOf\">"
                + "<arguments><argument><list><value>cyclic</value></list>"
                + "</argument></arguments><return>0</return></invoke>\n",
                this.deleteTimeStamp(writer.toString()));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPrimitive() {
        proxy = factory.wrap(writer, 20, short.class);
    }

}
