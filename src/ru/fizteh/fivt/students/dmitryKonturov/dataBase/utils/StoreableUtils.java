package ru.fizteh.fivt.students.dmitryKonturov.dataBase.utils;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.DatabaseException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class StoreableUtils {
    private static final String SIGNATURE_FILE_NAME = "signature.tsv";
    private static final String[] ALLOWED_TYPES_TO_SAVE = new String[] {
            "int",
            "long",
            "byte",
            "float",
            "double",
            "boolean",
            "String" };

    private static Map<String, Class<?>> mappedType = new HashMap<>();
    private static Map<Class<?>, String> mappedClass = new HashMap<>();

    static {
        mappedType.put("int", Integer.class);
        mappedType.put("byte", Byte.class);
        mappedType.put("long", Long.class);
        mappedType.put("float", Float.class);
        mappedType.put("double", Double.class);
        mappedType.put("boolean", Boolean.class);
        mappedType.put("String", String.class);
        mappedClass.put(Integer.class, "int");
        mappedClass.put(Byte.class, "byte");
        mappedClass.put(Long.class, "long");
        mappedClass.put(Float.class, "float");
        mappedClass.put(Double.class, "double");
        mappedClass.put(Boolean.class, "boolean");
        mappedClass.put(String.class, "String");
    }

    public static Class<?> getClassByString(String name) {
        return mappedType.get(name);
    }

    public static String getStringByClass(Class<?> type) {
        return mappedClass.get(type);
    }

    public static String getSignatureFileName() {
        return SIGNATURE_FILE_NAME;
    }

    public static boolean isSupportedType(Class<?> type) {
        return mappedClass.containsKey(type);
    }

    public static void checkSignatureFile(Path file) throws DatabaseException, IOException {
        if (!Files.isRegularFile(file)) {
            throw new DatabaseException("Signature file is not regular");
        }
        if (!Files.isReadable(file) || !Files.isWritable(file)) {
            throw new DatabaseException("Signature file has not enough rights");
        }
        try (Scanner scanner = new Scanner(file)) {
            scanner.useDelimiter("\\s+");
            if (!scanner.hasNext()) {
                throw new DatabaseException("Signature file is empty");
            }
            while (scanner.hasNext()) {
                String currentType = scanner.next();
                boolean found = false;
                for (String type : ALLOWED_TYPES_TO_SAVE) {
                    if (currentType.equals(type)) {
                        found = true;
                    }
                }
                if (!found) {
                    throw new DatabaseException("Signature file contains unsupported type");
                }
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Fail to check signature file", e);
        }
    }

    public static void checkStoreableBelongsToTable(Table table, Storeable storeable) throws IllegalArgumentException {
       if (table == null) {
           throw new IllegalArgumentException("Table is null");
       }
       if (storeable == null) {
           throw new IllegalArgumentException("Storeable is null");
       }
       int columnCount = table.getColumnsCount();
       for (int i = 0; i < columnCount; ++i) {
           try {
                if (storeable.getColumnAt(i) == null) {
                   continue;
               }
               Class<?> storeableClass = storeable.getColumnAt(i).getClass();
               Class<?> tableClass = table.getColumnType(i);

               if (!(storeableClass == tableClass)) {
                    throw new ColumnFormatException(String.format("types not equal: %s with %s",
                            storeableClass.getName(), tableClass.getName()));
                }
           } catch (Exception e) {
               throw new ColumnFormatException(String.format("Column %d  is bad", i), e);
           }
       }
       boolean isCorrect;
       try {
           storeable.getColumnAt(columnCount);
           isCorrect = false;
       } catch (Exception e) {
           isCorrect = true;
       }
       if (!isCorrect) {
           throw new ColumnFormatException("Storeable contains more columns than table");
       }
    }

    public static List<Class<?>> loadSignatureFile(Path file) throws DatabaseException, IOException {
        checkSignatureFile(file);
        List<Class<?>> toReturn = new ArrayList<>();
        try (Scanner scanner = new Scanner(Files.newInputStream(file))) {
            scanner.useDelimiter("\\s+");
            while (scanner.hasNext()) {
                String currentType = scanner.next();
                Class<?> classType = getClassByString(currentType);
                if (classType == null) {
                    throw new DatabaseException("Type " + currentType + " not supported");
                }
                toReturn.add(classType);
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Fail to check signature file", e);
        }
        return toReturn;
    }

    public static void writeSignatureFile(Path file, List<Class<?>> types) throws IOException, DatabaseException {
        for (Class<?> type : types) {
            if (mappedClass.get(type) == null) {
                throw new ColumnFormatException("Unsupported types");
            }
        }
        try (BufferedWriter output = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            boolean isFirst = true;
            for (Class<?> type : types) {
                String typeName = getStringByClass(type);
                if (typeName == null) {
                    throw new DatabaseException("Not supported type");
                }
                if (!isFirst) {
                    output.write(" ");
                }
                output.write(typeName);
                isFirst = false;
            }
        } catch (IOException e) {
            throw new IOException("Cannot save signature file", e);
        } catch (Exception e) {
            throw new DatabaseException("Cannot save signature file", e);
        }
    }


}
