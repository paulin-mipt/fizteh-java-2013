package ru.fizteh.fivt.students.dubovpavel.executor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Feeder {
    public static void feed(DispatcherBuilder dispatcherBuilder, String[] args) {
        Dispatcher dispatcher;
        if (args.length != 0) {
            dispatcherBuilder.setForwarding(true);
            dispatcher = dispatcherBuilder.construct();
            StringBuilder concatenator = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                concatenator.append(args[i]);
                concatenator.append(' ');
            }
            try {
                dispatcher.sortOut(concatenator.toString());
            } catch (Dispatcher.DispatcherException e) {
                System.exit(-1);
            }
        } else {
            dispatcherBuilder.setForwarding(false);
            dispatcher = dispatcherBuilder.construct();
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            while (dispatcher.online()) {
                System.out.print("$ ");
                try {
                    dispatcher.sortOut(input.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage()); // Something should go totally wrong.
                } catch (Dispatcher.DispatcherException e) {
                    throw new RuntimeException("Dispatcher exception forwarding was set in the interactive mode");
                }
            }
        }
    }
}
