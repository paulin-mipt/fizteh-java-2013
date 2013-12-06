package ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils;

import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.DataTable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FileWriter {
    public void writeDataToFile(File file, DataTable dataTable) {
        try {
            DataOutputStream outStream = new DataOutputStream(new FileOutputStream(file));
            Set<String> keys = dataTable.getKeys();
            List<String> values = new ArrayList<String>(keys.size());
            int intSize = 4;
            int separatorSize = 1;
            int offset = 0;
            for (String s : keys) {
                offset += s.getBytes(StandardCharsets.UTF_8).length + intSize + separatorSize;
            }
            for (String s : keys) {
                byte[] b = s.getBytes(StandardCharsets.UTF_8);
                outStream.write(b);
                outStream.write('\0');
                outStream.writeInt(offset);
                String value = dataTable.get(s);
                values.add(value);
                offset += value.getBytes().length;
            }
            for (String v : values) {
                outStream.write(v.getBytes(StandardCharsets.UTF_8));
            }
            outStream.close();
        } catch (FileNotFoundException e) {
            System.err.println(file.getName() + " was not found");
        } catch (IOException e) {
            System.err.println("mistake in record");
        }

    }
}
