package ru.fizteh.fivt.students.mazanovArtem.homeworkforlosers.wordwounter;

import ru.fizteh.fivt.file.WordCounter;

import java.io.*;
import java.util.*;

public class MyWordCounter implements WordCounter {

    public MyWordCounter() {

    }

    public void count(List<File> inputFiles, OutputStream output, boolean aggregate) throws IOException {
        if (output == null) {
            throw new IllegalArgumentException("Output is null");
        }
        if (inputFiles.isEmpty()) {
            throw new IllegalArgumentException("File list is empty");
        }
        String s;
        HashMap<String, Integer> map = new HashMap<>();
        for (File file : inputFiles) {
            if (!aggregate) {
                output.write(file.getName().getBytes());
                s = ":" + System.lineSeparator();
                output.write(s.getBytes());
            }
            if (file == null) {
                throw new IllegalArgumentException("File is null");
            }
            if (!file.exists()) {
                if (!aggregate) {
                    s = "file not found" + System.lineSeparator();
                    output.write(s.getBytes());
                }
                continue;
            }
            if (!file.canRead()) {
                if (!aggregate) {
                    s = "file not available" + System.lineSeparator();
                    output.write(s.getBytes());
                }
                continue;
            }
            BufferedReader input = new BufferedReader(new FileReader(file));
            if (input == null) {
                throw new IOException("Can't create stream");
            }
            StringBuilder tmpstr = new StringBuilder();
            boolean dash = false;
            char k;

            while (true) {
                String str = input.readLine();
                if (str == null) {
                    break;
                }
                for (int i = 0; i < str.length(); ++i) {
                    k = str.charAt(i);
                    if (Character.isLetterOrDigit(k)) {
                        if (dash) {
                            if (tmpstr.length() > 1) {
                                dash = false;
                                tmpstr.append(k);
                            } else {
                                tmpstr = new StringBuilder();
                                dash = false;
                            }
                        } else {
                            tmpstr.append(k);
                        }
                    } else {
                        if (k == '-') {
                            if (dash) {
                                if (tmpstr.length() > 1) {
                                    tmpstr.delete(tmpstr.length() - 1,tmpstr.length());
                                    if (map.containsKey(tmpstr.toString())) {
                                        int count = map.get(tmpstr.toString()) + 1;
                                        map.put(tmpstr.toString(), count);
                                    } else {
                                        map.put(tmpstr.toString(), 1);
                                    }
                                    dash = false;
                                }
                                tmpstr = new StringBuilder();
                            } else {
                                if (!tmpstr.toString().isEmpty()) {
                                    dash = true;
                                    tmpstr.append(k);
                                }
                            }
                        } else {
                            if (!tmpstr.toString().isEmpty() && !dash) {
                                if (map.containsKey(tmpstr.toString())) {
                                    int count = map.get(tmpstr.toString()) + 1;
                                    map.put(tmpstr.toString(), count);
                                } else {
                                    map.put(tmpstr.toString(), 1);
                                }
                            }
                            tmpstr = new StringBuilder();
                            dash = false;
                        }
                    }
                }
                if (tmpstr.length() > 0) {
                    if (!dash) {
                        if (map.containsKey(tmpstr.toString())) {
                            int count = map.get(tmpstr.toString()) + 1;
                            map.put(tmpstr.toString(), count);
                        } else {
                            map.put(tmpstr.toString(), 1);
                        }
                    } else {
                        if (tmpstr.length() > 1) {
                            tmpstr.delete(tmpstr.length() - 1,tmpstr.length());
                            if (map.containsKey(tmpstr.toString())) {
                                int count = map.get(tmpstr.toString()) + 1;
                                map.put(tmpstr.toString(), count);
                            } else {
                                map.put(tmpstr.toString(), 1);
                            }
                        }
                    }


                }
                tmpstr = new StringBuilder();
                dash = false;
            }

            if (!aggregate) {
                for (String str : map.keySet()) {
                    String tmp = str + " " + map.get(str) + System.lineSeparator();
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
                String tmp = str + " " + map.get(str) + System.lineSeparator();
                output.write(tmp.getBytes());
            }
        }
    }
}
