package ru.fizteh.fivt.students.dsalnikov.filemap;

import ru.fizteh.fivt.students.dsalnikov.shell.Command;

import java.io.FileNotFoundException;

public class PutCommand implements Command {

    public String getName() {
        return "put";
    }

    public int getArgsCount() {
        return 2;
    }

    public void execute(Object f, String[] args) {
        if (args.length != 3) {
            //throw new IllegalArgumentException("Incorrect usage of command put:wrong amount" +
            //        " of arguments");
            // изменил чтобы можно было использовать реализации Table
            System.out.println("Incorrect usage of command put:wrong amount" + " of arguments");
        } else {
            FileMapState filemap = (FileMapState) f;
            String temp = filemap.setValue(args[1], args[2]);
            if (temp == null) {
                System.out.println("new pair " + args[1] + " " + args[2]);
                return;
            } else {
                System.out.println("overwrite " + temp);
                return;
            }
        }
    }
}