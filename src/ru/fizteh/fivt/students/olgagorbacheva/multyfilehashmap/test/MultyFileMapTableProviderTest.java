package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap.MultyFileMapTableProvider;
import ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap.MultyFileMapTableProviderFactory;

public class MultyFileMapTableProviderTest {

      static MultyFileMapTableProvider provider;
      
      @BeforeClass
      public static void setUpBeforeClass() throws Exception {
            String dir = System.getProperty("user.dir") + "db";
            new File(dir).mkdir();
            provider = new MultyFileMapTableProvider(dir);
      }
      //

      @Test (expected = IllegalArgumentException.class)
      public void testCreateNullTable() {
            provider.createTable(null);
      }
      
      @Test (expected = RuntimeException.class)
      public void testCreateNonValidTable() {
            provider.createTable("+*_+_+_");
      }
      
      @Test
      public void testCreateTable() {
            provider.createTable("1");
            provider.createTable("2");
            provider.createTable("3");
            provider.createTable("4");
      }
      
      @Test
      public void testGetNotExistedTable() {
            Assert.assertEquals(provider.getTable("5"), null);
      }
      
      @Test
      public void testGetExistedTable() {
            Assert.assertNotNull(provider.getTable("1"));
      }
      
      @Test (expected = IllegalArgumentException.class)
      public void testGetNotValidTable() {
            provider.getTable(null);
      }

      @Test
      public void testSetTable() {
            provider.setTable("2");
            Assert.assertEquals(provider.currentDataBase, provider.getTable("2"));
      }


      @Test
      public void testRemoveTable() {
            provider.removeTable("3");
            Assert.assertEquals(provider.getTable("3"), null);
      }
      
      
}
