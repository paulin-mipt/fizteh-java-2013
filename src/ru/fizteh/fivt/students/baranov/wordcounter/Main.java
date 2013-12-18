package ru.fizteh.fivt.students.baranov.wordcounter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("WordCounter needs arguments");
            System.err.println("Arguments:");
            System.err.println("Paths to files: file1.txt test/file2.txt //for example...");
            System.err.println("-o FILENAME //if you want to write results to file");
            System.err.println("-a //if you want to count number of words in all files");
            System.exit(1);
        }

        boolean output = false;
        boolean aggregate = false;
        boolean outputIsFound = false;
        List<File> files = new ArrayList<>();
        File outputFile = null;
        File outputFileMaybe;
        String dir = System.getProperty("user.dir");

        for (int i = 0; i < args.length; ++i) {
            if (output && !outputIsFound && args[i - 1].equals("-o")) {
                outputFileMaybe = new File(args[i]);

                if (!outputFileMaybe.isAbsolute()) {
                    outputFileMaybe = new File(dir, args[i]);
                }

                if (outputFileMaybe.isFile()) {
                    outputFile = outputFileMaybe;
                    outputIsFound = true;
                } else if (!(outputFileMaybe.exists()) && !isParameter(outputFileMaybe)) {
                    outputFile = outputFileMaybe;
                    try {
                        outputFile.createNewFile();
                    } catch (IOException e) {
                        System.err.println("can't create output: " + outputFile.toString());
                        System.exit(1);
                    }
                    outputIsFound = true;
                } else {
                    System.err.println(outputFileMaybe.toString() + " - isn't correct output");
                    System.exit(1);
                }
                continue;
            }
            if (args[i].equals("-o")) {
                output = true;
                continue;
            }
            if (args[i].equals("-a")) {
                aggregate = true;
                continue;
            }

            File newFile = new File(args[i]);
            if (!newFile.isAbsolute()) {
                newFile = new File(dir, args[i]);
            }

            files.add(newFile);
        }

        MyWordCounterFactory factory = new MyWordCounterFactory();
        MyWordCounter counter = factory.create();

        if (output) {
            if (outputIsFound) {
                try (OutputStream stream = new FileOutputStream(outputFile)) {
                    counter.count(files, stream, aggregate);
                } catch (IllegalArgumentException | IOException e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            } else {
                System.err.println("output not found");
                System.exit(1);
            }
        } else {
            OutputStream stream = System.out;
            try {
                counter.count(files, stream, aggregate);
            } catch (IllegalArgumentException | IOException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }

    public static boolean isParameter(File file) {
        return (file.getName().equals("-o") || file.getName().equals("-a"));
    }
}