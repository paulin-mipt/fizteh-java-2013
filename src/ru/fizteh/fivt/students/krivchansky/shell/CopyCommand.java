package ru.fizteh.fivt.students.krivchansky.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CopyCommand implements Commands {

    public String getCommandName() {
        return "cp";
    }
    
    public int getArgumentQuantity() {
        return 2;
    }
    
    
    public void implement(String[] args, Shell.ShellState state) throws SomethingIsWrongException {
        String from = args[0];
        String to = args[1];
        File source = UtilMethods.getAbsoluteName(from, state);
        File newPlace = UtilMethods.getAbsoluteName(to, state);
        if (!newPlace.isDirectory()) {
            if (!newPlace.exists()) {
                copyToNotExistingFile(source, newPlace);
                return;
            } else {
                throw new SomethingIsWrongException("This file already exists. ");
            }
        }
        mkCopy(source, newPlace);
    }
    
    
    private static void copyToNotExistingFile(File from, File to) throws SomethingIsWrongException {
        File copy = new File(to, to.getName());
        FileInputStream first = null; //file from copy was made
        FileOutputStream second = null; // file to
        try {
            first = new FileInputStream(from);
            second = new FileOutputStream(to);
            byte[]buf = new byte[4096];
            int read = first.read(buf);
            while (0 < read) {
                second.write(buf, 0, read);
                read = first.read(buf);
            }
        } catch (FileNotFoundException e) {
            throw new SomethingIsWrongException("File not found. " + e.getMessage());
        } catch (IOException e) {
            throw new SomethingIsWrongException("Error aquired while reading/writing a file. " + e.getMessage());
        } finally {
            UtilMethods.closeCalm(first);
            UtilMethods.closeCalm(second);
        }
    }
    
    protected static void copy(File from, File to) throws SomethingIsWrongException {
        File copy = new File(to, from.getName());
        FileInputStream first = null; //file from copy was made
        FileOutputStream second = null; // file to
        try {
            copy.createNewFile();
            first = new FileInputStream(from);
            second = new FileOutputStream(to);
            byte[]buf = new byte[4096];
            int read = first.read(buf);
            while (0 < read) {
                second.write(buf, 0, read);
                read = first.read(buf);
            }
        } catch (FileNotFoundException e) {
            throw new SomethingIsWrongException("File not found. " + e.getMessage());
        } catch (IOException e) {
            throw new SomethingIsWrongException("Error aquired while reading/writing a file. " + e.getMessage());
        } finally {
            UtilMethods.closeCalm(first);
            UtilMethods.closeCalm(second);
        }
    }
    
    private void mkCopy(File from, File to) throws SomethingIsWrongException {
        if (from.isFile()) {
            copy(from, to);
            return;
        }
        File newPlace = new File(to, from.getName());
        if (!newPlace.exists() || !newPlace.mkdir()) {
            throw new SomethingIsWrongException("Unable to create a new directory " + from.getName());
        }
        for (String temp : from.list()) {
            copy(new File(to, temp), newPlace); 
        }
    }
    

}
