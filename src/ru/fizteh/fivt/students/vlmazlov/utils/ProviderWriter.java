package ru.fizteh.fivt.students.vlmazlov.utils;

import ru.fizteh.fivt.students.vlmazlov.generics.GenericTable;
import ru.fizteh.fivt.students.vlmazlov.generics.GenericTableProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProviderWriter {
    private static final int FILES_QUANTITY = 16;
    private static final int DIRECTORIES_QUANTITY = 16;

    private static <V, T extends GenericTable<V>> void splitTable(ArrayList<ArrayList<T>> tableParts, T table)
            throws ValidityCheckFailedException {
        for (Map.Entry<String, V> entry : table) {

            ValidityChecker.checkTableKey(entry.getKey());

            tableParts.get(Math.abs(entry.getKey().getBytes()[0]) % DIRECTORIES_QUANTITY).get(
                Math.abs(entry.getKey().getBytes()[0]) / FILES_QUANTITY % FILES_QUANTITY).put(
                entry.getKey(), entry.getValue());
        }

        /*
        Only pushed changes will be written to the disc.
        The parts are created in writeMultiTable exclusively for each thread;
        therefore, synchronization is unnecessary
        */

        for (int i = 0; i < tableParts.size(); ++i) {
            for (int j = 0; j < tableParts.get(i).size(); ++j) {
                tableParts.get(i).get(j).pushChanges();
            }
        }

    }

    public static <V, T extends GenericTable<V>> void writeMultiTable(
        T table, File root, GenericTableProvider<V, T> provider)
            throws IOException, ValidityCheckFailedException {

        ArrayList<ArrayList<T>> tableParts =
                new ArrayList<ArrayList<T>>(DIRECTORIES_QUANTITY);

        for (int i = 0; i < DIRECTORIES_QUANTITY; ++i) {
            tableParts.add(i, new ArrayList<T>(FILES_QUANTITY));
            for (int j = 0; j < FILES_QUANTITY; ++j) {
                tableParts.get(i).add(j, (T) table.clone());
            }
        }

        //  System.out.println(tableParts.size());

        splitTable(tableParts, table);

        for (int i = 0; i < DIRECTORIES_QUANTITY; ++i) {
            File directory = new File(root, i + ".dir");

            if (!directory.exists()) {

                if (!directory.mkdir()) {
                    throw new IOException("Unable to create directory " + directory.getName());
                }
            }

            for (int j = 0; j < FILES_QUANTITY; ++j) {
                TableWriter.writeTable(directory, new File(directory, j + ".dat"), tableParts.get(i).get(j), provider);
            }
        }

        dumpGarbage(root);
    }

    private static void dumpGarbage(File root) {
        for (File directory : root.listFiles()) {
            if (!directory.isDirectory()) {
                continue;
            }
            for (File file : directory.listFiles()) {
                if (file.length() == 0) {
                    file.delete();
                }
            }

            if (directory.listFiles().length == 0) {
                directory.delete();
            }
        }
    }

    public static <V, T extends GenericTable<V>> Map<T, File> writeProvider(GenericTableProvider<V, T> provider)
            throws IOException, ValidityCheckFailedException {
        ValidityChecker.checkMultiTableDataBaseRoot(provider.getRoot());

        Map<T, File> tableDirMap = new HashMap<T, File>();
        File rootFile = new File(provider.getRoot());

        for (File entry : rootFile.listFiles()) {

            T curTable = provider.getTable(entry.getName());

            if (curTable == null) {
                throw new IOException(entry.getName() + " doesn't match any database");
            }

            curTable.checkRoot(entry);

            //Autocommit is performed before writing
            curTable.commit();
        }

        return tableDirMap;
    }
}

