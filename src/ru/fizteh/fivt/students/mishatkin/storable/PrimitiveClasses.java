package ru.fizteh.fivt.students.mishatkin.storable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vladimir Mishatkin on 11/11/13
 */
public abstract class PrimitiveClasses {
//	int, long, byte, float, double, boolean, String
	private static Map<String, Class> classes = new HashMap<>();
	static {
		Map<String, Class> temp = new HashMap<>();
		temp.put("int", Integer.class);
		temp.put("long", Long.class);
		temp.put("byte", Byte.class);
		temp.put("float", Float.class);
		temp.put("double", Double.class);
		temp.put("boolean", Boolean.class);
		temp.put("String", String.class);
	}
	static Class<?> classForName(String className) {
		return classes.get(className);
	}
}
