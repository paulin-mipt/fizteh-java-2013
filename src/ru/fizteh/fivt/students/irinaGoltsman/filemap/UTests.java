package ru.fizteh.fivt.students.irinaGoltsman.filemap;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBTableProviderFactory;
import ru.fizteh.fivt.students.irinaGoltsman.shell.MapOfCommands;
import ru.fizteh.fivt.students.irinaGoltsman.shell.Shell;
import ru.fizteh.fivt.students.irinaGoltsman.shell.ShellCommands;

import java.io.*;
import java.util.Scanner;

public class UTests {
    DataBase dataBase;
    TableProvider tableProvider;
    @Rule
    public TemporaryFolder rootDBDirectory = new TemporaryFolder();

    @BeforeClass
    public static void setUp() {
        MapOfCommands cm = new MapOfCommands();
        cm.addCommand(new ShellCommands.Exit());
        cm.addCommand(new DBCommands.Put());
        cm.addCommand(new DBCommands.Get());
        cm.addCommand(new DBCommands.Remove());
        cm.addCommand(new DBCommands.Commit());
        cm.addCommand(new DBCommands.CreateTable());
        cm.addCommand(new DBCommands.Drop());
        cm.addCommand(new DBCommands.Use());
        cm.addCommand(new DBCommands.Size());
        cm.addCommand(new DBCommands.RollBack());
    }

    @Before
    public void init() throws IOException {
        TableProviderFactory factory = new DBTableProviderFactory();
        tableProvider = factory.create(rootDBDirectory.toString());
        dataBase = new DataBase(tableProvider);
    }


    @Test
    public void useNotExistingTable() throws IOException {
        String[] args = new String[1];
        args[0] = "use newTable";
        File output = new File("output.txt");
        output.createNewFile();
        try (PrintStream st = new PrintStream(output)) {
            System.setOut(st);
            Shell.shell(args);
            System.setOut(System.out);
            Scanner scan = new Scanner(output);
            Assert.assertEquals(true, scan.hasNext());
            String out = scan.nextLine();
            Assert.assertEquals("newTable not exists", out);
        }
    }

    @Test
    public void createTable() throws IOException {
        String[] args = new String[1];
        args[0] = "create newTable";
        File output = new File("output.txt");
        output.createNewFile();
        try (PrintStream st = new PrintStream(output)) {
            System.setOut(st);
            System.setErr(st);
            Shell.shell(args);
            System.setOut(System.out);
            System.setErr(System.err);
            Scanner scan = new Scanner(output);
            Assert.assertEquals(true, scan.hasNext());
            String out = scan.nextLine();
            Assert.assertEquals("Command 'create' has wrong arguments", out);
        }
    }

    @Test
    public void consoleApp() throws IOException {
        String[] args = new String[6];
        args[0] = "create check (String int long boolean float double byte String);";
        args[1] = "use check;";
        args[2] = "put keyX [\"keyX\",1024,1025,true,1024.1,1024.1,null,\"05bf9c3c5d9031e21babab85fd3bbb3cзначение\"];";
        args[3] = "commit;";
        args[4] = "put keyZ [\"keyZ\",40,41,true,40.1,40.1,null,\"8a14e407cadf8d9b8863ba0f93ee7b50значение\"];";
        args[5] = "exit";

        try {
            tableProvider.removeTable("check");
        } catch (IllegalStateException e) {
            //ignore
        }
        File output = new File("output.txt");
        output.createNewFile();
        try (PrintStream st = new PrintStream(output)) {
            System.setOut(st);
            System.setErr(st);

            Shell.shell(args);
            Scanner scan = new Scanner(output);
            Assert.assertEquals(true, scan.hasNext());
            String out = scan.nextLine();
            Assert.assertEquals("created", out);
            Assert.assertEquals(true, scan.hasNext());
            out = scan.nextLine();
            Assert.assertEquals("using check", out);
            Assert.assertEquals(true, scan.hasNext());
            out = scan.nextLine();
            Assert.assertEquals("new", out);
            Assert.assertEquals(true, scan.hasNext());
            out = scan.nextLine();
            Assert.assertEquals("1", out);
            Assert.assertEquals(true, scan.hasNext());
            out = scan.nextLine();
            Assert.assertEquals("new", out);
            Assert.assertEquals(false, scan.hasNext());

            System.setOut(System.out);
            System.setErr(System.err);
        }
        dataBase.closeDB();
    }
}
