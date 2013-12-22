package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.commands.ExitCommand;
import ru.fizteh.fivt.students.nadezhdakaratsapova.commands.GetCommand;
import ru.fizteh.fivt.students.nadezhdakaratsapova.commands.PutCommand;
import ru.fizteh.fivt.students.nadezhdakaratsapova.commands.RemoveCommand;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Shell;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.StringMethods;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        try {
            File dataFile = new File(System.getProperty("fizteh.db.dir"), "db.dat");
            if (!dataFile.exists()) {
                dataFile.createNewFile();

            }
            FileMapState state = new FileMapState(dataFile);
            Shell fileMap = new Shell();
            fileMap.addCommand(new GetCommand(state));
            fileMap.addCommand(new PutCommand(state));
            fileMap.addCommand(new RemoveCommand(state));
            fileMap.addCommand(new ExitCommand(state));
            state.dataStorage.load();
            if (args.length == 0) {
                fileMap.interactiveMode();
            } else {
                String arguments = StringMethods.join(Arrays.asList(args), " ");
                try {
                    fileMap.batchMode(arguments);
                    state.dataStorage.writeToDataBase();
                } catch (IOException e) {
                    state.dataStorage.writeToDataBase();
                    System.err.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("not managed to create file");
            System.exit(1);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }
}
