package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class DirCommand implements Command {

    private ShellState curState;

    public DirCommand(ShellState state) {
        curState = state;

    }

    public String getName() {
        return "dir";
    }

    public void execute(String[] args) {
        if (curState.getCurDir().isDirectory()) {
            File[] fileList = curState.getCurDir().getAbsoluteFile().listFiles();
            Arrays.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            if (fileList.length > 0) {
                int i = 0;
                while (fileList[i].getName().charAt(0) == ('.')) {
                    ++i;
                }
                int length = fileList.length;

                for (int j = i; j < length; ++j) {
                    System.out.println(fileList[j].getName());
                }
            }

        }
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 0);
    }
}
