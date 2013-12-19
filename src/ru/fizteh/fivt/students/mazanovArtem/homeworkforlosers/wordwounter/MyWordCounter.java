package ru.fizteh.fivt.students.mazanovArtem.homeworkforlosers.wordwounter;

import ru.fizteh.fivt.file.WordCounter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MyWordCounter implements WordCounter {

    public MyWordCounter() {

    }

    public void count(List<File> inputFiles, OutputStream output, boolean aggregate) throws IOException {
        if (inputFiles.isEmpty()) {
            throw new IllegalArgumentException("File list is empty");
        }
        HashMap<String, Integer> map = new HashMap<>();
        for (File file : inputFiles) {
            if (!aggregate) {
                output.write(file.getName().getBytes(StandardCharsets.UTF_8));
                output.write(":\n".getBytes());
            }
            if (!file.exists()) {
                if (!aggregate) {
                    output.write("file not found\n".getBytes());
                }
                continue;
            }
            if (!file.canRead()) {
                if (!aggregate) {
                    output.write("file not available\n".getBytes());
                }
                continue;
            }
            BufferedReader input = new BufferedReader(new FileReader(file));
            if (input == null) {
                throw new IOException("Can't create stream");
            }

            StringBuilder tmpstr = new StringBuilder();
            boolean dash = false;
            boolean wrongWord = false;
            char k;

            while (true) {
                String str = input.readLine();
                if (str == null) {
                    break;
                }
                for (int i = 0; i < str.length(); ++i) {
                    k = str.charAt(i);
                    if (Character.isLetterOrDigit(k)) {
                        tmpstr.append(k);
                    } else {
                        if (k == '-') {
                            if (dash) {
                                wrongWord = true;
                                tmpstr.append(k);
                            } else {
                                if (!tmpstr.toString().isEmpty()) {
                                    dash = true;
                                    tmpstr.append(k);
                                }
                            }
                        } else {
                            if (!tmpstr.toString().isEmpty()) {
                                if (wrongWord) {
                                    tmpstr = new StringBuilder();
                                    wrongWord = false;
                                } else {
                                    if (map.containsKey(tmpstr.toString())) {
                                        int count = map.get(tmpstr.toString()) + 1;
                                        map.put(tmpstr.toString().toLowerCase(), count);
                                    } else {
                                        map.put(tmpstr.toString().toLowerCase(), 1);
                                    }
                                    tmpstr = new StringBuilder();
                                }
                            }
                            dash = false;
                        }

                    }
                }
                if (wrongWord) {
                    tmpstr = new StringBuilder();
                    wrongWord = false;
                } else {
                    if (tmpstr.length() > 0) {
                        if (map.containsKey(tmpstr.toString())) {
                            int count = map.get(tmpstr.toString()) + 1;
                            map.put(tmpstr.toString().toLowerCase(), count);
                        } else {
                            map.put(tmpstr.toString().toLowerCase(), 1);
                        }
                        tmpstr = new StringBuilder();
                    }
                    dash = false;
                }
            }

            if (!aggregate) {
                for (String str : map.keySet()) {
                    String tmp = str + " " + map.get(str) + "\n";
                    output.write(tmp.getBytes());
                }
                map.clear();
            }
            try {
                input.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
        if (aggregate) {
            for (String str : map.keySet()) {
                String tmp = str + " " + map.get(str) + "\n";
                output.write(tmp.getBytes());
            }
        }
    }
}
