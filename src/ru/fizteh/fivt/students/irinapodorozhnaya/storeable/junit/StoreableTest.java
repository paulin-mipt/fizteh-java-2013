package ru.fizteh.fivt.students.irinapodorozhnaya.storeable.junit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.MyStoreable;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.MyTableProviderFactory;

public class StoreableTest {

    private Storeable s;
    public static final String DATA_BASE = "./src/ru/fizteh/fivt/students/irinapodorozhnaya/test";
    private TableProvider provider;
    
    @Before 
    public void setUp() throws Exception {
        List<Class<?>> columnTypes = new ArrayList<>();
        columnTypes.add(Integer.class);
        columnTypes.add(Boolean.class);
        columnTypes.add(String.class);
        columnTypes.add(Float.class);
        columnTypes.add(Double.class);
        columnTypes.add(Byte.class);
        columnTypes.add(Long.class);

        File curDir = new File(DATA_BASE);
        curDir.mkdirs();
        curDir.deleteOnExit();

        provider = new MyTableProviderFactory().create(DATA_BASE);
        
        s = new MyStoreable(provider.createTable("table", columnTypes));               
        s.setColumnAt(0, 1);
        s.setColumnAt(1, false);
        s.setColumnAt(2, "Hello");
        s.setColumnAt(3, 3.0f);
        s.setColumnAt(4, 4.0);
        Byte f = 5;
        s.setColumnAt(5, f);
        s.setColumnAt(6, 6L);
    }
    
    @After 
    public void tearDown() throws Exception {
        provider.removeTable("table");
    }
    
    @Test
    public void setColumnAt() throws Exception {
        s.setColumnAt(2, "Hello");
        s.setColumnAt(4, 4.0);
    }

    @Test
    public void toStringTest() {
        Assert.assertEquals(s.toString(), "MyStoreable[1,false,Hello,3.0,4.0,5,6]");
    }
    
    @Test (expected = IndexOutOfBoundsException.class)
    public void setAtIncorrectColumn() throws Exception {
        s.setColumnAt(9, "Hello");
    }
    
    @Test (expected = ColumnFormatException.class)
    public void setAtIncorrectColumnType() throws Exception {
        s.setColumnAt(1, "Hello");
    }
    
    @Test
    public void getColumnCorrect() throws Exception {     
        Assert.assertEquals(s.getColumnAt(4), 4.0);
    }
    
    @Test (expected = IndexOutOfBoundsException.class)
    public void getAtIncorrectColumn() throws Exception {
        s.getColumnAt(9);
    }

    @Test
    public void getFloatColumnCorrect() throws Exception {
        Float f = 3.0f;
        Assert.assertEquals(s.getFloatAt(3), f);
    }

    @Test (expected = ColumnFormatException.class)
    public void getFloatColumnIncorrect() throws Exception {
        s.getFloatAt(0);
    }
    
    @Test
    public void getIntColumnCorrect() throws Exception {
        Integer f = 1;
        Assert.assertEquals(s.getIntAt(0), f);
    }

    @Test (expected = ColumnFormatException.class)
    public void getIntColumnIncorrect() throws Exception {
        s.getIntAt(2);
    }

    @Test
    public void getBooleanColumnCorrect() throws Exception {
        Boolean f = false;
        Assert.assertEquals(s.getBooleanAt(1), f);
    }

    @Test (expected = ColumnFormatException.class)
    public void getBooleanColumnIncorrect() throws Exception {
        s.getFloatAt(0);
    }
    
    @Test
    public void getStringColumnCorrect() throws Exception {
        String f = "Hello";
        Assert.assertEquals(s.getStringAt(2), f);
    }

    @Test (expected = ColumnFormatException.class)
    public void getStringColumnIncorrect() throws Exception {
        s.getStringAt(0);
    }
    
    @Test
    public void getDoubleColumnCorrect() throws Exception {
        Double f = 4.0;
        Assert.assertEquals(s.getDoubleAt(4), f);
    }

    @Test (expected = ColumnFormatException.class)
    public void getDoubleColumnIncorrect() throws Exception {
        s.getDoubleAt(0);
    }
    
    @Test
    public void getByteColumnCorrect() throws Exception {
        Byte f = 5;
        Assert.assertEquals(s.getByteAt(5), f);
    }

    @Test (expected = ColumnFormatException.class)
    public void getByteColumnIncorrect() throws Exception {
        s.getFloatAt(0);
    }
    
    @Test
    public void getLongColumnCorrect() throws Exception {
        Long f = 6L;
        Assert.assertEquals(s.getLongAt(6), f);
    }

    @Test (expected = ColumnFormatException.class)
    public void getLongColumnIncorrect() throws Exception {
        s.getFloatAt(0);
    }
}
