package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.test;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMap;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapProvider;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandShell;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestFileMap {

    private FileMap fileMap;
    private String nameTable;
    private CommandShell mySystem;
    private Path pathTables;
    private FileMapProvider multiMap;

    @Before
    public void setUp() {

        pathTables = Paths.get(".");
        mySystem = new CommandShell(pathTables.toString(), false, false);
        pathTables = pathTables.resolve("bdTest");
        try {
            mySystem.mkdir(new String[]{pathTables.toString()});
            multiMap = new FileMapProvider(pathTables.toAbsolutePath().toString());
        } catch (Exception e) {
            e.printStackTrace();
            FileMapUtils.getMessage(e);
        }

        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(String.class);
        list.add(Integer.class);

        try {
            nameTable = "table";
            fileMap = (FileMap) multiMap.createTable("table", list);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void correctGetNameShouldEquals() {
        assertEquals(nameTable, fileMap.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullKeyShouldFail() {
        fileMap.get(null);
    }

    @Test
    public void getPutOneKey() throws ParseException {
        Storeable st = multiMap.deserialize(fileMap, "<row><col>qwe</col><col>12</col></row>");
        fileMap.put("1", st);
        assertEquals(st, fileMap.get("1"));
        fileMap.remove("1");
    }

    @Test
    public void getPutManyKey() throws ParseException {
        Storeable st1 = multiMap.deserialize(fileMap, "<row><col>qwe1</col><col>1</col></row>");
        fileMap.put("1", st1);
        Storeable st2 = multiMap.deserialize(fileMap, "<row><col>qwe2</col><col>2</col></row>");
        fileMap.put("2", st2);
        Storeable st3 = multiMap.deserialize(fileMap, "<row><col>qwe3</col><col>3</col></row>");
        fileMap.put("3", st3);
        Storeable st4 = multiMap.deserialize(fileMap, "<row><col>qwe4</col><col>4</col></row>");
        fileMap.put("4", st4);
        assertEquals(st1, fileMap.get("1"));
        assertEquals(st4, fileMap.get("4"));
        assertEquals(st3, fileMap.get("3"));
        fileMap.remove("1");
        fileMap.remove("2");
        fileMap.remove("3");
        fileMap.remove("4");
    }

    @Test
    public void correctSizeAfterPut() throws ParseException {
        Storeable st1 = multiMap.deserialize(fileMap, "<row><col>qwe1</col><col>1</col></row>");
        fileMap.put("1", st1);
        fileMap.put("2", st1);
        fileMap.put("3", st1);
        fileMap.put("4", st1);
        assertEquals(4, fileMap.size());
        fileMap.remove("1");
        fileMap.remove("2");
        assertEquals(2, fileMap.size());
        fileMap.remove("1");
        fileMap.remove("2");
        fileMap.remove("3");
        fileMap.remove("4");
        assertEquals(0, fileMap.size());
    }

    @Test
    public void getOutKey() {
        assertEquals(null, fileMap.get("1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullKey() throws ParseException {
        Storeable st1 = multiMap.deserialize(fileMap, "<row><col>qwe1</col><col>1</col></row>");
        fileMap.put(null, st1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullValue() {
        fileMap.put("!23", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNlValue() throws ParseException {
        Storeable st1 = multiMap.deserialize(fileMap, "<row><col>qwe1</col><col>1</col></row>");
        fileMap.put("!23\nwerf", st1);
    }

    @Test()
    public void commitSimpleCount() throws IOException, ParseException {
        fileMap.commit();
        Storeable st1 = multiMap.deserialize(fileMap, "<row><col>qwe1</col><col>1</col></row>");
        fileMap.put("123", st1);
        assertEquals(1, fileMap.commit());
        fileMap.remove("123");
    }

    @Test()
    public void commitHardCount() throws IOException, ParseException {
        try {
            fileMap.remove("1");
            fileMap.remove("2");
            fileMap.commit();
            Storeable st1 = multiMap.deserialize(fileMap, "<row><col>qwe1</col><col>1</col></row>");
            Storeable st2 = multiMap.deserialize(fileMap, "<row><col>qwe1</col><col>2</col></row>");
            fileMap.put("1", st1);
            fileMap.put("2", st1);
            assertEquals(2, fileMap.commit());

            fileMap.put("1", st1);
            fileMap.remove("1");
            fileMap.put("1", st1);
            assertEquals(0, fileMap.commit());

            fileMap.put("1", st1);
            fileMap.remove("1");
            fileMap.put("1", st2);
            assertEquals(1, fileMap.commit());
            fileMap.remove("1");
            fileMap.remove("2");
            fileMap.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test()
    public void rollbackAfterRemove() throws IOException, ParseException {
        fileMap.remove("1");
        fileMap.remove("2");
        fileMap.commit();
        Storeable st1 = multiMap.deserialize(fileMap, "<row><col>qwe1</col><col>1</col></row>");
        Storeable st2 = multiMap.deserialize(fileMap, "<row><col>qwe1</col><col>2</col></row>");
        fileMap.put("1", st1);
        fileMap.put("2", st2);
        assertEquals(fileMap.rollback(), 2);
        assertEquals(null, fileMap.get("1"));
        assertEquals(null, fileMap.get("2"));
        fileMap.remove("1");
        fileMap.remove("2");
    }

    @Test()
    public void rollbackAfterEdit() throws IOException, ParseException {
        Storeable st1 = multiMap.deserialize(fileMap, "<row><col>qwe1</col><col>1</col></row>");
        fileMap.put("1", st1);
        fileMap.commit();
        Storeable st2 = multiMap.deserialize(fileMap, "<row><col>qwe1</col><col>2</col></row>");
        fileMap.put("1", st2);
        fileMap.rollback();
        assertEquals(st1, fileMap.get("1"));
    }

    @Test()
    public void checkGetColumnsCount() {
        assertEquals(2, fileMap.getColumnsCount());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void checkErrorGetColumnType() {
        fileMap.getColumnType(3);
    }

    @Test()
    public void checkOKGetColumnType() {
        assertEquals(String.class, fileMap.getColumnType(0));
    }

    @Test(expected = IllegalStateException.class)
    public void closeTableCallGetName() {
        fileMap.close();
        fileMap.getName();
    }

    @Test(expected = IllegalStateException.class)
    public void closeTableCallToString() {
        fileMap.close();
        fileMap.toString();
    }

    @Test(expected = IllegalStateException.class)
    public void closeTableCallGet() {
        fileMap.close();
        fileMap.get("132");
    }

    @Test(expected = IllegalStateException.class)
    public void closeTableCallCommit() {
        fileMap.close();
        fileMap.commit();
    }

    @Test(expected = IllegalStateException.class)
    public void closeTableCallRemove() {
        fileMap.close();
        fileMap.remove("123");
    }

    @Test(expected = IllegalStateException.class)
    public void closeTableCallRollback() {
        fileMap.close();
        fileMap.rollback();
    }

    @Test(expected = IllegalStateException.class)
    public void closeTableCallSize() {
        fileMap.close();
        fileMap.size();
    }

    @Test(expected = IllegalStateException.class)
    public void closeTableProviderCallTableSize() {
        multiMap.close();
        fileMap.size();
    }

    @Test(expected = IllegalStateException.class)
    public void closeTableProviderCallTableToString() {
        multiMap.close();
        fileMap.toString();
    }

    @Test(expected = IllegalStateException.class)
    public void closeTableProviderCallTableName() {
        multiMap.close();
        fileMap.getName();
    }

    @Test()
    public void correctToString() throws IOException {
        assertEquals(fileMap.toString(),
                String.format("%s[%s]", "FileMap", pathTables.resolve("table").toAbsolutePath().toString()));
    }

    @After
    public void tearDown() {
        multiMap.close();
        fileMap.close();
        try {
            mySystem.rm(new String[]{pathTables.toString()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
