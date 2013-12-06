package ru.fizteh.fivt.students.drozdowsky.modes;

import java.lang.reflect.Method;
import java.util.HashMap;

public class ModeController<T> {
    T controller;
    public ModeController(T controller) {
        this.controller = controller;
    }

    public void execute(HashMap<String, Method> commands, String[] args) {
        if (args.length == 0) {
            InteractiveMode<T> im = new InteractiveMode<>(controller);
            im.execute(commands);
        } else {
            PacketMode<T> pm = new PacketMode<>(controller);
            pm.execute(args, commands, true);
        }
    }
}
