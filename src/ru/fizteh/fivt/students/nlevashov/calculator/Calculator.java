package ru.fizteh.fivt.students.nlevashov.calculator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

public class Calculator {
    //Checking input string for absence of excess symbols
    public static boolean checkForExcessSymbols(String s) {
        Pattern alphabet = Pattern.compile("^[A-Ha-h\\d\\s\\+\\-\\*/\\(\\)]+");
        return alphabet.matcher(s).matches();
    }

    //Parsing input string to array of tokens.
    //After parsing, negative numbers "-x" is replaced to "0-x", spaces are removed.
    public static ArrayList<String> parse(String s) {
        StringTokenizer tokenParser = new StringTokenizer(s, " \\+\\-\\*\\/\\(\\)", true);
        ArrayList<String> tokens = new ArrayList<String>();
        while (tokenParser.hasMoreTokens()) {
            tokens.add(tokenParser.nextToken());
        }

        int i = 0;
        while (i < tokens.size()) {
            if ((tokens.get(i).equals("-")) && ((i == 0) || (tokens.get(i - 1).equals("(")))) {
                tokens.add(i, "0");
                i += 2;
            } else if (" ".equals(tokens.get(i))) {
                tokens.remove(i);
            } else {
                i++;
            }
        }

        return tokens;
    }

    //Cheking array of tokens for correct sequence:
    //  - operator after number, number after operator, first & last tokens are number;
    //  - operator before "(", number after ")";
    //  - bracket balance >= 0 in every point, in the last point balance = 0
    public static boolean checkForTokensSequence(ArrayList<String> tokens) {
        boolean prev = false;
        int i = 0;
        Pattern sign = Pattern.compile("[\\+\\-\\*\\/]");

        for (String s : tokens) {
            if (s.equals("(")) {
                i++;
                if (prev) {
                    prev = false;
                    break;
                }
            } else if (s.equals(")")) {
                i--;
                if ((!prev) || (i < 0)) {
                    break;
                }
            } else if (sign.matcher(s).matches()) {
                if (prev) {
                    prev = false;
                } else {
                    break;
                }
            } else {
                if (prev) {
                    prev = false;
                    break;
                } else {
                    prev = true;
                }
            }
        }

        return (prev && (i == 0));
    }

    //Auxiliary function for "reversePolishNotation"
    public static int priorityOf(String s) {
        int priority = 0;
        switch (s) {
            case "(":
                priority = 1;
                break;
            case "+":
            case "-":
                priority = 2;
                break;
            case "*":
            case "/":
                priority = 3;
                break;
            case " ":
                priority = 0;
                break;
            default:
        }

        return priority;
    }

    //Making Reverse Polish Notation (from array of tokens in normal sequence)
    //  with Shunting Yard Algorithm of Dijkstra
    public static Vector<String> reversePolishNotation(ArrayList<String> input) {
        Vector<String> output = new Vector<String>();
        Stack<String> s = new Stack<String>();
        s.add(" ");
        Pattern sign = Pattern.compile("[\\+\\-\\*\\/]");

        for (String j : input) {
            if (j.equals("(")) {
                s.push(j);
            } else if (j.equals(")")) {
                while (!s.peek().equals("(")) {
                    output.add(s.pop());
                }
                s.pop();
            } else if (sign.matcher(j).matches()) {
                while (priorityOf(s.peek()) >= priorityOf(j)) {
                    output.add(s.pop());
                }
                s.push(j);
            } else {
                output.add(j);
            }
        }

        while (!s.peek().equals(" ")) {
            output.add(s.pop());
        }

        return output;
    }

    //Calculating of Reverse Polish Notation
    public static String calculate(Vector<String> rpn) throws Exception {
        Stack<BigInteger> s = new Stack<BigInteger>();

        for (String t : rpn) {
            BigInteger a;
            BigInteger b;
            switch (t) {
                case "+":
                    b = s.pop();
                    a = s.pop();
                    s.push(a.add(b));
                    break;
                case "-":
                    b = s.pop();
                    a = s.pop();
                    s.push(a.subtract(b));
                    break;
                case "*":
                    b = s.pop();
                    a = s.pop();
                    s.push(a.multiply(b));
                    break;
                case "/":
                    b = s.pop();
                    if (b.equals(0)) {
                        throw new Exception("Division by zero");
                    }
                    a = s.pop();
                    s.push(a.divide(b));
                    break;
                default:
                    s.push(new BigInteger(t, 18));
            }
        }

        return s.pop().toString(18);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Empty input");
            System.exit(1);
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : args) {
                sb.append(s).append(' ');
            }
            String expression = sb.toString();

            if (!checkForExcessSymbols(expression)) {
                System.err.println("There are incorrect symbols");
                System.exit(1);
            } else {
                ArrayList<String> tokens = parse(expression);
                if (!checkForTokensSequence(tokens)) {
                    System.err.println("Wrong sequence of symbols");
                    System.exit(1);
                } else {
                    Vector<String> rpn = reversePolishNotation(tokens);
                    try {
                        System.out.println(calculate(rpn));
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        System.exit(1);
                    }
                }
            }
        }
    }
}

