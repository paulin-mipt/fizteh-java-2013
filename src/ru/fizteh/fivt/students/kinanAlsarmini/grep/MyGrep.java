package ru.fizteh.fivt.students.kinanAlsarmini.grep;

import ru.fizteh.fivt.file.Grep;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;

public class MyGrep implements Grep {
    private Pattern pattern;
    public MyGrep(Pattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Invalid pattern.");
        }

        this.pattern = pattern;
    }

    private List<String> find(File inputFile, boolean inverse) throws IOException {
        List<String> matches = new ArrayList<String>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                boolean found = matcher.find();
                    if ((!inverse && found) || (inverse && !found)) {
                    matches.add(line);
                }
            }
        }

        return matches;
    }

    @Override
    public void find(List<File> inputFiles, OutputStream output, boolean inverse) throws IOException {
        if (inputFiles == null || inputFiles.isEmpty()) {
            throw new IllegalArgumentException("Input files list is empty or not valid.");
        }

        if (output == null) {
            throw new IllegalArgumentException("find: Invalid output stream.");
        }

        try (PrintWriter printer = new PrintWriter(output)) {
            for (File inputFile: inputFiles) {
                printer.println(inputFile.getName() + ":");
                if (!inputFile.exists()) {
                    printer.println("file not found");
                } else if (!inputFile.canRead()) {
                    printer.println("file not available");
                } else {
                    List<String> matches = find(inputFile, inverse);    
                    
                    if (matches.isEmpty()) {
                        printer.println("no matches");
                    } else {
                        for (String match: matches) {
                            printer.println(match);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int count(List<File> inputFiles, boolean inverse) throws IOException {
        if (inputFiles == null || inputFiles.isEmpty()) {
            throw new IllegalArgumentException("Input files list is empty or not valid.");
        }

        int countMatches = 0;
        for (File inputFile: inputFiles) {
            countMatches += find(inputFile, inverse).size();
        }

        return countMatches;
    }
}
