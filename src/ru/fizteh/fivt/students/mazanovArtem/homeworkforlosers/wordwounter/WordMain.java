package ru.fizteh.fivt.students.mazanovArtem.homeworkforlosers.wordwounter;

import ru.fizteh.fivt.file.WordCounter;
import ru.fizteh.fivt.file.WordCounterFactory;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;

public class WordMain {

    public static void main(String[] args) throws IOException {
        boolean aggregate = false;
        ArrayList<File> list = new ArrayList<>();
        File file = new File(".");
        Path path = file.toPath();
        File tmp = null;
        int start = 0;
        File outputFile;
        OutputStream output = null;
        boolean standOutput = false;
        WordCounterFactory factory = new MyWordCounterFactory();
        WordCounter counter = factory.create();
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
                            try {
                                tmp = path.resolve(args[2]).toFile().getCanonicalFile();
                            } catch (InvalidPathException e) {
                                throw new IllegalArgumentException("Wrong output filename");
                            }
                            start = 3;
                        } else {
                            throw new IllegalArgumentException("Few arguments");
                        }
                    }
                }
            } else {
                if (args[0].equals("-o")) {
                    if (args.length > 1) {
                        try {
                            tmp = path.resolve(args[1]).toFile().getCanonicalFile();
                        } catch (InvalidPathException e) {
                            throw new IllegalArgumentException("Wrong output filename");
                        }
                        start = 2;
                    } else {
                        throw new IllegalArgumentException("Few arguments");
                    }
                    if (args.length > 2) {
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
                output = System.out;
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
            if (start == args.length) {
                throw new IllegalArgumentException("Few arguments");
            }
            for (int i = start; i < args.length; ++i) {
                if (args[i] == null) {
                    break;
                }
                try {
                    tmp = path.resolve(args[i]).toFile().getCanonicalFile();
                } catch (InvalidPathException e) {
                    throw new IllegalArgumentException("Wrong input filename");
                }
                if (tmp.equals(outputFile)) {
                    throw new IllegalArgumentException("Output file is equals input file");
                }
                list.add(tmp);
            }
            counter.count(list, output, aggregate);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(1);
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
