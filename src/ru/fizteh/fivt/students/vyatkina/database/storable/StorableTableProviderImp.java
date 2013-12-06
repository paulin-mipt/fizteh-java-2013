package ru.fizteh.fivt.students.vyatkina.database.storable;

import org.json.JSONArray;
import org.json.JSONException;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;
import ru.fizteh.fivt.students.vyatkina.database.StorableTable;
import ru.fizteh.fivt.students.vyatkina.database.StorableTableProvider;
import ru.fizteh.fivt.students.vyatkina.database.logging.CloseState;
import ru.fizteh.fivt.students.vyatkina.database.superior.Type;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderChecker.storableForThisTableCheck;
import static ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderChecker.validTableNameCheck;
import static ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderUtils.*;


public class StorableTableProviderImp implements StorableTableProvider {

    private volatile Map<String, StorableTable> tables = new HashMap<>();
    private final Path location;
    final ReadWriteLock databaseKeeper = new ReentrantReadWriteLock(true);
    private final CloseState closeState;

    public StorableTableProviderImp(Path location) {
        this.location = location.toAbsolutePath();
        this.closeState = new CloseState(this + " is closed");
    }

    @Override
    public Table getTable(String name) {
        closeState.isClosedCheck();
        validTableNameCheck(name);
        databaseKeeper.writeLock().lock();
        try {
            loadTable(name);
            return tables.get(name);
        }
        finally {
            databaseKeeper.writeLock().unlock();
        }

    }

