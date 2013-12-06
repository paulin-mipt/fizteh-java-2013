package ru.fizteh.fivt.students.dubovpavel.shell2;

import ru.fizteh.fivt.students.dubovpavel.executor.Feeder;
import ru.fizteh.fivt.students.dubovpavel.shell2.performers.*;

public class Main {
    public static void main(String[] args) {
        DispatcherShellBuilder dispatcherShellBuilder = new DispatcherShellBuilder();
        dispatcherShellBuilder.addPerformer(new PerformerChangeDirectory());
        dispatcherShellBuilder.addPerformer(new PerformerCopy());
        dispatcherShellBuilder.addPerformer(new PerformerCreateDirectory());
        dispatcherShellBuilder.addPerformer(new PerformerExit());
        dispatcherShellBuilder.addPerformer(new PerformerMove());
        dispatcherShellBuilder.addPerformer(new PerformerPrintDirectoryContent());
        dispatcherShellBuilder.addPerformer(new PerformerPrintWorkingDirectory());
        dispatcherShellBuilder.addPerformer(new PerformerRemove());
        Feeder.feed(dispatcherShellBuilder, args);
    }
}
