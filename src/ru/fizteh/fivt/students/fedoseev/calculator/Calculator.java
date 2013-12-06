package ru.fizteh.fivt.students.fedoseev.calculator;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {
    public static final int RADIX = 18;
    public static final Map<String, Integer> MATH_OPERATIONS = new HashMap<String, Integer>() {
        {
            put("*", 1);
            put("/", 1);
            put("+", 2);
            put("-", 2);
        }
    };

    public static String shuntingYard(String expr, Map<String, Integer> ops) throws IOException {
        if (expr == null || expr.length() == 0 || ops == null || ops.isEmpty()) {
            throw new IllegalStateException();
        }

        final String lPar = "(";
        final String rPar = ")";
        List<String> output = new ArrayList<String>();
        Stack<String> opStack = new Stack<String>();

        final Pattern p1 = Pattern.compile("[A-H0-9\\+\\-\\*/ \\(\\)]*");
        final Pattern p2 = Pattern.compile("(.*)([A-H0-9]+( )+[A-H0-9]+)(.*)");
        Matcher m1 = p1.matcher(expr);
        boolean b1 = m1.matches();
        Matcher m2 = p2.matcher(expr);
        boolean b2 = m2.matches();

        if (!b1 || b2) {
            throw new IOException();
        }
        if (expr.indexOf("(") < expr.indexOf(")") || !expr.contains("(")) {
            expr = "(" + expr + ")";
        }
        expr = expr.replace("(-", "(0-").replace("---", "-").replace("--", "+0+").replace("+-", "+0-");

        String[] symbols = new String[]{"*", "/", "+", "-", lPar, rPar};
        /*
        Set<String> symbols = new HashSet<String>(ops.keySet());
        symbols.add(L_PAR);
        symbols.add(R_PAR);
        */

        int opIndex = 0;
        boolean read = true;

        while (read) {
            int nextOpIndex = expr.length();
            String nextOp = "";

            for (String op : symbols) {
                int i = expr.indexOf(op, opIndex);
                if (i >= 0 && i < nextOpIndex) {
                    nextOp = op;
                    nextOpIndex = i;
                }
            }

            if (nextOpIndex == expr.length()) {
                read = false;
            } else {
                if (opIndex != nextOpIndex) {
                    output.add(expr.substring(opIndex, nextOpIndex));
                }

                if (nextOp.equals(lPar)) {
                    opStack.push(nextOp);
                } else if (nextOp.equals(rPar)) {
                    while (!opStack.empty() && !opStack.peek().equals(lPar)) {
                        output.add(opStack.pop());

                        if (opStack.empty()) {
                            throw new IllegalArgumentException();
                        }
                    }

                    if (opStack.empty()) {
                        throw new IllegalArgumentException();
                    }

                    opStack.pop();
                } else {
                    while (!opStack.empty() && !opStack.peek().equals(lPar)
                            && ops.get(nextOp) >= ops.get(opStack.peek())) {
                        output.add(opStack.pop());
                    }

                    opStack.push(nextOp);
                }

                opIndex = nextOpIndex + nextOp.length();
            }
        }

        if (opIndex != expr.length()) {
            output.add(expr.substring(opIndex));
        }
        while (!opStack.empty()) {
            output.add(opStack.pop());
        }

        StringBuilder outputString = new StringBuilder();
        for (int i = 0; i < output.size(); ++i) {
            if (i != 0) {
                outputString.append(" ");
            }
            outputString.append(output.get(i));
        }

        return outputString.toString();
    }

    public static String calculateValueOfExpression(String expr) throws IOException {
        StringTokenizer tr = new StringTokenizer(shuntingYard(expr, MATH_OPERATIONS), " ");
        Stack<Integer> rpnStack = new Stack<Integer>();

        while (tr.hasMoreTokens()) {
            String t = tr.nextToken();

            if (!MATH_OPERATIONS.containsKey(t)) {
                try {
                    Integer a = Integer.valueOf(t, RADIX);
                    rpnStack.push(a);
                } catch (NumberFormatException e) {
                    System.err.println("ERROR: Out of range");
                    System.exit(1);
                }
            } else {
                if (rpnStack.empty()) {
                    throw new IOException();
                }

                Integer o2 = rpnStack.pop();

                if (rpnStack.empty()) {
                    throw new IOException();
                }

                Integer o1 = rpnStack.pop();

                if (t.equals("*")) {
                    if (o1 != 0 && o2 != 0) {
                        if (Integer.MAX_VALUE / Math.abs(o1) < Math.abs(o2)) {
                            throw new NumberFormatException();
                        }
                    }

                    rpnStack.push(o1 * o2);
                } else if (t.equals("/")) {
                    if (o2 == 0) {
                        throw new ArithmeticException();
                    }

                    rpnStack.push(o1 / o2);
                } else if (t.equals("+")) {
                    if (Integer.MAX_VALUE - Math.abs(o1) < Math.abs(o2)) {
                        throw new NumberFormatException();
                    }

                    rpnStack.push(o1 + o2);
                } else if (t.equals("-")) {
                    if (Integer.signum(o1) != Integer.signum(o2)) {
                        if (Integer.MAX_VALUE - Math.abs(o1) < Math.abs(o2)) {
                            throw new NumberFormatException();
                        }
                    }

                    rpnStack.push(o1 - o2);
                }
            }
        }

        if (rpnStack.size() != 1) {
            throw new IOException();
        }

        return (Integer.toString(rpnStack.pop(), RADIX)).toUpperCase();
    }

    public static void main(String[] args) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            sb.append(s).append(" ");
        }
        String expression = sb.toString();
        try {
            System.out.println(calculateValueOfExpression(expression));
        } catch (IllegalStateException e) {
            System.err.println("ERROR: Expression or operations are not specified");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("ERROR: Incorrect expression");
            System.exit(1);
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Out of range");
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("ERROR: Unmatched brackets");
            System.exit(1);
        } catch (ArithmeticException e) {
            System.err.println("ERROR: Divide by zero");
            System.exit(1);
        }

        System.exit(0);
    }
}
