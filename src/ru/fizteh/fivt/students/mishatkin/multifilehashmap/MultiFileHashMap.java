package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import ru.fizteh.fivt.students.mishatkin.shell.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Vladimir Mishatkin on 10/26/13
 */
public class MultiFileHashMap extends Shell {
	public static final int TABLE_OWNING_DIRECTORIES_COUNT = 16;

	public static void main(String[] args) {
		CommandSource source = (args.length > 0) ? new BatchCommandSource(args)
				: new StandardInputCommandSource(new Scanner(System.in));
		source.initCommands(getAllCommandsIncludingInheritanceDependencies());
		MultiFileHashMapRunner runner = new MultiFileHashMapRunner(source);
		String dbDirectory = System.getProperty("fizteh.db.dir");
		if (dbDirectory == null) {
			System.err.println("set \"fizteh.db.dir\" property before launch.");
			System.exit(1);
		} else {
			File dbFile = new File(dbDirectory);
			if (!dbFile.exists() || !dbFile.isDirectory()) {
				System.err.println("Specified dbDirectory is simply not a directory. Make sure your keyboard is not ejected, just in case.");
				System.exit(1);
			}
		}
		MultiFileHashMapReceiver receiver = new MultiFileHashMapReceiver(System.out, (args.length == 0), dbDirectory);
			runner.run(receiver);
	}

	protected static <Receiver extends CommandReceiver> Collection<Command<Receiver>> getMultiFileHashMapCommands() {
		List<Command<Receiver>> validCommands = new ArrayList<>();
		validCommands.add((Command<Receiver>) new CreateCommand<MultiFileHashMapReceiver>(null));
		validCommands.add((Command<Receiver>) new DropCommand<MultiFileHashMapReceiver>(null));
		validCommands.add((Command<Receiver>) new UseCommand<MultiFileHashMapReceiver>(null));

		validCommands.add((Command<Receiver>) new GetCommand<MultiFileHashMapReceiver>(null));
		validCommands.add((Command<Receiver>) new PutCommand<MultiFileHashMapReceiver>(null));
		validCommands.add((Command<Receiver>) new RemoveCommand<MultiFileHashMapReceiver>(null));
		return validCommands;
	}

	protected static <Receiver extends CommandReceiver> Collection<Command<Receiver>> getAllCommandsIncludingInheritanceDependencies() {
		List<Command<Receiver>> validCommands = (List<Command<Receiver>>) MultiFileHashMap.<Receiver>getMultiFileHashMapCommands();
		Collection<Command<Receiver>> superValidCommands = getShellCommands();
		validCommands.addAll(superValidCommands);
		return validCommands;
	}
}
