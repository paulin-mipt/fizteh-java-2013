package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.anastasyev.shell.Command;

import java.io.IOException;
import java.util.ArrayList;

public class CreateCommand implements Command<FileMapTableProvider> {
    private ArrayList<Class<?>> parseTypes(String allTypes, FileMapTableProvider provider) throws IOException {
        ArrayList<Class<?>> columnTypes = new ArrayList<Class<?>>();
        String[] types = allTypes.split("\\s+");
        for (String type : types) {
            Class<?> classType = provider.getClassName(type);
            if (classType == null) {
                throw new IOException("Wrong column format");
            }
            columnTypes.add(classType);
        }
        return columnTypes;
    }

    @Override
    public boolean exec(FileMapTableProvider provider, String[] command) {
        if (command.length == 1) {
            System.err.println("wrong type (table name expected)");
            return false;
        }
        if (command.length == 2) {
            System.err.println("wrong type (table columns types expected)");
            return false;
        }
        try {
            if (command[1].startsWith("(") && !command[2].startsWith("(")) {
                System.err.println("wrong type (table name expected)");
                return false;
            }
            ArrayList<Class<?>> classes = parseTypes(command[2].substring(1, command[2].length() - 1), provider);
            Table res = provider.createTable(command[1], classes);
            if (res == null) {
                System.out.println(command[1] + " exists");
            } else {
                System.out.println("created");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("wrong type (" + e.getMessage() + ")");
            return false;
        } catch (RuntimeException e) {
            System.err.println("wrong type (Bad symbol in name)");
            return false;
        } catch (IOException e) {
            System.err.println("wrong type (" + e.getMessage() + ")");
            return false;
        }
        return true;
    }

    @Override
    public String commandName() {
        return "create";
    }
}
