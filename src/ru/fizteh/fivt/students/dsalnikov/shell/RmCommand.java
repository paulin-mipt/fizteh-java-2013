package ru.fizteh.fivt.students.dsalnikov.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RmCommand implements Command {
    public String getName() {
        return "rm";
    }

    public int getArgsCount() {
        return 1;
    }

    private void delete(File f) throws IOException {
        if (f.isDirectory()) {
            //если размер директории 0 можем просто удалить ее
            //если нет то перебираем все файлы внутри и вызываем удаление рекурсивно
            if (f.list().length == 0) {
                deleteFile(f);
            } else {
                String files[] = f.list();
                for (String s : files) {
                    File fileDelete = new File(f, s);
                    delete(fileDelete);
                }
                if (f.list().length == 0) {
                    deleteFile(f);
                }
            }
        } else {
            deleteFile(f);

        }
    }

    private void deleteFile(File f) throws IOException {
        if (!f.delete()) {
            throw new IOException("Something went wrong. File wasn't deleted. Try moar");
        }
    }

    public void execute(Object shell, String[] s) throws IOException {
        if (s.length != 2) {
            throw new IllegalArgumentException("wrong ammount of args. should be called with one arg");
        } else {
            ShellState sh = (ShellState)shell;
            File fi = new File(s[1]);
            if (!fi.isAbsolute()) {
                fi = new File(sh.getState(), s[1]);
            }
            if (!fi.exists()) {
                throw new FileNotFoundException("file doesn't seem to be sexisting");
            }
            delete(fi);
        }
    }
}
