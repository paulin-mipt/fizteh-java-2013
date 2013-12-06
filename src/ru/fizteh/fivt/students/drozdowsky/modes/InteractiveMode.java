package ru.fizteh.fivt.students.drozdowsky.modes;

import ru.fizteh.fivt.students.drozdowsky.utils.Utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Scanner;

public class InteractiveMode<T> {
    T controller;

    public InteractiveMode(T controller) {
        this.controller = controller;
    }

    public void execute(HashMap<String, Method> commands) {
        Scanner in = new Scanner(System.in);
        while (true) {
            String[] args = Utils.scanArgs(in);
            if (args.length != 0) {
                PacketMode<T> pm = new PacketMode<T>(controller);
                pm.execute(args, commands);
            }
        }
    }
}
