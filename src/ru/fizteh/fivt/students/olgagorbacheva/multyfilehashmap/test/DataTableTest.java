package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap.DataTable;

public class DataTableTest {

      static DataTable test;
      
      @BeforeClass
      public static void setUpBeforeClass() throws Exception {
            String dir = System.getProperty("user.dir");
            new File(dir, "db").mkdir();
            test = new DataTable("testing", new File(dir, "db"));
      }
     

      @Test
      public void testGetName() throws Exception {
          Assert.assertEquals(test.getName(), "testing");
      }
      
      @Test (expected = IllegalArgumentException.class)
      public void testNonValidPut() {
            test.put(null, null);
      }

      @Test
      public void testValidPut() {
            Assert.assertNull(test.put("1", "10"));
            Assert.assertEquals(test.put("1", "20"), "10");
      }
      
      @Test
      public void testRemove() {
            Assert.assertEquals(test.remove("1"), "20");
            Assert.assertEquals(test.remove("5"), null);
      }

      @Test
      public void testSize() {
            Assert.assertEquals(test.size(), 0);
            test.put("2", "20");
            test.put("3", "30");
            test.put("4", "40");
            test.put("5", "50");
            Assert.assertEquals(test.size(), 4);
            
      }

      @Test
      public void testCommit() {
            Assert.assertEquals(test.commit(), 4);
      }

      @Test
      public void testSizeChangesCommit() {
            Assert.assertEquals(test.sizeChangesCommit(), 0);
      }

      @Test
      public void testRollback() {
            test.put("2", "30");
            test.put("3", "40");
            test.put("4", "50");
            test.put("5", "60");
            test.remove("5");
            Assert.assertEquals(test.rollback(), 4);
      }

      @Test
      public void testWrite() throws Exception {
            test.writeFile();
      }
      
      @Test
      public void testRead() throws Exception {
            test.readFile();
      }
      
      
      @Test
      public void testGet() throws Exception {
          Assert.assertEquals(test.get("2"), "20");
      }
      
      @Test (expected = IllegalArgumentException.class)
      public void testNonValidRemove() {
            test.remove(null);
      }
      
}
