package ru.fizteh.fivt.students.irinapodorozhnaya.utils;

import java.io.IOException;
import java.util.Scanner;

import ru.fizteh.fivt.students.irinapodorozhnaya.shell.StateInterface;

public class Mode {
     private Mode() {}
     public static void batchMode(String[] args, StateInterface st) throws IOException {
        parseAndExecute(joinString(args), st); 
     }

     public static String joinString(String[] args) {
         StringBuilder sb = new StringBuilder();
         for (String s: args) {
             sb.append(s);
             sb.append(" ");
         }
         return sb.toString();
     }

     private static void parseAndExecute(String arg, StateInterface st) throws IOException {
         String[] com = arg.trim().split("\\s*;\\s*");
         for (String s: com) {
             String[] args = s.split("\\s+");
             st.checkAndExecute(args);
         }
     }

     public static void interactiveMode(StateInterface st) {
         Scanner sc = new Scanner(st.getInputStream());
         do {
             try {
                 st.getOutputStream().print("$ ");
                 String s = sc.nextLine();
                 parseAndExecute(s, st);
             } catch (IOException e) {
                 System.err.println(e.getMessage());
             }     
         } while (!Thread.interrupted());
         sc.close();
     }
}
