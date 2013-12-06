package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 11.10.13
 * Time: 19:13
 * To change this template use File | Settings | File Templates.
 */
public class CopyFiles {
    public static void copy(File from, File to) throws IOException {
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(from);
            outStream = new FileOutputStream(to);
            byte[] buffer = new byte[4096];
            int read = 0;
            while ((read = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, read);
            }
        } finally {
            inStream.close();
            outStream.close();
        }
    }

    public static void copyRecursively(File from, File to) throws IOException {
        if (from.isDirectory()) {
            File fromNew = new File(to.getCanonicalPath(), from.getName());
            if (!fromNew.mkdirs()) {
                throw new IOException("Unable to create this directory - " + fromNew.getCanonicalPath());
            }
            for (File f : from.listFiles()) {
                copyRecursively(f, fromNew);
            }
            return;
        }
        to = new File(to.getCanonicalPath() + File.separator + from.getName());
        if (!to.exists()) {
            to.createNewFile();
        }
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(from);
            outStream = new FileOutputStream(to);
            byte[] buffer = new byte[4096];
            int read = 0;
            while ((read = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, read);
            }
        } finally {
            streamClose(inStream);
            streamClose(outStream);
        }
    }

    private static void streamClose(Closeable s) throws IOException {
        if (s != null) {
            s.close();
        }
    }
}
