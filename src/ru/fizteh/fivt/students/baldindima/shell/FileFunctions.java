package ru.fizteh.fivt.students.baldindima.shell;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileFunctions {
    public static File pwd;

    public FileFunctions() throws IOException {
        pwd = new File("").getAbsoluteFile();
    }

    public static File toFile(String path) throws IOException{
    	File file = new File(path);
    	if (file.isAbsolute()){
    		pwd = file.getCanonicalFile();
    		return file.getCanonicalFile();
    	} else {
    		pwd = new File(path).getCanonicalFile();
    	}
    	return pwd;
    }
    public static void changeDir(String path) throws IOException {
        File f = getAbsolute(path);
        if (f.exists() && f.isDirectory()) {
            pwd = f;
        } else {
            throw new IOException("Wrong path");
        }

        //System.out.println(pwd.getCanonicalPath());

    }

    public static String getCurrentDir() throws IOException {
        return pwd.getCanonicalPath();

    }

    public static File getAbsolute(String path) {
        File f = new File(path);
        if (!f.isAbsolute()) {
            f = new File(pwd + File.separator + path);
        }
        return f;
    }

    public static void createDir(String path) throws IOException {
        File f = getAbsolute(path);
        if (!f.mkdir()) {
            throw new IOException("Cannot create such directory");
        }
    }

    public static void delete(String path) throws IOException {
        File copyPwd = pwd;
        changeDir(path);
        if (pwd.exists()) {
            deleteSmth(pwd);

        } else {
            throw new IOException("No such file or directory");
        }
        pwd = copyPwd;
    }

    private static void deleteSmth(File file) throws IOException {
        if (file.isFile()) {
            if (!file.delete()) {
                throw new IOException("No such file or directory");
            }
        } else {
            for (String s : file.list()) {
                deleteSmth(getAbsolute(file.getCanonicalPath() + File.separator + s));
            }

            if (!file.delete()) {
                throw new IOException("No such file or directory");
            }
        }
    }

    public static void move(String[] arguments) throws IOException {
        File first = getAbsolute(arguments[1]);
        File second = getAbsolute(arguments[2]);
        if (!first.exists()) {
            throw new IOException("No such file or directory");
        }
        copy(first, second);
        deleteSmth(first);
    }

    static void toClose(Closeable object) {
        if (object != null) {
            try {
                object.close();
            } catch (Exception e) {
            }
        }
    }

    public static void printDir() throws IOException {
        for (String s : pwd.list()) {
            System.out.println(s);
        }
        System.out.println();

    }

    public static void readyToCopy(String[] arguments) throws IOException {
        File first = getAbsolute(arguments[1]);
        File second = getAbsolute(arguments[2]);
        if (!first.exists()) {
            throw new IOException("No such file or directory");
        }
        copy(first, second);
    }

    static void copy(File first, File second) throws IOException {
        
    	if (first.isFile()) {
            if (first.equals(second)) {
                throw new IOException("Cannot copy file to itself");
            }
            if (second.isDirectory()){
            	second = new File(second.getCanonicalPath() + File.separator + first.getName());
            }
            FileInputStream in = null;
            FileOutputStream out = null;
            try {
                in = new FileInputStream(first);
                out = new FileOutputStream(second);
                int length;
                byte[] buffer = new byte[4096];
                while (true) {
                    length = in.read(buffer);
                    if (length < 0) {
                        break;
                    }
                    out.write(buffer, 0, length);
                }
            } finally {
                toClose(in);
                toClose(out);
            }
            
        } else {
            if (!second.mkdir()) {
                if (!second.exists()) {
                    throw new IOException("No such file or directory");
                }
                second = new File(second.getCanonicalPath() + File.separator + first.getName());
                second.mkdir();
            }
            for (String s : first.list()) {
                copy(getAbsolute(first.getAbsoluteFile() + File.separator + s),
                        getAbsolute(second.getAbsoluteFile() + File.separator + s));

            }
        }


    }


}
