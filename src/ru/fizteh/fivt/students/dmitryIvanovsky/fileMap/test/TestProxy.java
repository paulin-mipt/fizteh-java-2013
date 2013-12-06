package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.test;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapLoggingFactory;
import java.io.StringWriter;
import java.util.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestProxy {
    StringWriter writer;
    FileMapLoggingFactory proxyFactory;

    @Before
    public void before() {
        writer = new StringWriter();
        proxyFactory = new FileMapLoggingFactory();
    }

    @Test
    public void proxyCorrectLogCoparable() {
        Comparable instance = (Comparable) proxyFactory.wrap(writer, 1, Comparable.class);
        instance.compareTo(1);
        JSONObject json = new JSONObject(writer.toString());
        assertEquals(json.get("method"), "compareTo");
        assertEquals(json.get("class"), "java.lang.Integer");
        assertTrue(json.getJSONArray("arguments").getInt(0) == 1);
        assertEquals(json.get("returnValue"), 0);
    }

    @Test
    public void proxyListCorrectJson() {
        ArrayList<Object> array = new ArrayList<Object>();
        List<Object> wrappedList = (List<Object>) proxyFactory.wrap(writer, array, List.class);
        wrappedList.add(1);
        JSONObject parsed = new JSONObject(writer.toString());
        Assert.assertTrue(parsed.getJSONArray("arguments").getInt(0) == 1);
    }

    @Test
    public void proxyList() {
        ArrayList<Object> array = new ArrayList<Object>();
        List<Object> wrappedList = (List<Object>) proxyFactory.wrap(writer, array, List.class);
        wrappedList.add(1);
        wrappedList.add(2);
        Assert.assertTrue(writer.toString() != null);
    }

    @Test
    public void proxyListReturnValue() {
        ArrayList<Object> array = new ArrayList<Object>();
        List<Object> wrappedList = (List<Object>) proxyFactory.wrap(writer, array, List.class);
        wrappedList.clear();
        Assert.assertTrue(!writer.toString().contains("returnValue"));
    }

    @Test
    public void proxyCorrectLogVector() {
        Vector<Object> vector1 = new Vector<>();
        Vector<Object> vector2 = new Vector<>();

        vector1.add(1);
        vector1.add(1.5);
        vector1.add("qwerty");
        vector1.add(vector1);
        vector1.add(vector2);
        vector1.add(null);

        vector2.add(2);
        vector2.add(vector2);
        vector2.add(vector1);

        ArrayList<Object> obj = new ArrayList<>();
        List instance = (List) proxyFactory.wrap(writer, obj, List.class);
        instance.add(vector1);

        JSONObject json = new JSONObject(writer.toString());
        assertEquals(json.get("method"), "add");
        assertEquals(json.get("class"), obj.getClass().getName());

        JSONArray array = json.getJSONArray("arguments").getJSONArray(0);

        assertEquals(array.length(), 6);
        assertEquals(array.get(0), 1);
        assertEquals(array.get(1), 1.5);
        assertEquals(array.getString(2), "qwerty");
        assertEquals(array.getString(3), "cyclic");
        assertEquals(array.get(5), JSONObject.NULL);

        JSONArray subArray = array.getJSONArray(4);
        assertEquals(subArray.length(), 3);
        assertEquals(subArray.getString(1), "cyclic");
        assertEquals(subArray.getString(2), "cyclic");
        assertEquals(json.get("returnValue"), true);
    }

    @Test
    public void proxyListCyclic() {
        ArrayList<Object> array = new ArrayList<Object>();
        array.add(array);
        List<Object> wrappedList = (List<Object>) proxyFactory.wrap(writer, array, List.class);
        wrappedList.addAll(array);
        JSONObject parsed = new JSONObject(writer.toString());
        Assert.assertEquals(parsed.getJSONArray("arguments").getJSONArray(0).getString(0), "cyclic");
    }

    @Test
    public void proxyCorrectLogSetArray() {
        HashSet<Object> obj = new HashSet<>();
        Set instance = (Set) proxyFactory.wrap(writer, obj, Set.class);
        Integer[] arrayInteger = new Integer[100];
        for (int i = 0; i < 100; ++i) {
            arrayInteger[i] = i;
        }
        instance.add(arrayInteger);
        JSONObject json = new JSONObject(writer.toString());
        JSONArray array = json.getJSONArray("arguments");
        for (int i = 0; i < 100; ++i) {
            assertEquals(array.getJSONArray(0).getInt(i), i);
        }
    }

    @Test
    public void proxyMapReturnNull() {
        HashMap<String, Object> obj = new HashMap<>();
        Map instance = (Map) proxyFactory.wrap(writer, obj, Map.class);
        instance.put("key", "value");
        JSONObject json = new JSONObject(writer.toString());
        assertEquals(json.get("returnValue"), JSONObject.NULL);
    }

    @Test
    public void proxyListMap() {
        ArrayList<Object> array = new ArrayList<Object>();
        array.add(new HashMap<>());
        List<Object> wrappedList = (List<Object>) proxyFactory.wrap(writer, array, List.class);
        wrappedList.indexOf(new int[0]);
        JSONObject parsed = new JSONObject(writer.toString());
        Assert.assertTrue(parsed.getJSONArray("arguments").getString(0) != null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void writerNullShouldFail() {
        proxyFactory.wrap(null, new ArrayList<Object>(), ArrayList.class.getInterfaces()[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void implementNullShouldFail() {
        proxyFactory.wrap(writer, null, ArrayList.class.getInterfaces()[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void interfaceNullShouldFail() {
        proxyFactory.wrap(writer, new ArrayList<Object>(), null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void proxyExceptionList() {
        ArrayList<Object> obj = new ArrayList<>();
        List instance = (List) proxyFactory.wrap(writer, obj, List.class);
        instance.get(1);
    }
}
