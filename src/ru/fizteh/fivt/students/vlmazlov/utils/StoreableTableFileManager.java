package ru.fizteh.fivt.students.vlmazlov.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;
import javax.activation.UnsupportedDataTypeException;

import ru.fizteh.fivt.storage.structured.Storeable;

import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableTable;
import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableTableProvider;

public class StoreableTableFileManager {

    private static final int FILES_QUANTITY = 16;
    private static final int DIRECTORIES_QUANTITY = 16;
    
    public static File getFileForKey(String key, StoreableTable table, StoreableTableProvider provider) {
        String dirName = Math.abs(key.getBytes()[0]) % DIRECTORIES_QUANTITY + ".dir";
        String fileName = Math.abs(key.getBytes()[0]) / FILES_QUANTITY % FILES_QUANTITY + ".dat";

        File tableDir = getTableDir(table, provider);
        File directory = new File(tableDir, dirName);
        File file = new File(directory, fileName);

        return file;
    }

    private static File getTableDir(StoreableTable table, StoreableTableProvider provider) {
        return new File(provider.getRoot(), table.getName());
    }    

    public static int getDirNum(String key) {
        return Math.abs(key.getBytes()[0]) % DIRECTORIES_QUANTITY;
    }

    public static int getFileNum(String key) {
        return Math.abs(key.getBytes()[0]) / FILES_QUANTITY % FILES_QUANTITY;
    }

    public static void writeSize(StoreableTable table, StoreableTableProvider provider) throws IOException {

        File tableDir = getTableDir(table, provider);
        File sizeFile = new File(tableDir, "size.tsv");

        sizeFile.createNewFile();

        PrintWriter writer = new PrintWriter(sizeFile);

        try {
            writer.print(table.size());
        } finally {
            QuietCloser.closeQuietly(writer);
        }
    }

       public static void writeSignature(StoreableTable table, StoreableTableProvider provider) throws IOException {

        File tableDir = getTableDir(table, provider);
        File signatureFile = new File(tableDir, "signature.tsv");

        signatureFile.createNewFile();

        PrintWriter writer = new PrintWriter(signatureFile);

        try {
            for (int i = 0;i < table.getColumnsCount();++i) {
                Class<?> clazz = table.getColumnType(i);
                writer.print(TypeName.getNameByClass(clazz) + " ");
            }
        } finally {
            QuietCloser.closeQuietly(writer);
        }
    }

    public static List<Class<?>> getTableSignature(String name, StoreableTableProvider provider)
    throws ValidityCheckFailedException, IOException {

        File tableDir = new File(provider.getRoot(), name);
        
        ValidityChecker.checkMultiStoreableTableRoot(tableDir);

        File signatureFile = new File(tableDir, "signature.tsv");
        List<Class<?>> signature = new ArrayList<Class<?>>();
        Scanner scanner = new Scanner(signatureFile);

        try {
            while (scanner.hasNext()) {
                String type = scanner.next();
                Class<?> columnType = TypeName.getClassByName(type.trim());

                if (columnType == null) {
                    throw new UnsupportedDataTypeException("Unsupported column type: " + type);
                }

                signature.add(columnType);
            }

            ValidityChecker.checkStoreableTableSignature(signature);

            return signature;
        } finally {
            scanner.close();
        }
    }

    public static int getTableSize(String name, StoreableTableProvider provider)
    throws ValidityCheckFailedException, IOException {

        File tableDir = new File(provider.getRoot(), name);  

        ValidityChecker.checkMultiStoreableTableRoot(tableDir);

        File sizeFile = new File(tableDir, "size.tsv");
        
        if (!sizeFile.exists()) {
            return 0;
        }

        ValidityChecker.checkTableSize(sizeFile);

        Scanner scanner = new Scanner(sizeFile);

        try {
            return scanner.nextInt();
        } finally {
            scanner.close();
        }
    }

    public static Storeable readSingleKey(String key,
        StoreableTable table, StoreableTableProvider provider) 
    throws IOException, ValidityCheckFailedException, ParseException {

        File fileForKey = getFileForKey(key, table, provider);

        if (!fileForKey.exists()) {
            return null;
        }

        String currentKey = null;
        StoreableTableFileReader reader = new StoreableTableFileReader(fileForKey);

        try {
            do {
                currentKey = reader.nextKey();

                if (key.equals(currentKey)) {
                    return provider.deserialize(table, reader.getCurrentSerializedValue());
                }

            } while (currentKey != null);

            return null;
        } finally {
            QuietCloser.closeQuietly(reader);
        }
    }

    public static void modifyMultipleFiles(Map<String, Storeable> changed, Set<String> deleted,
        StoreableTable table, StoreableTableProvider provider) throws IOException, ValidityCheckFailedException {

        Map<String, String>[][] changedInFile = new HashMap[DIRECTORIES_QUANTITY][FILES_QUANTITY];
        Set<String>[][] deletedInFile = new HashSet[DIRECTORIES_QUANTITY][FILES_QUANTITY];

        for (int i = 0;i < DIRECTORIES_QUANTITY;++i) {
            for (int j = 0;j < FILES_QUANTITY;++j) {
                changedInFile[i][j] = new HashMap();
                deletedInFile[i][j] = new HashSet();
            }
        }

        for (Map.Entry<String, Storeable> entry : changed.entrySet()) {
            String key = entry.getKey();

            changedInFile[getDirNum(key)][getFileNum(key)].put(key, provider.serialize(table, entry.getValue()));
        }   

        for (String key : deleted) {
            deletedInFile[getDirNum(key)][getFileNum(key)].add(key);
        }

        File tableDir = getTableDir(table, provider);

        for (int i = 0;i < DIRECTORIES_QUANTITY;++i) {
            for (int j = 0;j < FILES_QUANTITY;++j) {
                File directory = new File(tableDir, i + ".dir");
                File file = new File(directory, j + ".dat");

                modifySingleFile(directory, file, changedInFile[i][j], deletedInFile[i][j]);
            }
        }

        dumpGarbage(tableDir);
    }

    private static void dumpGarbage(File tableDir) {
        for (File directory : tableDir.listFiles()) {
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

    private static void modifySingleFile(File directory, File file, Map<String, String> changed, Set<String> deleted)
    throws IOException, ValidityCheckFailedException {

        if ((changed.isEmpty()) && (deleted.isEmpty())) {
            return;
        }

        if (!directory.exists()) {
            directory.mkdir();
        }

        if (!file.exists()) {
            file.createNewFile();
        }

        StoreableTableFileReader reader = new StoreableTableFileReader(file);
        StoreableTableFileWriter writer = new StoreableTableFileWriter(file);

        try {
            String currentKey = null;

            do {
                currentKey = reader.nextKey();
                
                if (changed.containsKey(currentKey)) {
                    writer.writeKeyValue(currentKey, changed.get(currentKey));
                    changed.remove(currentKey);
                    continue;
                }

                if (deleted.contains(currentKey)) {
                    continue;
                }

                if (currentKey != null) {
                    writer.writeKeyValue(currentKey, reader.getCurrentSerializedValue());
                }

            } while (currentKey != null);

            for (Map.Entry<String, String> entry : changed.entrySet()) {
                writer.writeKeyValue(entry.getKey(), entry.getValue());
                //System.out.println(entry);
            }

            writer.flush();
        } finally {
            QuietCloser.closeQuietly(reader);
        }
    }
}
