package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.olgagorbacheva.filemap.FileMapException;

public class MultyFileMapTableProvider implements TableProvider {

      public static final String TABLE_NAME = "[a-zA-Zа-яА-Я0-9]+";
      private File directory;
      public DataTable currentDataBase = null;
      private Map<String, DataTable> tables = new HashMap<String, DataTable>();

      public MultyFileMapTableProvider(String dir) {
            directory = new File(dir);
            File[] tableList = directory.listFiles();
            if (tableList.length != 0) {
                  for (File f : tableList) {
                        if (f.isDirectory()) {
                              DataTable dataTable = new DataTable(f.getName(), f);
                              tables.put(f.getName(), dataTable);
                        }
                  }
            }
      }

      public void writeToFile() throws FileNotFoundException, IOException {
            Iterator<Map.Entry<String, DataTable>> it = tables.entrySet().iterator();
            while (it.hasNext()) {
                  Entry<String, DataTable> elem = it.next();
                  try {
                        elem.getValue().writeFile();
                  } catch (IOException | FileMapException e) {
                        System.err.println(e.getLocalizedMessage());
                  }
            }
      }

      public Table getTable(String name) {
            if (name == null || name.isEmpty()) {
                  throw new IllegalArgumentException("Недопустимое название таблицы");
            }
            if (!name.matches(TABLE_NAME)) {
                  throw new RuntimeException("Недопустимое имя файла");
            }
            return tables.get(name);
      }

      public Table setTable(String name) {
            if (name == null || name.isEmpty()) {
                  currentDataBase = null;
            }
            if (tables.get(name) == null) {
                  currentDataBase = null;
                  return null;
            }
            currentDataBase = tables.get(name);
            return currentDataBase;
      }

      public Table createTable(String name) {
            if (name == null || name.isEmpty()) {
                  throw new IllegalArgumentException("Недопустимое название таблицы");
            }
            if (!name.matches(TABLE_NAME)) {
                  throw new RuntimeException("Недопустимое имя файла");
            }
            if (tables.get(name) != null) {
                  return null;
            }
            File f = new File(directory, name);
            if (!f.mkdir()) {
                  throw new IllegalArgumentException("Создание директории невозможно");
            }
            DataTable newTable = new DataTable(name, f);
            tables.put(name, newTable);
            return newTable;
      }

      private void deleteFiles(File f) {
            if (f.isDirectory()) {
                  File[] incFiles = f.listFiles();
                  for (File i : incFiles) {
                        deleteFiles(i);
                  }
            }
            f.delete();
      }

      public void removeTable(String name) throws IllegalArgumentException {
            if (name == null || name.isEmpty()) {
                  throw new IllegalArgumentException("Недопустимое название таблицы");
            }
            if (!name.matches(TABLE_NAME)) {
                  throw new IllegalArgumentException("Недопустимое имя файла");
            }
            if (tables.get(name) == null) {
                  throw new IllegalStateException("Данной таблицы не существует");
            }
            deleteFiles(tables.get(name).getWorkingDirectory());
            tables.remove(name);
      }

}