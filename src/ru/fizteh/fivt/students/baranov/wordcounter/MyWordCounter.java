package ru.fizteh.fivt.students.baranov.wordcounter;

import ru.fizteh.fivt.file.WordCounter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MyWordCounter implements WordCounter {
    public String ls = System.lineSeparator();

    public void count(List<File> files, OutputStream out, boolean aggregate) throws IOException {
        Map<String, Integer> mapOfWords = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        if (files.isEmpty()) {
            throw new IllegalArgumentException("files for counting not found");
        }
        if (out == null) {
            throw new IllegalArgumentException("output is null");
        }


        for (int i = 0; i < files.size(); ++i) {
            File file = files.get(i);
            if (file == null) {
                throw new IllegalArgumentException("file is null");
            }

            if (!(file.exists())) {
                if (!aggregate) {
                    out.write((file.getName() + ":" + ls + "file not found" + ls).getBytes(StandardCharsets.UTF_8));
                }
                continue;
            }
            if (file.isHidden() || !(file.canRead())) {
                if (!aggregate) {
                    out.write((file.getName() + ":" + ls + "file not available" + ls).getBytes(StandardCharsets.UTF_8));
                }
                continue;
            }
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String str = scanner.nextLine();
                    String[] words = parse(str.trim());

                    for (int j = 0; j < words.length; ++j) {
                        if (mapOfWords.get(words[j]) == null) {
                            mapOfWords.put(words[j], 1);
                        } else {
                            mapOfWords.put(words[j], mapOfWords.get(words[j]) + 1);
                        }
                    }
                }
            }

            if (!aggregate) {
                out.write((file.getName() + ":" + ls).getBytes(StandardCharsets.UTF_8));
                printMap(mapOfWords, out);
                mapOfWords = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            }
        }
        if (aggregate) {
            printMap(mapOfWords, out);
        }
    }

    private void printMap(Map<String, Integer> map, OutputStream out) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            try {
                out.write((entry.getKey() + " " + entry.getValue() + ls).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    private String[] parse(String s) {
        List<String> listOfWords = new ArrayList<>();
        char prevCh = ' ';
        StringBuilder currentString = new StringBuilder("");
        for (int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);

            if (Character.isLetterOrDigit(ch) && (Character.isLetterOrDigit(prevCh) || Character.isSpaceChar(prevCh))) {
                currentString.append(ch);
                prevCh = ch;
                continue;
            }
            if (Character.isLetterOrDigit(ch) && prevCh == '-') {
                if (!(currentString.length() == 0)) {
                    currentString.append("-");
                    currentString.append(ch);
                    prevCh = ch;
                } else {
                    currentString.append(ch);
                    prevCh = ch;
                }
                continue;
            }
            if (ch == '-') {
                if (prevCh == '-' && !(currentString.length() == 0)) {
                    listOfWords.add(currentString.toString());
                    currentString = new StringBuilder("");
                }
                prevCh = ch;
                continue;
            }
            if (!(currentString.length() == 0)) {
                listOfWords.add(currentString.toString());
                currentString = new StringBuilder("");
            }
        }
        if (!(currentString.length() == 0)) {
            listOfWords.add(currentString.toString());
        }

        String[] simpleArray = new String[listOfWords.size()];
        return listOfWords.toArray(simpleArray);
    }
}
