package ru.fizteh.fivt.students.mikhaylova_daria.db;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;



import static org.junit.Assert.*;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;


public class TesterOfTableProviderFactoryAndTableProvider {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private File mainDir;
    private File goodTable;
    private File goodTableSign;
    private File badTableEmpty;
    private File badTableEmptySign;
    private TableProviderFactory factory;
    private ArrayList<Class<?>> goodTypeList;
    private ArrayList<Object> goodValueList;
    private ArrayList<Object> wrongValueList;

    @Before
    public void before() {
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
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.toString());
            System.exit(1);
        }
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
        goodValueList.add(integ);
        goodValueList.add(integ.byteValue());
        goodValueList.add(integ.longValue());
        goodValueList.add(integ.floatValue());
        goodValueList.add(integ.doubleValue());
        goodValueList.add(true);
        goodValueList.add("123");
        wrongValueList = new ArrayList<Object>();
        wrongValueList.add(integ);
        wrongValueList.add(integ.byteValue());
        wrongValueList.add(integ.longValue());
        wrongValueList.add(integ.floatValue());
        wrongValueList.add(integ.doubleValue());
        wrongValueList.add("123");
        wrongValueList.add(true);
    }

    @After
    public void after() {
        folder.delete();
    }


    @Test(expected = IllegalArgumentException.class)
    public void createTableManagerByNullStringShouldFail() {
        TableProviderFactory factory = new TableManagerFactory();
        try {
            TableProvider obj = factory.create(null);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createdTableByNullStringShouldFail() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.createTable(null, goodTypeList);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createdTableByNullTypeListShouldFail() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.createTable("table", null);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createdTableWrongTypeListShouldFail() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            goodTypeList.add(Short.class);
            provider.createTable("table", goodTypeList);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createdTableByEmptyTypeListShouldFail() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.createTable("table", new ArrayList<Class<?>>());
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }


    @Test(expected = IllegalArgumentException.class)
    public void createTableSpaceNameShouldFail() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.createTable(" ", goodTypeList);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableBadCharInNameShouldFail1() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.createTable("a/b", goodTypeList);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableBadCharInNameShouldFail2() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.createTable("a\\b", goodTypeList);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableBadCharInNameShouldFail3() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.createTable("..", goodTypeList);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableBadCharInNameShouldFail4() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.createTable(".", goodTypeList);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test
    public void createExistingTableShouldReturnNull() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            assertNull("Не работает createTable: не находит существующую таблицу",
                    provider.createTable(goodTable.getName(), goodTypeList));
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }


    @Test(expected = IllegalArgumentException.class)
    public void getSpaceTableShouldFail() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            Table table = provider.getTable("\n");
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTableManagerByEmptyListShouldFail() {
        TableProviderFactory factory = new TableManagerFactory();

        try {
            TableProvider obj = factory.create(null);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullNameTableShouldFail() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            Table table = provider.getTable(null);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTableWithoutSignFileShouldFail() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            Table table = provider.getTable(badTableEmpty.getName());
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTableWithWrongSignFileShouldFail() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            Table table = provider.getTable(badTableEmpty.getName());
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test
    public void getExistingTableGetNameShouldRefAndCorrectName() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            Table table = provider.getTable(goodTable.getName());
            assertNotNull("не работает getTable()", table);
            assertEquals("не работает getTable() или getName()", table.getName(), goodTable.getName());
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test
    public void getNonexistentTableGetNameShouldRefAndCorrectName() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            assertNull("не работает getTable() : возвращает не null от несуществующий таблицы",
                    provider.getTable("nonexistentTable"));
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test
    public void doubleGetTableEquals() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            assertEquals("не работает getTable(): вызванное дважды с тем же аргументов возвращает разные объекты",
                    provider.getTable(goodTable.getName()), provider.getTable(goodTable.getName()));
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableByNullStringShouldFail() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.removeTable(null);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableNlShouldFail() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.removeTable("\t");
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableBadCharInNameShouldFail1() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.removeTable("a/b");
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableBadCharInNameShouldFail2() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.removeTable("a\\b");
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableBadCharInNameShouldFail3() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.removeTable(".");
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableBadCharInNameShouldFail4() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.removeTable("..");
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }


    @Test(expected = IllegalStateException.class)
    public void removeNonexistentTableShouldFail() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.removeTable("nonexistentTable");
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeNullOrEmptyOrNlString() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            Table table = provider.getTable(goodTable.getName());
            assertNotNull("не работает getTable для существующей таблицы", table);
            provider.deserialize(table, null);
            provider.deserialize(table, "");
            provider.deserialize(table, "\n");
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        } catch (ParseException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = ParseException.class)
    public void deserializeIncorrectStringEmptyElement() throws Exception {
        TableProvider provider = factory.create(mainDir.toString());
        Table table = provider.getTable(goodTable.getName());
        assertNotNull("не работает getTable для существующей таблицы", table);
        provider.deserialize(table, "<row></row>");
    }

    @Test(expected = ParseException.class)
    public void deserializeIncorrectStringWrongNumberOfColumn() throws Exception {
        TableProvider provider = factory.create(mainDir.toString());
        Table table = provider.getTable(goodTable.getName());
        assertNotNull("не работает getTable для существующей таблицы", table);
        provider.deserialize(table, "<row><null/><null/><null/><null/></row>");
    }

    @Test(expected = ParseException.class)
    public void deserializeIncorrectStringWrongNumberOfColumn2() throws Exception {
        TableProvider provider = factory.create(mainDir.toString());
        Table table = provider.getTable(goodTable.getName());
        assertNotNull("не работает getTable для существующей таблицы", table);
        provider.deserialize(table, "<row><col>12</col></row>");
    }

    @Test(expected = ColumnFormatException.class)
    public void deserializeIncorrectStringWrongTypeColumn() throws Exception {
        TableProvider provider = factory.create(mainDir.toString());
        Table table = provider.getTable(goodTable.getName());
        assertNotNull("не работает getTable для существующей таблицы", table);
        provider.deserialize(table, "<row><col>12.3</col><null/><col> value </col><null/><null/><null/><null/></row>");
    }


    @Test
    public void deserializeCorrect() throws Exception {
        TableProvider provider = factory.create(mainDir.toString());
        Table table = provider.getTable(goodTable.getName());
        assertNotNull("не работает getTable для существующей таблицы", table);
        Storeable stor = provider.deserialize(table,
                "<row><col>12</col><col>29</col><col>1234678</col><null/><null/><null/><col>value</col></row>");
        assertNotNull("не работает deserialize для корректного значения", stor);
        assertEquals("не работают deserialize или serialize для корректных значений", provider.serialize(table, stor),
                "<row><col>12</col><col>29</col><col>1234678</col><null/><null/><null/><col>value</col></row>");
    }


    @Test
    public void deserializeCorrect2() throws Exception {
        TableProvider provider = factory.create(mainDir.toString());
        Table table = provider.getTable(goodTable.getName());
        assertNotNull("не работает getTable для существующей таблицы", table);
        Storeable stor = provider.deserialize(table,
                "<row><col>12</col><col>29</col><null/><col>12.2</col><col>12.2</col><col>true</col><null/></row>");
        assertNotNull("не работает deserialize для корректного значения", stor);
        assertEquals("не работают deserialize или serialize для корректных значений", provider.serialize(table, stor),
                "<row><col>12</col><col>29</col><null/><col>12.2</col><col>12.2</col><col>true</col><null/></row>");
    }


    @Test(expected = IllegalArgumentException.class)
    public void deserializeNullTable() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            Table table = provider.getTable(goodTable.getName());
            assertNotNull("не работает getTable для существующей таблицы", table);
            provider.deserialize(table, null);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        } catch (ParseException e) {
            fail();
            e.printStackTrace();
        }
    }


    @Test(expected = IllegalArgumentException.class)
    public void createForNullTable() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.createFor(null);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createForNullTableWithList() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            provider.createFor(null, goodValueList);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createForNullList() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            Table table = provider.getTable(goodTable.getName());
            provider.createFor(table, null);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createForEmptyList() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            Table table = provider.getTable(goodTable.getName());
            provider.createFor(table, new ArrayList<Object>());
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void createForWrongLengthList() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            Table table = provider.getTable(goodTable.getName());
            ArrayList<Object> array = new ArrayList<Object>();
            array.add("123");
            provider.createFor(table, array);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = ColumnFormatException.class)
    public void createForWrongList2() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            Table table = provider.getTable(goodTable.getName());
            provider.createFor(table, wrongValueList);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void createForWrongLengthListShouldBoundIndex() {
        try {
            TableProvider provider = factory.create(mainDir.toString());
            Table table = provider.getTable(goodTable.getName());
            goodValueList.add("456");
            provider.createFor(table, goodValueList);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

}

