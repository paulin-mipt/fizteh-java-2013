package ru.fizteh.fivt.students.asaitgalin.shell;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FilesystemController {
    private File currentDir;

    public FilesystemController() {
           currentDir = new File(".");
    }

    public File getCurrentDir() {
        return currentDir;
    }

    public File getFileFromName(String name) {
        File f = new File(name);
        if (!f.isAbsolute()) {
            f = new File(currentDir, name);
        }
        return f;
    }

    public boolean changeDir(String newPath) {
        File f = getFileFromName(newPath);
        if (f.exists() && f.isDirectory()) {
            try {
                currentDir = f.getCanonicalFile();
            } catch (IOException ioe) {
                // Do nothing
            }
            return true;
        }
        return false;
    }

    public void copyFile(File src, File dest) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dest);
            byte[] buffer = new byte[65536]; // 64 kb chunk size
            int readSize;
            while ((readSize = in.read(buffer)) > 0) {
                out.write(buffer, 0, readSize);
            }
        } catch (IOException ioe) {
            throw new IOException("cp: internal error");
        } finally {
            closeStream(in);
            closeStream(out);
        }
    }

    private void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ioe) {
                // Do nothing
            }
        }
    }
}
