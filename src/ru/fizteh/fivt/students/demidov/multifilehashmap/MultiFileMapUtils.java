package ru.fizteh.fivt.students.demidov.multifilehashmap;

public class MultiFileMapUtils {
	public static Integer getNumber(String name) {
		Integer number;
		if (Character.toString(name.charAt(1)).equals(".")) {
			number = Integer.parseInt(name.substring(0, 1));
		} else {
			number = Integer.parseInt(name.substring(0, 2));
		}
		
		return number;
	}
	
	public static String makeKey(Integer ndirectory, Integer nfile) {		
		return Integer.toString(ndirectory) + " " + Integer.toString(nfile);
	}
	
	public static Integer getNDirectory(Integer hashcode) {	
		return Math.abs(hashcode) % 16;
	}
	
	public static Integer getNFile(Integer hashcode) {	
		return (Math.abs(hashcode) / 16) % 16;
	}
}
