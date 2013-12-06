package ru.fizteh.fivt.students.visamsonov.util;

public class StringUtils {

	public static String join (String[] array, String separator) {
		if (array == null || array.length == 0) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		result.append(array[0]);
		for (int i = 1; i < array.length; ++i) {
			result.append(separator).append(array[i]);
		}
		return result.toString();
	}
}