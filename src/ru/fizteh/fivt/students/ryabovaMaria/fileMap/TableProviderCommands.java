package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import org.json.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ryabovaMaria.shell.DeleteDir;

public class TableProviderCommands implements TableProvider {
    private final File curDir;
    private ThreadLocal<File> tableDir = new ThreadLocal<File>();
    private ThreadLocal<Table> myTable = new ThreadLocal<Table>();
    private HashMap<String, Table> names;
    private ThreadLocal<List<Class<?>>> types = new ThreadLocal();
    private Lock lock;
    
    TableProviderCommands(File tablesDir) {
        lock = new ReentrantLock(true);
        curDir = tablesDir;
        names = new HashMap<String, Table>();
    }
    
    private void isCorrectArgument(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (name.isEmpty()
            || name.contains("/")
            || name.contains("\\")
            || name.contains(".")
            || name.matches(".*\\s.*")) {
            throw new IllegalArgumentException("argument contains illegal symbols");
        }
        tableDir.set(curDir.toPath().resolve(name).normalize().toFile());
    }
    
    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("bad tablename");
        }
        isCorrectArgument(name);
        lock.lock();
        try {
            if (!tableDir.get().exists()) {
                return null;
            }
            if (!tableDir.get().isDirectory()) {
                throw new IllegalArgumentException(name + " is not a directory");
            }
            myTable.set(names.get(name));
            if (myTable.get() == null) {
                readSignature();
                myTable.set(new TableCommands(tableDir.get(), types.get(), this));
                names.put(name, myTable.get());
            }
            return myTable.get();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        } finally {
            lock.unlock();
        }
    }
    
    private void readSignature() {
        File signature = new File(tableDir.get(), "signature.tsv");
        if (!signature.exists()) {
            throw new IllegalArgumentException("signature.tsv not exists");
        }
        if (!signature.isFile()) {
            throw new IllegalArgumentException("Illegal signature.tsv");
        }
        try (Scanner sign = new Scanner(signature)) {
            String stringTypes = sign.nextLine().trim();
            String[] temp = stringTypes.split("[ ]+");
            types.set(new ArrayList(temp.length));
            for (int i = 0; i < temp.length; ++i) {
                String curType = temp[i];
                switch (curType) {
                    case "int" :
                        types.get().add(Integer.class);
                        break;
                    case "long" :
                        types.get().add(Long.class);
                        break;
                    case "byte" :
                        types.get().add(Byte.class);
                        break;
                    case "float" :
                        types.get().add(Float.class);
                        break;
                    case "double" :
                        types.get().add(Double.class);
                        break;
                    case "boolean" :
                        types.get().add(Boolean.class);
                        break;
                    case "String" :
                        types.get().add(String.class);
                        break;
                    default :
                        throw new IllegalArgumentException("Incorrect signature.tsv");
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal signature.tsv", e);
        }
    }
    
    private void writeSignature() throws IOException {
        File signature = new File(tableDir.get(), "signature.tsv");
        try (FileWriter sign = new FileWriter(signature.toString())) {
            for (int i = 0; i < types.get().size(); ++i) {
                String typeName = types.get().get(i).getSimpleName();
                switch (typeName) {
                    case ("int") :
                    case ("Integer") :
                        typeName = "int";
                        break;
                    case ("long") :
                    case ("Long") :
                        typeName = "long";
                        break;
                    case ("byte") :
                    case ("Byte") :
                        typeName = "byte";
                        break;
                    case ("float") :
                    case ("Float") :
                        typeName = "float";
                        break;
                    case ("double") :
                    case ("Double") :
                        typeName = "double";
                        break;
                    case ("boolean") :
                    case ("Boolean") :
                        typeName = "boolean";
                        break;
                    case ("String") :
                        break;
                    default :
                        throw new IllegalArgumentException("Incorrect type name");
                }
                if (i == types.get().size() - 1) {
                    sign.write(typeName);
                } else {
                    sign.write(typeName + " ");
                }
            }
        }
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("bad tablename");
        }
        isCorrectArgument(name);
        DeleteDir deleteTable = new DeleteDir();
        File table = new File(curDir, name);
        lock.lock();
        try {
            if (!tableDir.get().isDirectory()) {
                throw new IllegalStateException(name + " cannot be deleted");
            }
            deleteTable.delete(table.toPath());
            names.remove(name);
        } catch (Exception e) {
            throw new IllegalStateException(name + " cannot be deleted", e);
        } finally {
            lock.unlock();
        }
    }

    private void isCorrectColumnTypes(ArrayList<Class<?>> columnTypes) {
        if (columnTypes.size() <= 0) {
            throw new IllegalArgumentException("Empty columns type");
        }
        for (int i = 0; i < columnTypes.size(); ++i) {
            if (columnTypes.get(i) == null) {
                throw new IllegalArgumentException("Illegal types");
            }
            try {
                if (!(columnTypes.get(i).equals(Integer.class)
                    || columnTypes.get(i).equals(Byte.class)
                    || columnTypes.get(i).equals(Float.class)
                    || columnTypes.get(i).equals(Double.class)
                    || columnTypes.get(i).equals(Long.class)
                    || columnTypes.get(i).equals(Boolean.class)
                    || columnTypes.get(i).equals(String.class)
                    || columnTypes.get(i).equals(int.class)
                    || columnTypes.get(i).equals(byte.class)
                    || columnTypes.get(i).equals(float.class)
                    || columnTypes.get(i).equals(double.class)
                    || columnTypes.get(i).equals(long.class)
                    || columnTypes.get(i).equals(boolean.class))) {
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("incorrect types", e);
            }
        }
    }
    
    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("bad tablename");
        }
        if (columnTypes == null) {
            throw new IllegalArgumentException("Bad column types");
        }
        types.set(columnTypes);
        isCorrectColumnTypes(new ArrayList(columnTypes));
        isCorrectArgument(name);
        lock.lock();
        try {
            if (tableDir.get().exists()) {
                return null;
            }
            if (!tableDir.get().mkdir()) {
                throw new IllegalArgumentException(name + " cannot be created");
            } else {
                writeSignature();
                myTable.set(new TableCommands(tableDir.get(), columnTypes, this));
                names.put(name, myTable.get());
                return myTable.get();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (table == null) {
            throw new IllegalArgumentException("null table");
        }
        if (value == null) {
            return null;
        }
        ArrayList<Object> values = null;
        ArrayList<Class<?>> tableTypes = null;
        try {    
            values = new ArrayList(table.getColumnsCount());
            tableTypes = new ArrayList(table.getColumnsCount());
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
        JSONArray array = null;
        try {
            array = new JSONArray(value);
        } catch (JSONException e) {
            throw new ParseException(e.getMessage(), 0);
        }
        if (array.length() != table.getColumnsCount()) {
            throw new ParseException("Illegal number of args", 0);
        }
        for (int i = 0; i < array.length(); ++i) {
            Class<?> type = null;
            try {
                type = table.getColumnType(i);
            } catch (Exception e) {
                throw new ParseException(e.getMessage(), 0);
            }
            tableTypes.add(type);
            try {
                String className = array.get(i).getClass().getSimpleName().toString();
                if (className.equals("Null")) {
                    values.add(null);
                } else {
                    switch (type.getSimpleName()) {
                        case("int") :
                        case ("Integer") :
                            Integer currentInt = array.getInt(i);
                            values.add(currentInt);
                            break;
                        case ("long") :
                        case ("Long") :
                            Long currentLong = array.getLong(i);
                            values.add(currentLong);
                            break;
                        case ("byte") :
                        case ("Byte") :
                            Byte currentByte = Byte.valueOf(array.get(i).toString());
                            values.add(currentByte);
                            break;
                        case ("boolean") :
                        case ("Boolean") :
                            Boolean currentBoolean = array.getBoolean(i);
                            values.add(currentBoolean);
                            break;
                        case ("float") :
                        case ("Float") :
                            Float currentFloat = Float.valueOf(array.get(i).toString());
                            values.add(currentFloat);
                            break;
                        case ("double") :
                        case ("Double") :
                            Double currentDouble = array.getDouble(i);
                            values.add(currentDouble);
                            break;
                        case ("String") :
                            String currentString = array.getString(i);
                            values.add(currentString);
                            break;
                        default : 
                            throw new Exception();
                    }
                }
            } catch (Exception e) {
                throw new ParseException("Illegal type of column", i);
            }
        }
        try {
            return new StoreableCommands(values, tableTypes);
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (value == null) {
            return null;
        }
        if (table == null) {
            throw new IllegalArgumentException("incorrect table");
        }
        JSONArray text = null;
        try {
            text = new JSONArray();
        } catch (Exception e) {
            throw new ColumnFormatException(e.getMessage(), e);
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            Class<?> type = null;
            try {
                type = table.getColumnType(i);
            } catch (Exception e) {
                throw new ColumnFormatException(e.getMessage(), e);
            }
            Object current = null;
            try {
                switch (type.getSimpleName().toString()) {
                    case "int" :
                    case "Integer" :
                        current = value.getIntAt(i);
                        break;
                    case "long" :
                    case "Long" :
                        current = value.getLongAt(i);
                        break;
                    case "byte":
                    case "Byte" :
                        current = value.getByteAt(i);
                        break;
                    case "float" :
                    case "Float" :
                        current = value.getFloatAt(i);
                        break;
                    case "double" :
                    case "Double" :
                        current = value.getDoubleAt(i);
                        break;
                    case "boolean" :
                    case "Boolean" :
                        current = value.getBooleanAt(i);
                        break;
                    case "String" :
                        current = value.getStringAt(i);
                        break;
                    default :
                        throw new Exception("illegal value type");
                }
            } catch (Exception e) {
                throw new ColumnFormatException("column " + i + " " + type.getSimpleName().toString(), e);
            }
            try {
                text.put(current);
            } catch (Exception e) {
                throw new ColumnFormatException(e.getMessage(), e);
            }
        }
        int i = table.getColumnsCount();
        try {
            value.getColumnAt(i);
        } catch (Exception e) {
            try {
                return text.toString();
            } catch (Exception ex) {
                throw new ColumnFormatException(ex.getMessage(), ex);
            }
        }
        throw new ColumnFormatException("incorrect number of columns");
    }

    private List<Class<?>> getTypeList(Table table) {
        List<Class<?>> curTypes = new ArrayList();
        int n = table.getColumnsCount();
        for (int i = 0; i < n; ++i) {
            Class curType = table.getColumnType(i);
            curTypes.add(curType);
        }
        return curTypes;
    }
    
    @Override
    public Storeable createFor(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("bad tablename");
        }
        List<Class<?>> curTypes;
        try {
            curTypes = getTypeList(table);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
        return new StoreableCommands(curTypes);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table == null) {
            throw new IllegalArgumentException("bad tablename");
        }
        if (values == null) {
            throw new IllegalArgumentException("bad values");
        }
        List<Class<?>> curTypes;
        curTypes = getTypeList(table);
        return new StoreableCommands((List<Object>) values, curTypes);
    }
}
