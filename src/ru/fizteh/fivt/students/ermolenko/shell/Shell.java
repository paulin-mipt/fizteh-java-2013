package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.IOException;
import java.util.Scanner;

public class Shell<State> {

    private State state;

    public Shell(State inState) {

        state = inState;
    }

    public void batchState(String[] args, Executor exec) throws IOException {

        StringBuilder tmp = new StringBuilder();
        for (String arg : args) {
            tmp.append(arg).append(" ");
        }

        String[] command = tmp.toString().split("\\;");

        String cmd = "";

        for (int i = 0; i < command.length - 1; ++i) {

            cmd = command[i].trim();
            if (cmd.equals("exit")) {
                break;
            }
            try {
                exec.execute(state, cmd);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }

    public void interactiveState(Executor exec) throws IOException {

        Scanner scanner = new Scanner(System.in);

        String[] cmd;
        while (true) {
            System.out.print("$ ");
            cmd = scanner.nextLine().trim().split("\\s*;\\s*");

            try {
                for (String aCmd : cmd) {
                    exec.execute(state, aCmd);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}