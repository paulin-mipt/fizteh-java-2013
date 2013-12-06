package ru.fizteh.fivt.students.krivchansky.shell;

import java.io.File;
import java.util.Arrays;
import java.util.List;


public class DirectoryInfoCommand implements Commands {
    
    public String getCommandName() {
        return "dir";
    }

    public int getArgumentQuantity() {
        return 0;
    }
    
    public void implement(String[] args, Shell.ShellState state) {
        File dir = new File(state.getCurDir());
        String[] files = dir.list();
        List<String> tempObj = Arrays.asList(files);
        if(tempObj.size() > 0) {
            String anotherTempObj = UtilMethods.uniteItems(tempObj, "\n");
            System.out.println(anotherTempObj);
        }
    }

}
