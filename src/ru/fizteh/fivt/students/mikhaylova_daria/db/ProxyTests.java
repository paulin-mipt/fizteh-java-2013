package ru.fizteh.fivt.students.mikhaylova_daria.db;


import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


import java.io.StringWriter;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ProxyTests implements ProxyTestInterface {
    private StringWriter writer;
    private LogProxyFactory factory;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void before() {
        writer = new StringWriter();
        factory = new LogProxyFactory();
    }

    @After
    public void after() {
        folder.delete();
    }

    @Test(expected = IllegalArgumentException.class)
    public void writerNullShouldFail() {
        factory.wrap(null, new ArrayList<Object>(), ArrayList.class.getInterfaces()[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void implementNullShouldFail() {
        factory.wrap(writer, null, ArrayList.class.getInterfaces()[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void interfaceNullShouldFail() {
        factory.wrap(writer, new ArrayList<Object>(), null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldTargetException() {
        ArrayList<Object> obj = new ArrayList<>();
        List instance = (List) factory.wrap(writer, obj, List.class);
        instance.get(1005000);
    }

    @Test
    public void primitiveTypes() {
        int a = 123;
        Comparable instance = (Comparable) factory.wrap(writer, a, Integer.class.getInterfaces()[0]);
        instance.compareTo(123);
        JSONObject json = new JSONObject(writer.toString());
        assertEquals(json.get("timestamp").getClass(), Long.class);
        assertEquals(json.get("method"), "compareTo");
        assertEquals(json.get("class"), "java.lang.Integer");
        assertTrue(json.getJSONArray("arguments").getInt(0) == 123);
        assertEquals(json.get("returnValue"), 0);
    }

    @Test
    public void iterableTypes() {
        Vector<Object> v = new Vector<>();
        v.add(1d);
        v.add(true);
        v.add("abc");
        v.add(v);
        Vector<Object> vOther = new Vector<>();
        vOther.add(2);
        vOther.add(vOther);
        vOther.add(v);
        v.add(vOther);
        v.add(null);
        ArrayList<Object> obj = new ArrayList<>();
        List instance = (List) factory.wrap(writer, obj, List.class);
        instance.add(v);
        JSONObject json = new JSONObject(writer.toString());
        assertEquals(json.get("timestamp").getClass(), Long.class);
        assertEquals(json.get("method"), "add");
        assertEquals(json.get("class"), obj.getClass().getName());
        JSONArray array = json.getJSONArray("arguments").getJSONArray(0);
        assertEquals(array.length(), 6);
        assertEquals(array.get(0), 1);
        assertEquals(array.get(1), true);
        assertEquals(array.getString(2), "abc");
        assertEquals(array.getString(3), "cyclic");
        assertEquals(array.get(5), JSONObject.NULL);
        JSONArray subArray = array.getJSONArray(4);
        assertEquals(subArray.length(), 3);
        assertEquals(subArray.getString(1), subArray.getString(2));
        assertEquals(subArray.getString(1), "cyclic");
        assertEquals(json.get("returnValue"), true);
    }

    @Test
    public void arrayTypes() {
        HashMap<String, Object> obj = new HashMap<>();
        Map instance = (Map) factory.wrap(writer, obj, Map.class);
        Integer[] arrayInteger = new Integer[10];
        for (int i = 0; i < 10; ++i) {
            arrayInteger[i] = i;
        }
        instance.put("arrayInteger", arrayInteger);
        JSONObject json = new JSONObject(writer.toString());
        JSONArray array = json.getJSONArray("arguments");
        assertEquals("Ошибка в проксировании аргумента, являющегося массивом объектов", array.get(0).toString(),
                "arrayInteger");
        for (int i = 0; i < 10; ++i) {
            assertEquals("Ошибка в проксировании аргумента, являющегося массивом объектов - несовпадение значений",
                    array.getJSONArray(1).getInt(i), i);
        }
        StringWriter writerOther = new StringWriter();
        HashMap<String, Object> objOther = new HashMap<>();
        Map instanceOther = (Map) factory.wrap(writerOther, objOther, Map.class);
        int[] arrayInt = new int [10];
        for (int i = 0; i < 10; ++i) {
            arrayInt[i] = 1;
        }
        instanceOther.put("arrayInt", arrayInt);
        JSONObject jsonOther = new JSONObject(writerOther.toString());
        assertEquals("Ошибка в проксировании аргумента, являющегося массивом примитивных типов. "
                    + "Ожидается приминение toString", jsonOther.getJSONArray("arguments").get(1), arrayInt.toString());

    }

    @Test
    public void correctRecordStructure() {
        int a = 123;
        Comparable instance = (Comparable) factory.wrap(writer, a, Integer.class.getInterfaces()[0]);
        instance.compareTo(123);
        String first = writer.toString();
        instance.compareTo(1234);
        assertTrue("Необходим перевод строки между описанием методов", writer.toString().contains("\n"));
    }

    @Test
    public void returnValueIsNull() {
        HashMap<String, Object> obj = new HashMap<>();
        Map instance = (Map) factory.wrap(writer, obj, Map.class);
        instance.put("key", "value");
        JSONObject json = new JSONObject(writer.toString());
        assertEquals(json.get("returnValue"), JSONObject.NULL);
    }

    @Test(expected = org.json.JSONException.class)
    public void returnValueIsVoid() {
        HashMap<String, Object> obj = new HashMap<>();
        Map instance = (Map) factory.wrap(writer, obj, Map.class);
        instance.clear();
        JSONObject json = new JSONObject(writer.toString());
        json.get("returnValue");
    }


    @Test
    public void logNotOverrideMethodShouldNoLog() {
        HashMap<String, Object> obj = new HashMap<>();
        Map instance = (Map) factory.wrap(writer, obj, Map.class);
        instance.equals(new HashMap());
        assertTrue(writer.toString().isEmpty());

    }

}