    private void loadTable(String tableName) {
        if (tables.containsKey(tableName)) {
            return;
        }
        Path tableDirectory = tableDirectory(tableName);
        if (!Files.exists(tableDirectory)) {
            return;
        }
        Path tableSignature = tableDirectory.resolve(SIGNATURE_FILE);
        if (Files.exists(tableSignature)) {
            try {
                StorableRowShape shape = new StorableRowShape(readTableSignature(tableSignature));
                StorableTable table = new StorableTableImp2(tableName, shape, this);

                Map<String, String> diskValues = getTableFromDisk(tableDirectory.toFile());
                Map<String, Storeable> deserializedDiskValues = new HashMap<>();
                for (Map.Entry<String, String> entry : diskValues.entrySet()) {
                    deserializedDiskValues.put(entry.getKey(), deserialize(table, entry.getValue()));
                }
                table.putValuesFromDisk(deserializedDiskValues);
                tables.put(table.getName(), table);
            }
            catch (IOException | ParseException e) {
                throw new WrappedIOException();
            }
        } else {
            throw new WrappedIOException("Bad database: table without signature file");
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        closeState.isClosedCheck();
        validTableNameCheck(name);
        databaseKeeper.writeLock().lock();
        try {
            loadTable(name);
            getTable(name);
            if (tables.containsKey(name)) {
                return null;
            }
            StorableRowShape shape = new StorableRowShape(columnTypes);
            StorableTable table = new StorableTableImp2(name, shape, this);
            table.putValuesFromDisk(new HashMap<String, Storeable>());

            tables.put(name, table);
            Files.createDirectories(location.resolve(name));
            writeTableSignature(tableDirectory(name), columnTypes);
            return table;
        }
        finally {
            databaseKeeper.writeLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        closeState.isClosedCheck();
        validTableNameCheck(name);
        databaseKeeper.writeLock().lock();
        try {
            loadTable(name);
            if (!tables.containsKey(name)) {
                throw new IllegalStateException();
            }
            deleteTableFromDisk(tableDirectory(name).toFile());
            tables.remove(name);
        }
        finally {
            databaseKeeper.writeLock().unlock();
        }
    }

    void removeOldReference(Table table)throws IOException{
        try {
            databaseKeeper.writeLock().lock();
            tables.remove(table.getName());
        }
        finally {
            databaseKeeper.writeLock().unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        closeState.isClosedCheck();
        try {
            JSONArray jsonArray = new JSONArray(value);
            Storeable result = createFor(table);

            for (int i = 0; i < table.getColumnsCount(); i++) {

                Class<?> valueClass = table.getColumnType(i);
                if (valueClass.equals(Integer.class)) {
                    result.setColumnAt(i, jsonArray.getInt(i));
                } else if (valueClass.equals(Long.class)) {
                    result.setColumnAt(i, jsonArray.getLong(i));
                } else if (valueClass.equals(Byte.class)) {
                    Object o = jsonArray.get(i);
                    if (!o.toString().equals("null")) {
                        result.setColumnAt(i, Byte.parseByte(o.toString()));
                    }
                } else if (valueClass.equals(Double.class)) {
                    result.setColumnAt(i, jsonArray.getDouble(i));
                } else if (valueClass.equals(Float.class)) {
                    Object o = jsonArray.get(i);
                    if (!o.toString().equals("null")) {
                        result.setColumnAt(i, Float.parseFloat(o.toString()));
                    }
                } else if (valueClass.equals(Boolean.class)) {
                    result.setColumnAt(i, (jsonArray.getBoolean(i)));
                } else if (valueClass.equals(String.class)) {
                    result.setColumnAt(i, jsonArray.getString(i));
                } else {
                    throw new ColumnFormatException(UNEXPECTED_CLASS_IN_STORABLE + EXPECTED + table.getColumnType(i)
                            + BUT_HAVE + valueClass);
                }
            }
            return result;
        }
        catch (JSONException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        closeState.isClosedCheck();
        storableForThisTableCheck(table, value);
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < table.getColumnsCount(); i++) {
            Class<?> valueClass = table.getColumnType(i);
            if (valueClass.equals(Integer.class)) {
                jsonArray.put(i, value.getIntAt(i));
            } else if (valueClass.equals(Long.class)) {
                jsonArray.put(i, (value.getLongAt(i)));
            } else if (valueClass.equals(Byte.class)) {
                jsonArray.put(i, (value.getByteAt(i)));
            } else if (valueClass.equals(Double.class)) {
                jsonArray.put(i, (value.getDoubleAt(i)));
            } else if (valueClass.equals(Float.class)) {
                jsonArray.put(i, (value.getFloatAt(i)));
            } else if (valueClass.equals(Boolean.class)) {
                jsonArray.put(i, (value.getBooleanAt(i)));
            } else if (valueClass.equals(String.class)) {
                jsonArray.put(i, (value.getStringAt(i)));
            } else {
                throw new ColumnFormatException(UNEXPECTED_CLASS_IN_STORABLE + EXPECTED + table.getColumnType(i)
                        + BUT_HAVE + valueClass);
            }
        }
        return jsonArray.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        closeState.isClosedCheck();
        List<Class<?>> columnTypes = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            columnTypes.add(table.getColumnType(i));
        }
        return new StorableRow(new StorableRowShape(columnTypes));
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        closeState.isClosedCheck();
        List<Class<?>> columnTypes = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            columnTypes.add(table.getColumnType(i));
        }
        StorableRow row = new StorableRow(new StorableRowShape(columnTypes));
        row.fillWith(values);
        return row;
    }

    @Override
    public void saveChangesOnExit() {
        closeState.isClosedCheck();
        try {
            databaseKeeper.writeLock().lock();
            for (StorableTable table : tables.values()) {
                table.commit();
            }
        }
        catch (IOException e) {
            throw new WrappedIOException(e);
        }
        finally {
            databaseKeeper.writeLock().unlock();
        }
    }

    Path tableDirectory(String name) {
        return location.resolve(name);
    }

    @Override
    public List<Class<?>> parseStructedSignature(String structedSignature) {
        closeState.isClosedCheck();
        if (structedSignature.trim().isEmpty()) {
            throw new IllegalArgumentException("wrong type (empty)");
        }
        String[] classNames = structedSignature.split("\\s+");
        List<Class<?>> classes = new ArrayList<>();
        for (String className : classNames) {
            if (Type.BY_SHORT_NAME.containsKey(className)) {
                classes.add(Type.BY_SHORT_NAME.get(className));
            } else {
                throw new IllegalArgumentException("wrong type (" + className + ")");
            }
        }
        return classes;
    }

    @Override
    public void close() throws IOException {
        if (closeState.isAlreadyClosed()) {
            return;
        }
        try {
            databaseKeeper.writeLock().lock();
            for (StorableTable table : tables.values()) {
                table.close();
            }
            closeState.close();
        }
        finally {
            databaseKeeper.writeLock().unlock();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + location + "]";
    }
}
