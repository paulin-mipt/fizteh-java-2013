package ru.fizteh.fivt.students.elenarykunova.calculator;

import java.math.BigInteger;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;

public class Calculator {

    static Stack<Long> operands = new Stack<>();
    static Stack<String> operators = new Stack<>();

    private static boolean isCorrectExpression() {
        if (operands.size() < 2 || operators.size() < 1) {
            System.err.println("Wrong expression.");
            return false;
        }
        return true;
    }

    private static boolean makeCalculation() {
        if (!isCorrectExpression()) {
            return false;
        }
        long secondOperand = operands.pop();
        long firstOperand = operands.pop();
        String operator = operators.pop();

        long result = 0;
        BigInteger firstBig = BigInteger.valueOf(firstOperand);
        BigInteger secondBig = BigInteger.valueOf(secondOperand);
        BigInteger realRes = null; 
        BigInteger myRes = null;

        if (operator.equals("+")) {
            result = firstOperand + secondOperand;
            realRes = firstBig.add(secondBig);
        } else if (operator.equals("-")) {
            result = firstOperand - secondOperand;
            realRes = firstBig.subtract(secondBig);
        } else if (operator.equals("*")) {
            result = firstOperand * secondOperand;
            realRes = firstBig.multiply(secondBig);
        } else if (operator.equals("/")) {
            if (secondOperand == 0) {
                System.err.println("Dividing by zero");
                return false;
            }
            result = firstOperand / secondOperand;
            realRes = BigInteger.valueOf(result);
        }

        myRes = BigInteger.valueOf(result);
        if (!realRes.equals(myRes)) {
            System.out.println("Too large values.");
            return false;
        }
        operands.push(result);
        return true;
    }

    private static boolean isCorrectBracketSeq(String str) {
        long count = 0;

        for (int i = 0; i < str.length(); i++) {
            if (count < 0) {
                return false;
            }
            if (str.charAt(i) == '(') {
                count++;
            } else if (str.charAt(i) == ')') {
                count--;
            }
        }

        if (count != 0) {
            return false;
        }
        return true;
    }

    private static boolean isCorrectSpaceSeq(String str) {
        if (Pattern.matches("[0-9a-gA-G\\s/(/)/+-/*//]*"
                + "([0-9a-zA-Z]([\\s]+)[0-9a-zA-Z])+"
                + "[0-9a-gA-G\\s/(/)/+-/*//]*", str)) {
            return false;
        }
        return true;
    }

    private static int getPriority(String str) {
        if (str.equals("(") || str.equals(")")) {
            return 1;
        }
        if (str.equals("+") || str.equals("-")) {
            return 2;
        }
        if (str.equals("*") || str.equals("/")) {
            return 3;
        }
        return -1;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("No expression is found.");
            System.exit(1);
        }

        int base = 17;

        StringBuffer s = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            s.append(args[i]);
            s.append(" ");
        }
        String str = s.toString();

        if (!Pattern.matches("[0-9a-gA-G\\s/(/)/+-/*//]*", str)) {
            System.err
                    .println("Unexpected symbol. Use 0-9, A-G, a-g, +, -, *, /, (, ).");
            System.exit(1);
        }
        if (!isCorrectBracketSeq(str)) {
            System.err.println("Wrong expression: brackets mismatch.");
            System.exit(1);
        }
        if (!isCorrectSpaceSeq(str)) {
            System.err.println("Wrong expression: too many spaces.");
            System.exit(1);
        }

        str = str.replace(" ", "");

        Scanner myScan = new Scanner(str);
        myScan.useRadix(base);

        myScan.useDelimiter("((?<=[0-9a-gA-G])(?=[/(/)/+-/*//]))"
                + "|((?<=[/(/)/+-/*//])(?=[0-9a-gA-G]))"
                + "|((?<=[/(/)/+-/*//])(?=[/(/)/+-/*//]))");

        int currPriority = 0;
        String currOperator;
        while (myScan.hasNext()) {
            if (myScan.hasNextLong()) {
                long newInt = myScan.nextLong();
                operands.push(newInt);
            } else {
                currOperator = myScan.next();
                if (currOperator.equals("(")) {
                    operators.push(currOperator);
                } else if (currOperator.equals(")")) {
                    while (!operators.isEmpty()
                            && !operators.peek().equals("(")) {
                        if (!makeCalculation()) {
                            myScan.close();
                            System.exit(1);
                        }
                    }
                    if (!operators.isEmpty()) {
                        operators.pop();
                    } else {
                        System.err.println("Wrong expression: '(' not found");
                        myScan.close();
                        System.exit(1);
                    }
                } else if (currOperator.equals("+") || currOperator.equals("-")
                        || currOperator.equals("*") || currOperator.equals("/")) {
                    currPriority = getPriority(currOperator);
                    while (!operators.isEmpty()
                            && getPriority(operators.peek()) >= currPriority) {
                        if (!makeCalculation()) {
                            myScan.close();
                            System.exit(1);
                        }
                    }
                    operators.push(currOperator);
                } else {
                    System.err.println("Too large number.");
                    myScan.close();
                    System.exit(1);
                }
            }
        }

        while (!operators.isEmpty()) {
            if (!makeCalculation()) {
                myScan.close();
                System.exit(1);
            }
        }

        if (operands.size() != 1) {
            System.err.println("Wrong expression.");
            myScan.close();
            System.exit(1);
        } else {
            long result = operands.pop();
            System.out.println(Long.toString(result, base));
        }
        myScan.close();
    }
}
