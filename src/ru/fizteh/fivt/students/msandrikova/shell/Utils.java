package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Utils {
    
    
    private Utils() {
        // static usage only
    }
    
    public static void generateAnError(final String description, String commandName, boolean isInteractive) {
        if (!commandName.equals("")) {
            System.err.println("Error: " + commandName + ": " + description);
        } else {
            System.err.println("Error: " + description);
        }
        if (!isInteractive) {
            System.exit(1);
        }
    }
    
    
    public static void copyFiles(File fileFrom, File fileTo) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;
        try {
            sourceChannel = new FileInputStream(fileFrom).getChannel();
            destinationChannel = new FileOutputStream(fileTo).getChannel();
            destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } finally {
            sourceChannel.close();
            destinationChannel.close();
        }
    }
    
    public static boolean createFile(File newFile) throws IOException {
        if (!newFile.getParentFile().exists()) {
            return false;
        }
        if (!newFile.createNewFile()) {
            return false;
        }
        return true;
    }
    
    public static boolean copying(File filePath, File destination, String commandName, boolean isInteractive)
            throws IOException {
        if (filePath.isDirectory()) {
            File newDestination = new File(destination + File.separator + filePath.getName());
            try {
                if (!newDestination.mkdirs()) {
                    Utils.generateAnError("Directory with name \"" + filePath.getName() 
                            + "\" can not be created in directory \"" 
                            + destination.getCanonicalPath() + "\"", commandName, isInteractive);
                    return false;
                }
            } catch (SecurityException e) {
                Utils.generateAnError("Directory with name \"" + filePath.getName() 
                        + "\" can not be created in directory \"" + destination.getCanonicalPath() 
                        + "\"", commandName, isInteractive);
                return false;
            }
            File[] listOfFiles;
            listOfFiles = filePath.listFiles();
            for (File nextFile : listOfFiles) {
                copying(nextFile, newDestination, commandName, isInteractive);
            }
        } else {
            File newFile = new File(destination + File.separator + filePath.getName());
            if (!Utils.createFile(newFile)) {
                Utils.generateAnError("File with name \"" + filePath.getName() 
                        + "\" can not be created in directory \"" + destination.getCanonicalPath() 
                        + "\"", commandName, isInteractive);
            }
            Utils.copyFiles(filePath, newFile);
        }
        return true;
    }
    
    public static boolean copyDirectoriesInSameDirectory(File filePath, File destination, 
            String commandName, boolean isInteractive) throws IOException {
        try {
            if (!destination.mkdirs()) {
                Utils.generateAnError("Directory with name \"" + destination.getCanonicalPath() 
                        + "\" can not be created in directory \"" 
                        + destination.getParentFile().getCanonicalPath() 
                        + "\"", commandName, isInteractive);
                return false;
            }
        } catch (SecurityException e) {
            Utils.generateAnError("Directory with name \"" + filePath.getName() 
                    + "\" can not be created in directory \"" + destination.getCanonicalPath() 
                    + "\"", commandName, isInteractive);
            return false;
        }
        File[] listOfFiles;
        listOfFiles = filePath.listFiles();
        for (File nextFile : listOfFiles) {
            copying(nextFile, destination, commandName, isInteractive);
        }
        return true;
    }
    
    public static boolean remover(File filePath, String commandName, boolean isInteractive)
            throws IOException {
        if (!filePath.exists()) {
            Utils.generateAnError("File with path \"" + filePath.getCanonicalPath() 
                    + "\" does not exist", commandName, isInteractive);
            return false;
        }
        if (filePath.isDirectory()) {
            File[] listOfFiles;
            listOfFiles = filePath.listFiles();
            for (File nextFile : listOfFiles) {
                remover(nextFile, commandName, isInteractive);
            }
        }
        try {
            if (!filePath.delete()) {
                Utils.generateAnError("File with path \"" + filePath.getCanonicalPath() 
                        + "\" can not be deleted", commandName, isInteractive);
                return false;
            }
        } catch (SecurityException e) {
            Utils.generateAnError("File with path \"" + filePath.getCanonicalPath() 
                    + "\" can not be deleted", commandName, isInteractive);
            return false;
        }
        return true;
    }
    
    public static String joinArgs(Collection<?> items, String separator) {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        
        for (Object o: items) {
            if (!first) {
                sb.append(separator);
            }
            first = false;
            sb.append(o.toString());
        }
        return sb.toString();
    }
    
    public static String[] parseOfInstruction(String instruction) {
        String[] arguments;
        arguments = instruction.split("\\s+");
        return arguments;
    }
    
    public static String[] parseOfInstructionLine(String instructionLine) {
        instructionLine = instructionLine.trim();
        return instructionLine.split("\\s*;\\s*", -1);
    }
    
    public static int getNameNumber(String name) {
        String[] tokens = name.split("[.]");
        return Integer.parseInt(tokens[0]);
    }
    
    public static int getNDirectory(String key) {
        int result = key.hashCode() % 16;
        return Math.abs(result);
    }
    
    public static int getNFile(String key) {
        int result = key.hashCode() / 16 % 16;
        return Math.abs(result);
    }
    
    public static boolean testUTFSize(String word) {
        return word.length() <= 1000 * 1000;
    }
    
    public static boolean testBadSymbols(String name) {
        return name.matches("[\u0430-\u044F\u0410-\u042FA-Za-z0-9_]*");
    }
    
    public static boolean isEmpty(String s) {
        if (s == null || s.trim().isEmpty() || s.equals("\n")) {
            return true;
        }
        return false;
    }
    
    
    public static List<Class<?>> classTypesFromList(List<String> columnTypes) throws IOException {
        List<Class<?>> answer = new ArrayList<Class<?>>();
        for (String columnType : columnTypes) {
            switch(columnType) {
            case "int":
                answer.add(Integer.class);
                break;
            case "long":
                answer.add(Long.class);
                break;
            case "byte":
                answer.add(Byte.class);
                break;
            case "float":
                answer.add(Float.class);
                break;
            case "double":
                answer.add(Double.class);
                break;
            case "boolean":
                answer.add(Boolean.class);
                break;
            case "String":
                answer.add(String.class);
                break;
            default:
                throw new IOException("Incorrect signature file.");
            }
        }
        return answer;
    }
    
    public static List<Class<?>> getClassTypes(File f) throws IOException {
        File signature = new File(f, "signature.tsv");
        if (!signature.exists()) {
            throw new IOException("Signature file does not exist.");
        }
        Scanner reader = new Scanner(new FileInputStream(signature));
        if (!reader.hasNextLine()) {
            reader.close();
            throw new IOException("Signature file is empty.");
        }
        String[] columnTypes = reader.nextLine().split("\\s");
        reader.close();
        
        return Utils.classTypesFromList(Arrays.asList(columnTypes));
    }
    
    public static List<String> getColumnTypesNames(List<Class<?>> columnTypes) {
        List<String> answer = new ArrayList<String>();
        for (int i = 0; i < columnTypes.size(); ++i) {
            switch(columnTypes.get(i).getName()) {
            case "java.lang.Integer":
                answer.add("int");
                break;
            case "java.lang.Byte":
                answer.add("byte");
                break;
            case "java.lang.Long":
                answer.add("long");
                break;
            case "java.lang.Float":
                answer.add("float");
                break;
            case "java.lang.Boolean":
                answer.add("boolean");
                break;
            case "java.lang.String":
                answer.add("String");
                break;
            case "java.lang.Double":
                answer.add("double");
                break;
            default:
                break;
            }
        }
        return answer;
    }
    
    public static List<Class<?>> parseColumnTypes(String[] argumentsList) throws IOException {
        List<String> columnTypes = new ArrayList<String>();
        for (int i = 2; i < argumentsList.length; i++) {
            if (argumentsList[i].equals(")") || argumentsList[i].equals("(")) {
                continue;
            }
            columnTypes.add(argumentsList[i]);
        }
        if (columnTypes.isEmpty()) {
            throw new IOException("Incorrect arguments amount");
        }
        if (columnTypes.get(0).startsWith("(")) {
            columnTypes.set(0, columnTypes.get(0).substring(1));
        }
        if (columnTypes.get(columnTypes.size() - 1).endsWith(")")) {
            String previous = columnTypes.get(columnTypes.size() - 1);
            columnTypes.set(columnTypes.size() - 1, previous.substring(0, previous.length() - 1));
        }
        
        return Utils.classTypesFromList(columnTypes);
    }

    public static boolean testColumnTypes(List<Class<?>> columnTypes) {
        Set<Class<?>> permitTypes = new HashSet<Class<?>>();
        permitTypes.add(Integer.class);
        permitTypes.add(Byte.class);
        permitTypes.add(Long.class);
        permitTypes.add(Double.class);
        permitTypes.add(Float.class);
        permitTypes.add(String.class);
        permitTypes.add(Boolean.class);
            
        if (columnTypes.isEmpty()) {
            return false;
        }
        for (Class<?> type : columnTypes) {
            if (type == null) {
                return false;
            }
            if (!permitTypes.contains(type)) {
                return false;
            }
        }
        
        return true;
    }
    
}
