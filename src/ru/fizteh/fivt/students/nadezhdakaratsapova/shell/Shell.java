package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.IOException;
import java.util.Scanner;

public class Shell {

    private CommandsController controller = new CommandsController();

    public void addCommand(Command cmd) {
        controller.addCmd(cmd);
    }

    public void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.print(/*currentDirectory.getCurDir().getAbsolutePath() +*/ "$ ");
                String inputString = scanner.nextLine();
                String[] commands = inputString.split(";");
                for (String command : commands) {
                    String[] splittedCommand = command.trim().split("\\s+");
                    controller.runCommand(splittedCommand);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            } catch (IllegalStateException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void batchMode(String inputString) throws IOException {
        String[] commands = inputString.split(";");
        for (String command : commands) {
            String[] splittedCommand = command.trim().split("\\s+");
            controller.runCommand(splittedCommand);
        }
    }
}
