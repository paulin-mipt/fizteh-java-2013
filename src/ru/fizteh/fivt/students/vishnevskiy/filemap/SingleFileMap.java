package ru.fizteh.fivt.students.vishnevskiy.filemap;

import ru.fizteh.fivt.students.vishnevskiy.shell.State;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SingleFileMap extends State {
    private Map<String, String> table = new HashMap<String, String>();
    private File datebase;

    public SingleFileMap(File datebase) {
        try {
            this.datebase = datebase;
            FileReader reader = new FileReader(datebase);
            reader.readFile(table);
        } catch (FileNotFoundException e) {
            // do nothing
        } catch (IOException e) {
            System.err.println("Failed to read datebase file");
            System.exit(1);
        }
    }

    public String get(String key) {
        return table.get(key);
    }

    public String put(String key, String value) {
        String status;
        status = (table.get(key) == null) ? null : table.get(key);
        table.put(key, value);
        return status;
    }

    public int remove(String key) {
        int status;
        status = (table.get(key) == null) ? 0 : 1;
        table.remove(key);
        return status;
    }


    public void write() {
        try {
            FileWriter writer = new FileWriter(datebase);
            writer.writeFile(table);
        } catch (FileNotFoundException e) {
            System.err.println("Datebase file not found");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to rewrite datebase file");
            System.exit(1);
        }

    }

}
