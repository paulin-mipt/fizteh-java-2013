package ru.fizteh.fivt.students.vorotilov.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class InteractiveInput extends ConsoleInput {

    protected BufferedReader inputStream;

    public InteractiveInput() {
        commandsBuffer = new LinkedList<>();
        inputStream = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public String[] getNext() throws IOException, ExitCommand {
        if (commandsBuffer.isEmpty()) {
            System.out.print("$ ");
            String newLine = inputStream.readLine();
            if (newLine == null) {
                throw new ExitCommand();
            } else {
                String[] splittedInput = newLine.split(";");
                for (String i : splittedInput) {
                    commandsBuffer.offer(parseCommand(i));
                }
            }
        }
        return commandsBuffer.remove();
    }

}
