package ru.fizteh.fivt.students.mishatkin.storable;

import org.json.JSONArray;
import ru.fizteh.fivt.students.mishatkin.junit.JUnit;

/**
 * Created by Vladimir Mishatkin on 11/11/13
 */
public class StorableMain extends JUnit {
	public static void main(String args[]) {
		JSONArray o = new JSONArray("[\" EL  1 \", 3, null]");
		System.err.println(o.toString());
		String s = o.toString();
		JSONArray os = new JSONArray(s);
		System.err.println(os.toString());
	}
}
