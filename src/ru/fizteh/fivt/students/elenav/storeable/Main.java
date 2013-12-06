package ru.fizteh.fivt.students.elenav.storeable;

import java.io.IOException;

import ru.fizteh.fivt.students.elenav.commands.CommitCommand;
import ru.fizteh.fivt.students.elenav.commands.DropCommand;
import ru.fizteh.fivt.students.elenav.commands.ExitCommand;
import ru.fizteh.fivt.students.elenav.commands.GetCommand;
import ru.fizteh.fivt.students.elenav.commands.PutCommand;
import ru.fizteh.fivt.students.elenav.commands.RemoveCommand;
import ru.fizteh.fivt.students.elenav.commands.RollbackCommand;
import ru.fizteh.fivt.students.elenav.commands.SizeCommand;
import ru.fizteh.fivt.students.elenav.commands.UseCommand;
import ru.fizteh.fivt.students.elenav.storeable.commands.CreateTableCommand;
import ru.fizteh.fivt.students.elenav.utils.ExitException;

public class Main {
    
    public static void main(String[] args) {
        try {
            
            String property = System.getProperty("fizteh.db.dir");
            
            StoreableTableProviderFactory factory = new StoreableTableProviderFactory();
            final StoreableTableProvider provider = (StoreableTableProvider) factory.create(property);

            StoreableTableState storeable = new StoreableTableState(null, null, System.out, provider);
            
            storeable.addCommand(new GetCommand(storeable));
            storeable.addCommand(new PutCommand(storeable));
            storeable.addCommand(new RemoveCommand(storeable));
            storeable.addCommand(new ExitCommand(storeable));
            storeable.addCommand(new CommitCommand(storeable));
            storeable.addCommand(new RollbackCommand(storeable));
            
            storeable.addCommand(new CreateTableCommand(storeable));
            storeable.addCommand(new DropCommand(storeable));
            storeable.addCommand(new UseCommand(storeable));
            storeable.addCommand(new SizeCommand(storeable));
        
            try {
                storeable.run(args);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            } catch (ExitException e) {
                System.exit(0);
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
