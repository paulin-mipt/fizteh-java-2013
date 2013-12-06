package ru.fizteh.fivt.students.krivchansky.shell;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class UtilMethods {
    
    public static String uniteItems(Collection<?> items, String separator) {
        boolean isFirstIteration = true;
        StringBuilder joinBuilder = new StringBuilder();
        for (Object item: items) {
            if(isFirstIteration) {
                isFirstIteration = false;
            } else {
                joinBuilder.append(separator);
            }
            joinBuilder.append(item.toString());
        }
        return joinBuilder.toString();
    }
    
    public static void closeCalm(Closeable something) {
        try {
            if (something != null) {
                something.close();
            }
        } catch (IOException e) {
            System.err.println("Error aquired while trying to close " + e.getMessage());
        }
    }
    
    public static void copyFile(File source, File dirDestination) throws SomethingIsWrongException {
        File copy = new File(dirDestination, source.getName());
        FileInputStream ofOriginal = null;
        FileOutputStream ofCopy = null;
        try {
            copy.createNewFile();
            ofOriginal = new FileInputStream(source);
            ofCopy = new FileOutputStream(copy);
            byte[] buf = new byte[4096]; //size of reading = 4kB
            int read = ofOriginal.read(buf);
            while(read > 0) {
                ofCopy.write(buf, 0, read);
                read = ofOriginal.read(buf);
            }
        } catch (FileNotFoundException e) {
            throw new SomethingIsWrongException ("This file or directory doesn't exist yet. " + e.getMessage());
        } catch (IOException e) {
            throw new SomethingIsWrongException ("Error while writing/reading file. " + e.getMessage());
        } finally {
            closeCalm(ofOriginal);
            closeCalm(ofCopy);
        }
    }
    
    public static File getAbsoluteName(String fileName, Shell.ShellState state) {
        File file = new File(fileName);
        
        if (!file.isAbsolute()){
            file = new File(state.getCurDir(), fileName);
        }
        return file;
    }
}