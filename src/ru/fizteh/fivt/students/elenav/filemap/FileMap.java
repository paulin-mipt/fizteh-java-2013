package ru.fizteh.fivt.students.elenav.filemap;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.elenav.commands.ExitCommand;
import ru.fizteh.fivt.students.elenav.commands.GetCommand;
import ru.fizteh.fivt.students.elenav.commands.PutCommand;
import ru.fizteh.fivt.students.elenav.commands.RemoveCommand;
import ru.fizteh.fivt.students.elenav.utils.ExitException;
import ru.fizteh.fivt.students.elenav.utils.Reader;

public class FileMap {
    
    static FileMapState createFileMapState(String name, File in) throws IOException {
        if (!in.exists()) {
            System.err.println("File was created");
            if (!in.createNewFile()) {
                throw new IOException("Can't create file");
            }
        }
        FileMapState fileMap = new FileMapState(name, in, System.out);
        fileMap.addCommand(new GetCommand(fileMap));
        fileMap.addCommand(new PutCommand(fileMap));
        fileMap.addCommand(new RemoveCommand(fileMap));
        fileMap.addCommand(new ExitCommand(fileMap));
        Reader.readFile(in, fileMap.map);
        return fileMap;
    }
    
    public static void main(String[] args) throws IOException {
        FileMapState fileMap = null;
        String property = System.getProperty("fizteh.db.dir");
        File in = new File(property, "db.dat");
        try {
            fileMap = createFileMapState("My first table", in);
            fileMap.run(args);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (ExitException f) {
            fileMap.writeFile(in);
            System.exit(0);
        }
        
    }

}
