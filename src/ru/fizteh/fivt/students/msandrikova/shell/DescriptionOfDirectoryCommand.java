package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class DescriptionOfDirectoryCommand extends Command {

    public DescriptionOfDirectoryCommand() {
        super("dir", 0);
    }
    
    @Override
    public void execute(String[] argumentsList, Shell myShell) {
        if (!super.getArgsAcceptor(argumentsList.length - 1, myShell.getIsInteractive())) {
            return;
        }
        
        File[] listOfFiles;
        listOfFiles = myShell.getCurrentDirectory().listFiles();
        
        Arrays.sort(listOfFiles, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                if (file1.isDirectory()) {
                    if (file2.isDirectory()) {
                        return file1.compareTo(file2);
                    } else {
                        return -1;
                    }
                } else {
                    if (file2.isDirectory()) {
                        return 1;
                    } else {
                        return file1.compareTo(file2);
                    }
                }
            }
        });
        for (File fileName : listOfFiles) {
            System.out.println(fileName.getName());
        }
    }

}
