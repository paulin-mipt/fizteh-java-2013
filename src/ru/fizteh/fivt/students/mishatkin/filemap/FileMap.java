package ru.fizteh.fivt.students.mishatkin.filemap;

import ru.fizteh.fivt.students.mishatkin.shell.*;

import java.util.*;

/**
 * Created by Vladimir Mishatkin on 10/14/13
 */
public class FileMap extends Shell {
	public static void main(String[] args) {
		CommandSource source = (args.length > 0) ? new BatchCommandSource(args)
		                                         : new StandardInputCommandSource(new Scanner(System.in));
		source.initCommands(getAllCommandsIncludingInheritanceDependencies());
		FileMapRunner runner = new FileMapRunner(source);
		String dbDirectory = System.getProperty("fizteh.db.dir");
		String dbFileName = "db.dat";
		FileMapReceiver receiver = null;
		try {
			receiver = new FileMapReceiver(dbDirectory, dbFileName, (args.length == 0), System.out);
			runner.run(receiver);
		} catch (FileMapDatabaseException e) {
			System.err.println(e.getMessage());
		}
	}

	protected static <Receiver extends CommandReceiver> Collection<Command<Receiver>> getFileMapCommands() {
		List<Command<Receiver>> validCommands = new ArrayList<>();
		validCommands.add((Command<Receiver>) new GetCommand<FileMapReceiver>(null));
		validCommands.add((Command<Receiver>) new PutCommand<FileMapReceiver>(null));
		validCommands.add((Command<Receiver>) new RemoveCommand<FileMapReceiver>(null));
		return validCommands;
	}

	protected static <Receiver extends CommandReceiver> Collection<Command<Receiver>> getAllCommandsIncludingInheritanceDependencies() {
		List<Command<Receiver>> validCommands = (List<Command<Receiver>>) FileMap.<Receiver>getFileMapCommands();
		Collection<Command<Receiver>> superValidCommands = getShellCommands();
		validCommands.addAll(superValidCommands);
		return validCommands;
	}
}
