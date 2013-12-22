package ru.fizteh.fivt.students.mikhaylova_daria.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestStoreable {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private File mainDir;
    private File goodTable;
    private File goodTableSign;
    private File badTableEmpty;
    private File badTableEmptySign;
    private TableManagerFactory factory;
    private ArrayList<Class<?>> goodTypeList;
    private ArrayList<Object> goodValueList;
    private ArrayList<Object> wrongValueList;
    private TableManager provider;
    private Table table;
    private Storeable st;
    private final String goodStrVal
            = "<row><col>12</col><col>12</col><null/><col>12.2</col><col>12.2</col><col>true</col><null/></row>";

    @Before
    public void before() {
        factory = new TableManagerFactory();
        goodTypeList = new ArrayList<>();
        goodTypeList.add(Integer.class);
        goodTypeList.add(Byte.class);
        goodTypeList.add(Long.class);
        goodTypeList.add(Float.class);
        goodTypeList.add(Double.class);
        goodTypeList.add(Boolean.class);
        goodTypeList.add(String.class);
        goodValueList = new ArrayList<Object>();
        Integer integ = 12;
        Float fl = new Float(12.2);
        goodValueList.add(integ);
        goodValueList.add(integ.byteValue());
        goodValueList.add(null);
        goodValueList.add(fl);
        goodValueList.add(fl.doubleValue());
        goodValueList.add(true);
        goodValueList.add(null);
        wrongValueList = new ArrayList<Object>();
        wrongValueList.add(integ);
        wrongValueList.add(integ.byteValue());
        wrongValueList.add(integ.longValue());
        wrongValueList.add(integ.floatValue());
        wrongValueList.add(integ.doubleValue());
        wrongValueList.add("123");
        wrongValueList.add(true);
        try {
            mainDir = folder.newFolder("mainDir");
            goodTable = new File(mainDir, "goodTable");
            if (!goodTable.mkdir()) {
                throw new IOException("Creating file error");
            }
            goodTableSign = new File(goodTable, "signature.tsv");
            if (!goodTableSign.createNewFile()) {
                throw new IOException("Creating file error");
            }
            badTableEmptySign = new File(mainDir, "badTable");
            if (!badTableEmptySign.mkdir()) {
                throw new IOException("Creating file error");
            }
            badTableEmpty = new File(mainDir, "badTable2");
            if (!badTableEmpty.mkdir()) {
                throw new IOException("Creating file error");
            }
            File badTableSign = new File(badTableEmptySign, "signature.tsv");
            if (!badTableSign.createNewFile()) {
                throw new IOException("Creating file error");
            }

            String str = "int byte long float double boolean String";
            try (BufferedWriter signatureWriter =
                         new BufferedWriter(new FileWriter(goodTableSign))) {
                signatureWriter.write(str);
            } catch (IOException e) {
                throw new IOException("Reading error: signature.tsv", e);
            }
            provider = factory.create(mainDir.toString());
            table = provider.getTable("goodTable");
            st = provider.createFor(table, goodValueList);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.toString());
            System.exit(1);
        }
    }

    @After
    public void after() {
        factory.close();
        folder.delete();
    }

    @Test
    public void correctValueGet() {
        assertEquals("Несовпадение значений: Integer", st.getIntAt(0), (Integer) goodValueList.get(0));
        assertEquals("Несовпадение значений: Byte", st.getByteAt(1), (Byte) goodValueList.get(1));
        assertNull("Значение null в Long", st.getLongAt(2));
        assertEquals("Несовпадение значений: Float", st.getFloatAt(3), (Float) goodValueList.get(3));
        assertEquals("Несовпадение значений: Double", st.getDoubleAt(4), (Double) goodValueList.get(4));
        assertEquals("Несовпадение значений: Boolean", st.getBooleanAt(5), true);
        assertNull("Значение null в String", st.getStringAt(6));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutOfBoundsShouldFail() {
        st.getColumnAt(table.getColumnsCount() + 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutOfBoundsShouldFailInt() {
        st.getIntAt(table.getColumnsCount() + 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutOfBoundsShouldFailByte() {
        st.getByteAt(table.getColumnsCount() + 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutOfBoundsShouldFailLong() {
        st.getLongAt(table.getColumnsCount() + 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutOfBoundsShouldFailFloat() {
        st.getFloatAt(table.getColumnsCount() + 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutOfBoundsShouldFailDouble() {
        st.getDoubleAt(table.getColumnsCount() + 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutOfBoundsShouldFailBoolean() {
        st.getBooleanAt(table.getColumnsCount() + 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutOfBoundsShouldFailStr() {
        st.getStringAt(table.getColumnsCount() + 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutOfBoundsShouldFailSet() {
        st.setColumnAt(table.getColumnsCount() + 1, "123");
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectValueByteButFoundStr() {
        st.setColumnAt(1, "string");
    }


    @Test(expected = ColumnFormatException.class)
    public void incorrectValueStringButFoundInt() {
        st.setColumnAt(6, 12);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectGetInt() {
        st.getIntAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectGetByte() {
        st.getByteAt(5);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectGetLong() {
        st.getLongAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectGetFloat() {
        st.getFloatAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectGetDouble() {
        st.getDoubleAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectGetBoolean() {
        st.getBooleanAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectGetString() {
        st.getStringAt(0);
    }

    @Test
    public void correctValueByte() {
        byte b = 1;
        st.setColumnAt(1, b);
    }

    @Test
    public void correctValuePut() {
        st.setColumnAt(0, (Integer) goodValueList.get(0));
        st.setColumnAt(1, (Byte) goodValueList.get(1));
        st.setColumnAt(2, (Long) goodValueList.get(2));
        st.setColumnAt(3, (Float) goodValueList.get(3));
        st.setColumnAt(4, (Double) goodValueList.get(4));
        st.setColumnAt(5, (Boolean) goodValueList.get(5));
        st.setColumnAt(6, (String) goodValueList.get(6));
        assertEquals("Несовпадение значений: Integer", st.getIntAt(0), (Integer) goodValueList.get(0));
        assertEquals("Несовпадение значений: Byte", st.getByteAt(1), (Byte) goodValueList.get(1));
        assertNull("Значение null в Long", st.getLongAt(2));
        assertEquals("Несовпадение значений: Float", st.getFloatAt(3), (Float) goodValueList.get(3));
        assertEquals("Несовпадение значений: Double", st.getDoubleAt(4), (Double) goodValueList.get(4));
        assertEquals("Несовпадение значений: Boolean", st.getBooleanAt(5), true);
        assertNull("Значение null в String", st.getStringAt(6));
    }

    @Test
    public void correctValuePutNull() {
        st.setColumnAt(0, null);
        st.setColumnAt(1, null);
        st.setColumnAt(2, null);
        st.setColumnAt(3, null);
        st.setColumnAt(4, null);
        st.setColumnAt(5, null);
        st.setColumnAt(6, null);
        for (int i = 0; i < 7; ++i) {
            assertNull("Ошибка в get или put при работе с null в значении", st.getColumnAt(i));
        }
        assertNull("Несовпадение значений: Integer", st.getIntAt(0));
        assertNull("Несовпадение значений: Byte", st.getByteAt(1));
        assertNull("Значение null в Long", st.getLongAt(2));
        assertNull("Несовпадение значений: Float", st.getFloatAt(3));
        assertNull("Несовпадение значений: Double", st.getDoubleAt(4));
        assertNull("Несовпадение значений: Boolean", st.getBooleanAt(5));
        assertNull("Значение null в String", st.getStringAt(6));
    }
}

