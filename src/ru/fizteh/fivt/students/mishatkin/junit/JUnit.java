package ru.fizteh.fivt.students.mishatkin.junit;

import ru.fizteh.fivt.students.mishatkin.multifilehashmap.*;
import ru.fizteh.fivt.students.mishatkin.shell.*;

import java.io.File;
import java.util.*;

/**
 * Created by Vladimir Mishatkin on 11/4/13
 */
public class JUnit extends MultiFileHashMap {
	public static void main(String[] args) {
		CommandSource source = (args.length > 0) ? new BatchCommandSource(args)
				: new StandardInputCommandSource(new Scanner(System.in));
		source.initCommands(getAllCommandsIncludingInheritanceDependencies());
		ShellRunner runner = new MultiFileHashMapRunner(source);
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
		ShellReceiver receiver = new JUnitReceiver(System.out, (args.length == 0), dbDirectory);
		runner.run(receiver);
	}

	protected static <Receiver extends CommandReceiver> Collection<Command<Receiver>> getJUnitCommands() {
		List<Command<Receiver>> validCommands = new ArrayList<>();
		validCommands.add((Command<Receiver>) new SizeCommand<JUnitReceiver>(null));
		validCommands.add((Command<Receiver>) new CommitCommand<JUnitReceiver>(null));
		validCommands.add((Command<Receiver>) new RollbackCommand<JUnitReceiver>(null));
		return validCommands;
	}

	protected static <Receiver extends CommandReceiver> Collection<Command<Receiver>> getAllCommandsIncludingInheritanceDependencies() {
		List<Command<Receiver>> validCommands = (List<Command<Receiver>>) JUnit.<Receiver>getJUnitCommands();
		Collection<Command<Receiver>> superValidCommands = MultiFileHashMap.getAllCommandsIncludingInheritanceDependencies();
		validCommands.addAll(superValidCommands);
		return validCommands;
	}
}
