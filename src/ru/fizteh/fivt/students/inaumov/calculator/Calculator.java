package ru.fizteh.fivt.students.inaumov.calculator;

import java.io.IOException;

public class Calculator {
    public static void abortPrg(String errorMsg) {
        System.err.println(errorMsg);
        System.exit(1);
    }
    public static void main(String[] args) {
        /*StringBuilder bigString = new StringBuilder();
        for (String string: args) {
            bigString.append(string);
        }
        String inputData = bigString.toString();*/
        String inputData = "";
        for (String nextArg: args) {
            inputData = inputData + nextArg + ' ';
        }
        //System.out.println(inputData);
        Analysis analysis = new Analysis(inputData);
        int result = 0;
        try {
            result = analysis.calculateAnswer();
            System.out.println(Integer.toString(result, analysis.RADIX));
        } catch (IOException err) {
            abortPrg("ERROR: INCORRECT INPUT");
        } catch (NumberFormatException err) {
            abortPrg("ERROR: INCORRECT INPUT");
        }
    }
}
