package ru.fizteh.fivt.students.dubovpavel.storeable.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageAccessible;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.performers.PerformerCreate;
import ru.fizteh.fivt.students.dubovpavel.storeable.TableStoreableBuilder;
import ru.fizteh.fivt.students.dubovpavel.storeable.TypeNamesMatcher;

import java.util.ArrayList;

public class PerformerCreateStoreable<D extends Dispatcher & StorageAccessible> extends PerformerCreate<D> {
    private TableStoreableBuilder builder;

    public PerformerCreateStoreable(TableStoreableBuilder dataBaseBuilder) {
        builder = dataBaseBuilder;
    }

    private void testType(ArrayList<Class<?>> fields, String type, D dispatcher) throws PerformerException {
        Class<?> caster = TypeNamesMatcher.CLASS_BY_NAME.get(type);
        if (caster == null) {
            throw new PerformerException(dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("wrong type (Type %s is not supported)", type)));
        } else {
            fields.add(caster);
        }
    }

    @Override
    public boolean pertains(Command command) {
        return command.getHeader().equals("create") && command.argumentsCount() == 2
                && command.getArgument(1).charAt(0) == '(';
    }

    @Override
    public void execute(D dispatcher, Command command) throws PerformerException {
        ArrayList<Class<?>> fields = new ArrayList<>();
        String list = command.getArgument(1);
        String[] types = list.substring(1, list.length() - 1).split("\\s+");
        if (types.length == 0) {
            throw new PerformerException(dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("wrong type (0 types given)")));
        }
        for (String type : types) {
            testType(fields, type, dispatcher);
        }
        builder.setFields(fields);
        dispatcher.getStorage().create(command.getArgument(0));
    }
}
