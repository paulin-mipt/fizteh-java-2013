package ru.fizteh.fivt.students.baldindima.junit;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class BaseSignature {

    static final String[] STYPES = {"int", "long", "byte", "float", "double", "boolean", "String"};
    static final Class<?>[] CLASSES = {Integer.class, Long.class, Byte.class, Float.class,
            Double.class, Boolean.class, String.class};
    static final Map<String, Class<?>> TYPES_CLASSES;
    static {
    	Map<String, Class<?>> aTYPES = new HashMap<String, Class<?>>();
    	aTYPES.put("int", Integer.class);
    	aTYPES.put("long", Long.class);
    	aTYPES.put("byte", Byte.class);
    	aTYPES.put("float", Float.class);
    	aTYPES.put("double", Double.class);
    	aTYPES.put("boolean", Boolean.class);
    	aTYPES.put("String", String.class);
    	TYPES_CLASSES = Collections.unmodifiableMap(aTYPES);
    	
    }
    static final Map<Class<?>, String> CLASSES_TYPES;
    static {
    	Map<Class<?>, String> aTYPES = new HashMap<Class<?>, String>();
    	aTYPES.put(Integer.class, "int");
    	aTYPES.put(Long.class, "long");
    	aTYPES.put(Byte.class, "byte");
    	aTYPES.put(Float.class, "float");
    	aTYPES.put(Double.class, "double");
    	aTYPES.put(Boolean.class, "boolean");
    	aTYPES.put(String.class, "String");
    	CLASSES_TYPES = Collections.unmodifiableMap(aTYPES);
    	
    }



    public static void setBaseSignature(String nameDirectory, List<Class<?>> types) throws IOException {


        File fileForSignature = new File(nameDirectory, "signature.tsv");

        try (PrintWriter output = new PrintWriter(fileForSignature)) {
            for (int i = 0; i < types.size(); ++i) {
                
            	String search = CLASSES_TYPES.get(types.get(i));
            	if (search == null){
            		throw new IllegalArgumentException("There is no such types!");
            	}
            	output.write(search);
                
                
                if (i + 1 != types.size()) {
                    output.write(" ");
                }
            }
        }


    }

    public static List<Class<?>> getBaseSignature(String directoryName) throws IOException {
        File file = new File(directoryName, "signature.tsv");
        if (!file.exists()) {
            throw new IOException("Cannot find file");
        }
        Scanner input = new Scanner(file);
        if (!input.hasNext()) {
            throw new IOException("Empty signature");
        }

        StringBuilder builder = new StringBuilder();
        while (input.hasNext()) {
            builder.append(input.next()).append(" ");
        }

        String[] signatureFromFile = builder.toString().split(" ");

        List<Class<?>> signature = new ArrayList<>();

        if (signatureFromFile.length <= 0) {
            throw new IOException("Empty signature");
        }

        for (String type : signatureFromFile) {
            Class <?> search = TYPES_CLASSES.get(type);
            if (search == null){
            	throw new IOException("There is no such type");
            }
        	signature.add(search);
            
            
        }
        return signature;
    }


    public static List<Class<?>> getTypes(String str) throws IOException {
        List<Class<?>> result = new ArrayList<>();
        if (!(str.charAt(0) == '(' && str.charAt(str.length() - 1) == ')')) {
            throw new IOException("wrong type (no brackets)");
        }
        str = str.substring(1, str.length() - 1);
        String[] s = str.split("[\\s]+");
        for (String type: s){
        	Class<?> search = TYPES_CLASSES.get(type);
            if (search == null){
            	throw new IOException("Cannot read type!");
            }
            result.add(search);
        }
       
       
        return result;
    }
}