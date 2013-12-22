package ru.fizteh.fivt.students.dubovpavel.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.strings.ObjectTransformer;
import ru.fizteh.fivt.students.dubovpavel.strings.WrappedMindfulDataBaseMultiFileHashMap;

import java.io.*;
import java.util.ArrayList;

public class TableStoreable extends WrappedMindfulDataBaseMultiFileHashMap<Storeable> implements Table {
    private ArrayList<Class<?>> fields;

    public TableStoreable(File path, Dispatcher dispatcher, ArrayList<Class<?>> types) {
        super(path, dispatcher, new StoreableImplTransformer(types));
        fields = types;
    }

    @Override
    protected void generateLoadingError(String error, String message, boolean acc) throws DataBaseException {
        fields.clear();
        super.generateLoadingError(error, message, acc);
    }

    @Override
    public void open() throws DataBaseException {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(root, "signature.tsv")))) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("EOF reached");
            }
            String[] types = line.split("\\s+");
            if (types.length == 0) {
                throw new IOException("Line is empty");
            }
            fields.clear();
            for (String type : types) {
                Class<?> t = TypesCaster.SUPPORTED_NAMES.get(type);
                if (t == null) {
                    generateLoadingError("DataBaseException",
                            String.format("Signature file contains unsupported type %s", type), false);
                } else {
                    fields.add(t);
                }
            }
            super.open();
        } catch (IOException e) {
            generateLoadingError("IOException",
                    String.format("Can not read signature file: %s", e.getMessage()), false);
        }
    }

    @Override
    public void save() throws DataBaseException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(root, "signature.tsv")))) {
            StringBuilder joiner = new StringBuilder();
            for (Class<?> type : fields) {
                joiner.append(TypesCaster.SUPPORTED_TYPES.get(type));
                joiner.append(' ');
            }
            writer.write(joiner.toString());
            super.save();
        } catch (IOException e) {
            generateLoadingError("IOException", "Can not write signature file", true);
        }
    }

    @Override
    protected void checkPutInput(String key, Storeable value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < fields.size(); i++) {
            try {
                Object cell = value.getColumnAt(i);
                if (cell != null && !cell.getClass().equals(fields.get(i))) {
                    throw new ColumnFormatException(String.format("Type at column %s mismatches", i));
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException("Size of value mismatches signature");
            }
        }
        boolean error;
        try {
            value.getColumnAt(fields.size());
            error = true;
        } catch (IndexOutOfBoundsException e) {
            error = false;
        }
        if (error) {
            throw new ColumnFormatException("Size of value mismtaches signature");
        }
        super.checkPutInput(key, value);
    }

    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        checkPutInput(key, value);
        return super.put(key, value);
    }

    public int getColumnsCount() {
        return fields.size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= fields.size()) {
            throw new IndexOutOfBoundsException("Index is out of bound");
        }
        return fields.get(columnIndex);
    }

    public ObjectTransformer<Storeable> getTransformer() {
        return transformer;
    }
}
