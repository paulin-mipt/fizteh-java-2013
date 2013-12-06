package ru.fizteh.fivt.students.msandrikova.calculator;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.Vector;

public class Calculator {
    
    private static final int RAD = 17;
    
    private static boolean isMathSymbol(char s) {
        return (s == '+' || s == '-' || s == '*' || s == '/');
    }
    
    private static boolean isBracket(char s) {
        return (s == '(' || s == ')');
    }
    
    public static void main(String[] args) { 
        Queue<String> polishNotation = new LinkedList<String>();
        Stack<String> tempForPolishNotation = new Stack<String>();
        String token = new String();
        String expression = new String();
        
        if (args.length == 0) {
            System.err.println("Error: Your should enter at least one argument");
            System.exit(1);
        }
        
        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            sb.append(s);
        }
        expression = sb.toString();
        
        expression = expression.replace(" ", "");
        
        Vector<String> tokens = new Vector<String>();
        
        
        for (int i = 0; i < expression.length(); i++) {
            if (Calculator.isBracket(expression.charAt(i)) 
                    || (Calculator.isMathSymbol(expression.charAt(i)) 
                            && !(expression.charAt(i) == '-' 
                            && (i == 0 || expression.charAt(i - 1) == '(')))) {
                if (token.length() != 0) {
                    tokens.add(token);
                    token = "";
                }
                token += expression.charAt(i);
                tokens.add(token);
                token = "";
                continue;
            } else {
                token += expression.charAt(i);
            }
        }
        if (token.length() != 0) {
            tokens.add(token);
            token = "";
        }
        
        for (String s : tokens) {
            token = s;
            if (!token.matches("[*+-/)(]")) {
                polishNotation.add(token);
                continue;
            } else if (token.equals("(")) {
                tempForPolishNotation.add(token);
                continue;
            } else if (token.equals(")")) {
                while (!tempForPolishNotation.empty() && !tempForPolishNotation.peek().equals("(")) {
                    polishNotation.add(tempForPolishNotation.pop());
                }
                if (tempForPolishNotation.empty()) {
                    System.err.println("Error: Brackets isn't balanced");
                    System.exit(1);
                } else {
                    tempForPolishNotation.pop();
                }
                continue;
            } else {
                if (token.matches("[+-]")) {
                    while (!tempForPolishNotation.empty() && tempForPolishNotation.peek().matches("[*+-/]")) {
                        polishNotation.add(tempForPolishNotation.pop());
                    }
                    tempForPolishNotation.push(token);
                } else if (token.matches("[*/]")) {
                    while (!tempForPolishNotation.empty() && tempForPolishNotation.peek().matches("[*/]")) {
                        polishNotation.add(tempForPolishNotation.pop());
                    }
                    tempForPolishNotation.push(token);
                } 
            }
        }
                
        while (!tempForPolishNotation.empty() && tempForPolishNotation.peek().matches("[*+-/]")) {
            polishNotation.add(tempForPolishNotation.pop());
        }
        if (!tempForPolishNotation.empty()) {
            System.out.println("Error: Brackets isn't balanced");
            return;
        }
        
        Stack<Integer> calcOfPolishNotation = new Stack<Integer>();
        while (polishNotation.peek() != null) {
            token = polishNotation.remove();
            if (!token.matches("[*+-/)(]")) {
                try {
                    calcOfPolishNotation.push(Integer.parseInt(token, RAD));
                } catch (NumberFormatException e) {
                    System.err.println("Error: Illegal symbol in \"" + token + "\"");
                    System.exit(1);
                }
            } else {
                Integer a = 0;
                Integer b = 0; 
                Integer res = 0;
                a = calcOfPolishNotation.pop();
                b = calcOfPolishNotation.pop();
                switch(token) {
                case "+":
                    if ((a > 0 && Integer.MAX_VALUE - a < b) || (a < 0 && Integer.MIN_VALUE - a > b)) {
                        System.err.println("Error: Arithmetic overflow");
                        System.exit(1);
                    }
                    res = b + a;
                    break;
                case "-":
                    if ((a < 0 && Integer.MAX_VALUE + a < b) || (a > 0 && Integer.MIN_VALUE + a > b)) {
                        System.err.println("Error: Arithmetic overflow");
                        System.exit(1);
                    }
                    res = b - a;
                    break;
                case "/":
                    if (a.equals(0)) {
                        System.err.println("Error: Incorret expression: division by zero");
                        System.exit(1);
                    }
                    res = b / a;
                    break;
                    case "*":
                    if ((a > 0 && b > 0 && (double) Integer.MAX_VALUE / (double) a < (double) b)
                            || (a > 0 && b < 0 && (double) Integer.MIN_VALUE / (double) a > (double) b) 
                            || (a < 0 && b > 0 && (double) Integer.MIN_VALUE / (double) a < (double) b)
                            || (a < 0 && b < 0 && (double) Integer.MAX_VALUE / (double) a > (double) b)) {
                        System.err.println("Error: Arithmetic overflow");
                        System.exit(1);
                    }
                    res = b * a;
                    break;
                default:
                    break;
                }
                calcOfPolishNotation.push(res);
            }
        }
        System.out.println(Integer.toString(calcOfPolishNotation.pop(), RAD));
    }

}
