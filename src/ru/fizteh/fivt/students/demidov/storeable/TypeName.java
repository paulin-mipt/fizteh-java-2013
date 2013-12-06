package ru.fizteh.fivt.students.demidov.storeable;

import java.util.Map;
import java.util.HashMap;

public enum TypeName {
	INTEGER(Integer.class, "int"),
	LONG(Long.class, "long"),
	BOOLEAN(Boolean.class, "boolean"),
	DOUBLE(Double.class, "double"),
	FLOAT(Float.class, "float"),
	STRING(String.class, "String"),
	BYTE(Byte.class, "byte");

	private Class<?> clazz;
	private String type;
	private static Map<Class<?>, String> typeByClass; 
	private static Map<String, Class<?>> classByType; 


	private TypeName(Class<?> clazz, String type) {
		this.clazz = clazz;
		this.type = type;
	}

	static {
		typeByClass = new HashMap<Class<?>, String>();
		classByType = new HashMap<String, Class<?>>();

		for (TypeName typeName : TypeName.values()) {
			typeByClass.put(typeName.clazz, typeName.type);
			classByType.put(typeName.type, typeName.clazz);
		}
	}

	public static String getAppropriateName(Class<?> clazz) {
		String className = typeByClass.get(clazz);
		if (className == null) {
			throw new IllegalArgumentException("no appropriate primitive type");
		}
		return className;
	}

	public static Class<?> getAppropriateClass(String type) {
		Class<?> approptiateClass = classByType.get(type);
		if (approptiateClass == null) {
			throw new IllegalArgumentException("wrong column type");
		}
		return approptiateClass;
	}
}
