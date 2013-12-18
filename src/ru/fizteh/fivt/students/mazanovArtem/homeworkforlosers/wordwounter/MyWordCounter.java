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
            InputStream tmpInput;
            try {
                tmpInput = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                output.write("<pre>file not found</pre>\n".getBytes());
                continue;
            }
            BufferedReader input = new BufferedReader(new InputStreamReader(tmpInput));
            if (!file.canRead()) {
                output.write("<pre>file not available</pre>\n".getBytes());
                continue;
            }
            if (!aggregate) {
                output.write(file.getName().getBytes(StandardCharsets.UTF_8));
                output.write(":\n".getBytes());
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
                    if (isChar(k)) {
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
                            tmpstr.append(k);
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
                    }
                }
                if (wrongWord) {
                    tmpstr = new StringBuilder();
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
                }
            }

            if (!aggregate) {
                for (String str : map.keySet()) {
                    String tmp = str + " " + map.get(str) + "\n";
                    output.write(tmp.getBytes());
                }
                map.clear();
            }
            input.close();
            tmpInput.close();
        }
        if (aggregate) {
            for (String str : map.keySet()) {
                String tmp = str + " " + map.get(str) + "\n";
                output.write(tmp.getBytes());
            }
        }
    }

    public static boolean isChar(char k) {
        if ((k >= 'a' && k <= 'z') || (k >= 'A' && k <= 'Z') || (k >= '0' && k <= '9') || (k == '-') || (k >= 'а')
                && (k <= 'я') || (k >= 'А') && (k <= 'Я')) {
            return true;
        } else {
            return false;
        }
    }
}
