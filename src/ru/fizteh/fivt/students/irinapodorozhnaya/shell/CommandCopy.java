package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CommandCopy extends AbstractCommand {
    private StateShell state;
    public CommandCopy(StateShell st) {
        super(2);
        state = st;
    }
    
    public String getName() {
        return "cp";
    }
    
    public void execute(String[] args) throws IOException {
        File source = state.getFileByName(args[1]);
        File dest = state.getFileByName(args[2]);
        try {
            if (source.equals(dest)) {
                throw new IOException("cp: '" + source.getName()
                        + "' can't copy object to the same object");
            } else if (source.isFile() && dest.isDirectory()) {
                copyFileToDirectory(source, dest);
            } else if (source.isFile()) {
                copyFileToFile(source, dest);
            } else if (source.isDirectory() && dest.isDirectory()) {
                copyDirectoryToDirectory(source, dest);
            } else {
                throw new IOException("cp: can't copy from '" + source.getName()
                        +  "' to '" + dest.getName());
            }
        } catch (FileNotFoundException f) {
            throw new IOException(f);
        }
    }
    
    private void copyFileToFile(File source, File dest) throws IOException, FileNotFoundException {
        if (!dest.createNewFile()) {
            throw new IOException("cp: '" + dest.getName()
                    + "' can't create file or this file already exists");
        }
        FileInputStream in = new FileInputStream(source);
        FileOutputStream out = new FileOutputStream(dest);
        byte[] buffer = new byte[8192];
        int count = 0;
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
        in.close();
        out.close();
    }
    
    private void copyFileToDirectory(File source, File dest) throws IOException, FileNotFoundException {
        File destination = new File(dest, source.getName());
        copyFileToFile(source, destination);
    }
    
    private void copyDirectoryToDirectory(File source, File dest) throws FileNotFoundException, IOException {
        if (source.isDirectory()) {
            for (File s: source.listFiles()) {
                File destination = new File(dest, s.getName());
                destination.createNewFile();
                copyDirectoryToDirectory(s, destination);
            }
        } else {
            copyFileToDirectory(source, dest);
        }
    }
}
