package ru.fizteh.fivt.students.abramova.shell;

import java.io.*;

public class MakeDirectoryCommand extends Command {

    public MakeDirectoryCommand(String name) {
        super(name);
    }


    @Override
    public int doCommand(String[] args, Status status) throws IOException{
        Stage stage = status.getStage();
        if (stage == null) {
            throw new IllegalStateException(getName() + ": Command do not get stage");
        }
        File newDir = new File(stage.currentDirPath(), args[0]);
        if (!newDir.exists()) {
            if (!newDir.mkdir()) {
                //Не удалось создать директорию
                throw new IOException(getName() + ": Unexpected fail. Directory was not created");
            }
        } else {
            //Уже существует такой файл
            System.out.println(getName() + ": Directory \'" + args[0] + "\' already exists");
            return 3;
        }
        return 0;
    }

    @Override
    public boolean correctArgs(String[] args) {
        return args != null && args.length == 1;
    }
}
