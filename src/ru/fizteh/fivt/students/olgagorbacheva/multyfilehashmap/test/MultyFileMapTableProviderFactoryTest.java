package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap.MultyFileMapTableProviderFactory;

public class MultyFileMapTableProviderFactoryTest {

      static MultyFileMapTableProviderFactory factory;
      
      @BeforeClass
      public static void setUpBeforeClass() throws Exception {
            factory = new MultyFileMapTableProviderFactory();
      }

      @Test (expected = IllegalArgumentException.class)
      public void createTableProviderInBadDirectory() {
            factory.create("/dev");
      }
      
      @Test (expected = IllegalArgumentException.class)
      public void createTableProviderInNotExistingDirectory() {
            factory.create("/home/olga/ololo");
      }
      
      @Test (expected = IllegalArgumentException.class)
      public void createNullNameTableProvider() {
            factory.create(null);
      }
      
      @Test (expected = IllegalArgumentException.class)
      public void createNlNameTableProvider() {
            factory.create("");
      }
      
      @Test (expected = IllegalArgumentException.class)
      public void createNlNameTableProvider2() {
            factory.create("              ");
      }
      
      @Test
      public void createTableProvider() {
            String dir = System.getProperty("user.dir");
            new File(dir, "db").mkdir();
            factory.create(dir);
      }

      

}
