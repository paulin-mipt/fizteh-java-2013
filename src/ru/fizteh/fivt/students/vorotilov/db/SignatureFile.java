package ru.fizteh.fivt.students.vorotilov.db;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class SignatureFile {

    static String signatureFileName = "signature.tsv";

    /**
     * Считывает файл сигнатур из директории tableRootDir
     *
     * @param tableRootDir Директория с файлом сигнатур.
     * @return Список типов колонок
     * @throws java.io.IOException При ошибках ввода/вывода.
     * @throws IllegalStateException Если файл не существует или прочтенная строка null
     * @throws ColumnFormatException Если неизвестный тип колонки
     */
    public static List<Class<?>> readSignature(File tableRootDir) throws IOException, ColumnFormatException {
        File signatureFile = new File(tableRootDir, signatureFileName);
        if (!signatureFile.exists()) {
            throw new IllegalStateException("Signature file not exists");
        }
        String inputString;
        try (RandomAccessFile signature = new RandomAccessFile(signatureFile, "rw")) {
            inputString = signature.readLine();
        } catch (IOException e) {
            throw new IOException("Read signature.tsv error", e);
        }
        if (inputString == null) {
            throw new IllegalStateException("Readed string from signature file is null");
        }
        if (inputString.trim().equals("")) {
            throw new ColumnFormatException("There is no colums in signature file");
        }
        String[] parsedColumnTypes = inputString.split("[ ]");
        List<Class<?>> classes = new ArrayList<>(parsedColumnTypes.length);
        for (int i = 0; i < parsedColumnTypes.length; ++i) {
            classes.add(i, parseColumnType(parsedColumnTypes[i]));
        }
        return classes;
    }

    /**
     * Создает новый файл сигнатур в директории tableRootDir
     *
     * @param tableRootDir Директория с файлом сигнатур.
     * @param classes Список типов колонок
     * @throws java.io.IOException При ошибках ввода/вывода.
     * @throws IllegalStateException Если файл уже существует
     * @throws ColumnFormatException Если неизвестный тип колонки
     */
    public static void createSignature(File tableRootDir, List<Class<?>> classes) throws IOException {
        if (classes == null) {
            throw new IllegalArgumentException("Column types is null");
        }
        File signatureFile = new File(tableRootDir, signatureFileName);
        if (signatureFile.exists()) {
            throw new IllegalStateException("Signature file is already exists");
        }
        StringBuilder concatenatedColumnTypes = new StringBuilder();
        for (Class<?> aClass : classes) {
            concatenatedColumnTypes.append(formatColumnType(aClass));
            concatenatedColumnTypes.append(" ");
        }
        try (RandomAccessFile signature = new RandomAccessFile(signatureFile, "rw")) {
            signature.writeBytes(concatenatedColumnTypes.toString().trim());
        }
    }

    /**
     * По строке с типом возвращает его класс
     *
     * @param columnType Тип колонки таблицы
     * @throws ColumnFormatException Если неизвестный тип колонки
     */
    public static Class<?> parseColumnType(String columnType) {
        switch (columnType) {
            case "int":
                return Integer.class;
            case "long":
                return Long.class;
            case "byte":
                return Byte.class;
            case "float":
                return Float.class;
            case "double":
                return Double.class;
            case "boolean":
                return Boolean.class;
            case "String":
                return String.class;
            default:
                throw new ColumnFormatException("Unknown column type: " + columnType);
        }
    }

    /**
     * По классн возвращает строку
     *
     * @param columnType Класс колонки
     * @throws ColumnFormatException Если неизвестный тип колонки
     */
    public static String formatColumnType(Class<?> columnType) {
        if (columnType == null) {
            throw new IllegalArgumentException("Column type is null");
        }
        switch (columnType.getName()) {
            case "java.lang.Integer":
                return "int";
            case "java.lang.Long":
                return "long";
            case "java.lang.Byte":
                return "byte";
            case "java.lang.Float":
                return "float";
            case "java.lang.Double":
                return "double";
            case "java.lang.Boolean":
                return "boolean";
            case "java.lang.String":
                return "String";
            default:
                throw new ColumnFormatException("Uknonwn column type: " + columnType.getName());
        }
    }


    public static List<Class<?>> parseInputColumnTypes(String input) {
        if (input.trim().equals("")) {
            throw new IllegalArgumentException("Column types are empty");
        }
        String inputWithoutBraces = input.substring(1, input.length() - 1);
        if (inputWithoutBraces.trim().equals("")) {
            throw new ColumnFormatException("Unknown column type");
        }
        String[] parsedColumnTypes = inputWithoutBraces.split("\\s+");
        List<Class<?>> classes = new ArrayList<>(parsedColumnTypes.length);
        for (int i = 0; i < parsedColumnTypes.length; ++i) {
            classes.add(i, parseColumnType(parsedColumnTypes[i]));
        }
        return classes;
    }

    public static List<Object> parseValues(StoreableTable currentTable, String input) throws ColumnFormatException {
        List<Class<?>> classes = currentTable.getColumnTypes();
        List<Object> values = new ArrayList<>(classes.size());
        for (int i = 0; i < classes.size(); ++i) {
            values.add(i, null);
        }
        String[] splittedInput = input.split("\\s+");
        if (splittedInput.length != classes.size()) {
            throw new ColumnFormatException("Parsed values has different size from expected");
        }
        for (int i = 0; i < classes.size(); ++i) {
            if (splittedInput[i] == null) {
                throw new IllegalArgumentException("Null in values");
            }
            try {
                switch (classes.get(i).getName()) {
                    case "java.lang.Integer":
                        values.set(i, new Integer(splittedInput[i]));
                        break;
                    case "java.lang.Long":
                        values.set(i, new Long(splittedInput[i]));
                        break;
                    case "java.lang.Byte":
                        values.set(i, new Byte(splittedInput[i]));
                        break;
                    case "java.lang.Float":
                        values.set(i, new Float(splittedInput[i]));
                        break;
                    case "java.lang.Double":
                        values.set(i, new Double(splittedInput[i]));
                        break;
                    case "java.lang.Boolean":
                        values.set(i, new Boolean(splittedInput[i]));
                        break;
                    case "java.lang.String":
                        values.set(i, splittedInput[i]);
                        break;
                    default:
                        throw new ColumnFormatException("Uknonwn column type: " + classes.get(i).getName());
                }
            } catch (Exception e) {
                throw new ColumnFormatException("Error value type", e);
            }
        }
        return values;
    }

}
