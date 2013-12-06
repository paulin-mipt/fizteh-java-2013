package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Shell {
    private CommandsMap cm = null;
    private CurrentStatus currentStatus = null;
    private String startInvitation = " $ ";


    private void interactiveMode() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                if (currentStatus == null) {
                    System.out.print(startInvitation);
                } else {
                    System.out.print(currentStatus.getCurrentDirectory() + " " + startInvitation);
                }
                String s = sc.nextLine();
                cm.execute(s);
            } catch (IOException e) {
                System.err.println("Error! " + e.getMessage());
            }
        }
    }

    private void packageMode(String[] args) {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length - 1; ++i) {
                sb.append(args[i] + ' ');
            }
            sb.append(args[args.length - 1]);
            cm.execute(sb.toString());
            cm.execute("exit");
        } catch (IOException e) {
            System.err.println("Error! " + e.getMessage());
            System.exit(1);
        }
    }


    public Shell() {
        cm = new CommandsMap();
    }

    public void addCommand(Commands command) {
        cm.addCommand(command);
    }

    public void changeInvitation(String invitation) {
        startInvitation = invitation;
    }

    public void initializeBasicCommands() {
        currentStatus = new CurrentStatus(new File("."));
        cm.addCommand(new PrintWorkingDirectory(currentStatus));
        cm.addCommand(new ChangeDirectory(currentStatus));
        cm.addCommand(new MakeDirectory(currentStatus));
        cm.addCommand(new Directory(currentStatus));
        cm.addCommand(new Remove(currentStatus));
        cm.addCommand(new Copy(currentStatus));
        cm.addCommand(new Move(currentStatus));
        cm.addCommand(new Exit(currentStatus));
    }

    public void initializeBasicCommands(CurrentStatus cs) {
        currentStatus = cs;
        cm.addCommand(new PrintWorkingDirectory(currentStatus));
        cm.addCommand(new ChangeDirectory(currentStatus));
        cm.addCommand(new MakeDirectory(currentStatus));
        cm.addCommand(new Directory(currentStatus));
        cm.addCommand(new Remove(currentStatus));
        cm.addCommand(new Copy(currentStatus));
        cm.addCommand(new Move(currentStatus));
        cm.addCommand(new Exit(currentStatus));
    }

    public void start(String[] args) {
        if (args.length == 0) {
            interactiveMode();
        } else {
            packageMode(args);
        }
    }

    public void executeCommand(String args) throws IOException {
        cm.execute(args);
    }

    public void removeCommand(String commandName) throws IOException {
        cm.removeCommand(commandName);
    }

}
