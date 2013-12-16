package ru.fizteh.fivt.students.mazanovArtem.homeworkforlosers;

import ru.fizteh.fivt.file.WordCounter;
import ru.fizteh.fivt.file.WordCounterFactory;

import java.io.*;
import java.util.ArrayList;

public class WordMain {

    public static File appendPath(String path, File curDir) throws IOException {
        File tmp;
        if (path.equals("")) {
            return curDir;
        } else {
            tmp = new File(path);
        }
        File tmpStr;
        if (tmp.isAbsolute()) {
            return tmp.getAbsoluteFile();
        } else {
            tmp = new File(curDir.getAbsolutePath() + File.separator + path);
            try {
                tmpStr = tmp.getCanonicalFile();
            } catch (IOException e) {
                throw new IOException("Can't get canonical path");
            }
            return tmpStr;
        }
    }

    public static void main(String[] args) throws IOException {
        boolean aggregate = false;
        ArrayList<File> list = new ArrayList<>();
        File file = new File(".");
        File tmp = null;
        int start = 0;
        File outputFile;
        OutputStream output = null;
        boolean standOutput = false;
        WordCounterFactory factory = new MyWordCounterFactory();
        WordCounter counter = factory.create();
        /*args = new String[3];
        args[0] = "-a";
        args[1] = "-o";
        args[2] = "/home/tema/Documents/java/3.txt";
        args[3] = "/home/tema/Documents/java/1.txt";
        args[4] = "/home/tema/Documents/java/2.txt";
        args[5] = "/home/tema/Documents/java/4.txt";*/
        try {
            if (args.length == 0) {
                throw new IllegalArgumentException("Few arguments");
            }
            if (args[0].equals("-a")) {
                aggregate = true;
                start = 1;
                if (args.length > 1) {
                    if (args[1].equals("-o")) {
                        if (args.length > 2) {
                            tmp = appendPath(args[2], file);
                            start = 3;
                        } else {
                            throw new IllegalArgumentException("Few arguments");
                        }
                    }
                }
            } else {
                if (args[0].equals("-o")) {
                    if (args.length >= 2) {
                        tmp = appendPath(args[1], file);
                        start = 2;
                    } else {
                        throw new IllegalArgumentException("Few arguments");
                    }
                    if (args.length >= 3) {
                        if (args[2].equals("-a")) {
                            aggregate = true;
                            start = 3;
                        }
                    } else {
                        throw new IllegalArgumentException("Few arguments");
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        try {
            if (tmp == null) {
                output = new FileOutputStream(FileDescriptor.out);
                standOutput = true;
            } else {
                if (!tmp.exists()) {
                    boolean asd = tmp.createNewFile();
                    if (!asd) {
                        throw new IOException("Can't create output file");
                    }
                }
                if (tmp.canWrite()) {
                    output = new FileOutputStream(tmp);
                } else {
                    throw new IllegalArgumentException("Access denied");
                }
            }
            outputFile = tmp;
            for (int i = start; i < args.length; ++i) {
                if (args[i] == null) {
                    break;
                }
                tmp = appendPath(args[i], file);
                if (tmp.equals(outputFile)) {
                    throw new IllegalArgumentException("Output file is equals input file");
                }
                if (tmp.exists()) {
                    list.add(tmp);
                } else {
                    throw new IllegalArgumentException(tmp.getName() + " not exists");
                }
            }
            counter.count(list, output, aggregate);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } finally {
            if ((!standOutput) && (output != null)) {
                try {
                    output.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        }
    }
}
