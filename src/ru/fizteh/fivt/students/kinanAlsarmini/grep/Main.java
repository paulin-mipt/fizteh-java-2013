package ru.fizteh.fivt.students.kinanAlsarmini.grep;

import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        boolean invert = false;
        boolean countOnly = false;
        boolean flagsDone = false;

        String pattern = null;
        String outputFile = null;

        List<File> inputFiles = new ArrayList<File>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if (flagsDone) {
                    System.err.println("Invalid arguments format, usage: Grep [flags] [Files]");
                    System.exit(1);
                }
                if (args[i].equals("-i")) {
                    invert = true;
                } else if (args[i].equals("-c")) {
                    countOnly = true;
                } else if (args[i].equals("-p")) {
                    if (i == args.length - 1) {
                        System.err.println("Invalid flag format.");
                        System.exit(1);
                    } else if (pattern != null) {
                        System.err.println("Invalid flag format.");
                        System.exit(1);
                    } else {
                        pattern = args[i + 1];
                    }
                    i++;
                } else if (args[i].equals("-o")) {
                    if (i == args.length - 1) {
                        System.err.println("Invalid flag format.");
                        System.exit(1);
                    } else if (outputFile != null) {
                        System.err.println("Invalid flag format.");
                        System.exit(1);
                    } else {
                        outputFile = args[i + 1];
                    }
                    i++;
                }
            } else {
                flagsDone = true;
                inputFiles.add(new File(args[i]));
            }
        }

        OutputStream output = null;
        try {
            if (outputFile != null) {
                output = new FileOutputStream(outputFile);
            }

            MyGrepFactory factory = new MyGrepFactory();
            MyGrep grep = factory.create(pattern);

            if (countOnly) {
                if (outputFile != null) {
                    try (PrintWriter printer = new PrintWriter(output)) {
                        printer.println(grep.count(inputFiles, invert));
                    }
                } else {
                    System.out.println(grep.count(inputFiles, invert));
                }
            } else {
                if (outputFile != null) {
                    grep.find(inputFiles, output, invert);
                } else {
                    grep.find(inputFiles, System.out, invert);
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println("Some input files are missing.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Input / output error.");
            System.exit(1);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        }
    }
}
