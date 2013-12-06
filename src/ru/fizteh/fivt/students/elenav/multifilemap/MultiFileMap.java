package ru.fizteh.fivt.students.elenav.multifilemap;

import java.io.File;
import java.io.IOException;
import ru.fizteh.fivt.students.elenav.commands.CreateTableCommand;
import ru.fizteh.fivt.students.elenav.commands.DropCommand;
import ru.fizteh.fivt.students.elenav.commands.ExitCommand;
import ru.fizteh.fivt.students.elenav.commands.GetCommand;
import ru.fizteh.fivt.students.elenav.commands.PutCommand;
import ru.fizteh.fivt.students.elenav.commands.RemoveCommand;
import ru.fizteh.fivt.students.elenav.commands.UseCommand;
import ru.fizteh.fivt.students.elenav.utils.ExitException;

public class MultiFileMap {
    
    public static void main(String[] args) throws IOException {
        
        String property = System.getProperty("fizteh.db.dir");
        if (property == null) {
            System.err.println("db dir passed");
            System.exit(1); 
        }
        File f = new File(property);
        if (f.isFile()) {
            System.err.println("it is not dir, it is file");
            System.exit(1);
        }
        MultiFileMapState multi = new MultiFileMapState("MyFirstMultiFileMap", f, System.out);
        multi.addCommand(new CreateTableCommand(multi));
        multi.addCommand(new DropCommand(multi));
        multi.addCommand(new ExitCommand(multi));
        multi.addCommand(new GetCommand(multi));
        multi.addCommand(new PutCommand(multi));
        multi.addCommand(new RemoveCommand(multi));
        multi.addCommand(new UseCommand(multi));
        
        try {
            multi.run(args);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (ExitException err) {
            multi.write();
        }
        System.exit(0);
    }
    
}
