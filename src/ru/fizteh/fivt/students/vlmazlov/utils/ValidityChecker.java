package ru.fizteh.fivt.students.vlmazlov.utils;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidityChecker {

    public static final int MAX_KEY_LEN = 1 << 20;
    public static final int MIN_KEY_LEN = 1;
    public static final int MAX_VALUE_LEN = 1 << 20;
    public static final int MIN_VALUE_LEN = 1;

    public static void checkColumnType(Class<?> type) throws ValidityCheckFailedException {
        if (TypeName.getByClass(type) == null) {
            throw new ValidityCheckFailedException("Invalid type " + type);
        }
    }

    public static void checkValueFormat(Table table, Storeable toCheck) throws ValidityCheckFailedException {
        int i = 0;

        //Though invalid, it is checked elsewhere
        if (toCheck == null) {
            return;
        }

        try {
            for (i = 0; i < table.getColumnsCount(); ++i) {
                if (toCheck.getColumnAt(i) == null) {
                    continue;
                }

                if (!table.getColumnType(i).isAssignableFrom(toCheck.getColumnAt(i).getClass())) {
                    throw new ValidityCheckFailedException(
                        toCheck.getColumnAt(i).getClass() + "cannot be assigned to " + table.getColumnType(i));
                }
            }
        } catch (IndexOutOfBoundsException ex) {
            throw new ValidityCheckFailedException("Passed storeable has too few columns");
        }

        try {
            toCheck.getColumnAt(i);
        } catch (IndexOutOfBoundsException ex) {
            return;
        }

        throw new ValidityCheckFailedException("Passed storeable has too many columns");
    }

    public static void checkMultiFileStorageDir(File toCheck, int maxValue) throws ValidityCheckFailedException {
        if (!toCheck.getName().matches("\\d\\d?.dir")) {
            throw new ValidityCheckFailedException(toCheck.getPath() + " is not a valid name");
        }

        String[] tokens = toCheck.getName().split("\\.");

        if ((Integer.parseInt(tokens[0]) >= maxValue) && (Integer.parseInt(tokens[0]) < 0)) {
            throw new ValidityCheckFailedException(toCheck.getPath() + " is not a valid name");
        }

        if (toCheck.listFiles().length == 0) {
            throw new ValidityCheckFailedException(toCheck.getPath() + " is empty");
        }
    }

    public static void checkMultiFileStorageFile(File toCheck, int maxValue) throws ValidityCheckFailedException {
        if (!toCheck.getName().matches("\\d\\d?.dat")) {
            throw new ValidityCheckFailedException(toCheck.getPath() + " is not a valid name");
        }

        String[] tokens = toCheck.getName().split("\\.");

        if ((Integer.parseInt(tokens[0]) >= maxValue) && (Integer.parseInt(tokens[0]) < 0)) {
            throw new ValidityCheckFailedException(toCheck.getPath() + " is not a valid name");
        }

        if (toCheck.length() == 0) {
            throw new ValidityCheckFailedException(toCheck.getPath() + " is empty");
        }
    }

    public static void checkKeyStorageAffiliation(String key, int fileNum, int dirNum, int maxFileNum, int maxDirNum)
            throws ValidityCheckFailedException {
        if ((Math.abs(key.getBytes()[0]) % maxDirNum != dirNum) 
            || (Math.abs(key.getBytes()[0]) / maxFileNum % maxFileNum != fileNum)) {
            throw new ValidityCheckFailedException(key + " is in the wrong storage");
        }
    }

    public static void checkMultiTableDataBaseRoot(String rootName) throws ValidityCheckFailedException {
        if (rootName == null) {
            throw new ValidityCheckFailedException("directory not specified");
        }

        File root = new File(rootName);

        if (!root.isDirectory()) {
            throw new ValidityCheckFailedException(root.getPath() + " doesn't denote a directory");
        }

        for (File entry : root.listFiles()) {
            if (!entry.isDirectory()) {
                throw new ValidityCheckFailedException("root directory contains file " + entry.getName());
            }
        }
    }

    public static void checkMultiTableRoot(String root) throws ValidityCheckFailedException {
        if (root == null) {
            throw new ValidityCheckFailedException("root not specified");
        }

        checkMultiTableRoot(new File(root));
    }

    public static void checkMultiStoreableTableRoot(String root) throws ValidityCheckFailedException {
        if (root == null) {
            throw new ValidityCheckFailedException("root not specified");
        }

        checkMultiStoreableTableRoot(new File(root));
    }

    public static void checkMultiStoreableTableRoot(File root) throws ValidityCheckFailedException {
        if (!root.isDirectory()) {
            throw new ValidityCheckFailedException(root.getPath() + " doesn't denote a directory");
        }

        boolean signatureSeen = false;

        for (File content : root.listFiles()) {
            if ((content.isFile()) && (content.getName().equals("signature.tsv"))) {
                signatureSeen = true;
                continue;
            }

            //also allowed since Cache
            if ((content.isFile()) && (content.getName().equals("size.tsv"))) {
                continue;
            }

            if (!content.isDirectory()) {
                throw new ValidityCheckFailedException(root.getPath() + " contains file " + content.getName());
            }

            for (File file : content.listFiles()) {
                if (!file.isFile()) {
                    throw new ValidityCheckFailedException(file.getName() + " doesn't denote a file");
                }
            }
        }

        if (!signatureSeen) {
            throw new ValidityCheckFailedException("Table signature not specified or specified incorrectly");
        }
    }

    public static void checkMultiTableRoot(File root) throws ValidityCheckFailedException {
        if (!root.isDirectory()) {
            throw new ValidityCheckFailedException(root.getPath() + " doesn't denote a directory");
        }

        for (File directory : root.listFiles()) {
            if (!directory.isDirectory()) {
                throw new ValidityCheckFailedException(root.getPath() + " contains file " + directory.getName());
            }

            for (File file : directory.listFiles()) {
                if (!file.isFile()) {
                    throw new ValidityCheckFailedException(file.getName() + " doesn't denote a file");
                }
            }
        }
    }

    public static void checkMultiTableName(String name) throws ValidityCheckFailedException {
        //same criteria is applicable
        checkTableKey(name);

        if (!name.matches("\\w+")) {
            throw new ValidityCheckFailedException(name + " is not a valid name");
        }
    }

    public static void checkTableRoot(File root) throws ValidityCheckFailedException {
        if (!root.isDirectory()) {
            throw new ValidityCheckFailedException(root.getPath() + " doesn't denote a directory");
        }
    }

    public static void checkTableKey(String key) throws ValidityCheckFailedException {
        if (key == null) {
            throw new ValidityCheckFailedException("key not specified");
        }

        Pattern pattern = null;
        pattern = pattern.compile("\\s+|[\\r\\n]");
        Matcher matcher = pattern.matcher(key);

        if ((key.trim().length() < MIN_KEY_LEN) || (key.trim().length() > MAX_KEY_LEN) || (matcher.find())) {
            throw new ValidityCheckFailedException(key + " is not a valid key");
        }
    }

    public static void checkTableOffset(int offset) throws ValidityCheckFailedException {
        if (offset < 0) {
            throw new ValidityCheckFailedException(offset + " is negative, therefore, not a valid offset");
        }
    }

    public static void checkTableValue(Object value) throws ValidityCheckFailedException {
        if (value == null) {
            throw new ValidityCheckFailedException("value not specified");
        }
    }

    public static void checkStoreableTableSignature(List<Class<?>> signature) throws ValidityCheckFailedException {
        if (signature.isEmpty()) {
            throw new ValidityCheckFailedException("Table signature not specified");
        }
    }

    public static void checkTableSize(File sizeFile) throws ValidityCheckFailedException {
       
        Scanner scanner = null;
        try {
            scanner = new Scanner(sizeFile);
        } catch (FileNotFoundException ex) {
            throw new ValidityCheckFailedException("Unable to read file: " + ex.getMessage());
        }

        try {

            if (!scanner.hasNextInt()) {
                throw new ValidityCheckFailedException("Table size not specified int the corresponding file");
            }

            int size = scanner.nextInt();

            if (size < 0) {
                throw new ValidityCheckFailedException("Table size is negative");
            }

            if (scanner.hasNext()) {
                throw new ValidityCheckFailedException("size.tsv contains junk");
            }
        } finally {
            scanner.close();
        }
    }
}
