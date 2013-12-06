package ru.fizteh.fivt.students.sterzhanovVladislav.calculator;

import java.math.BigInteger;
import java.util.Queue;
import java.util.Stack;

public class Calculator {
    
    private static final String OPERATOR_MATCHER = "([\\+\\-\\*/\\(\\)])";
    private static final String NUMBER_MATCHER = "[0-9A-Ha-h]+";
    private static final int BASE = 18;
    
    public static void main(String[] args) {
        String usage = "usage: calculator <arithmetic expression>";     
        try {
            if (args.length == 0) {
                throw new Exception(usage);
            }
            
            StringBuilder expression = new StringBuilder();
            for (String arg : args) {
                expression.append(arg).append(" ");
            }
            Queue<String> reversePolishExpression = ExpressionNotationUtility
                    .convertToReversePolishNotation(expression.toString(), NUMBER_MATCHER, OPERATOR_MATCHER);
            String result = evaluatePostfixExpression(reversePolishExpression);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
    
    public static String evaluatePostfixExpression(Queue<String> reversePolishExpression) 
            throws IllegalArgumentException {
        Stack<BigInteger> numberStack = new Stack<BigInteger>();
        for (String token : reversePolishExpression) {
            if (token.matches(OPERATOR_MATCHER)) {
                if (numberStack.size() < 2) {
                    throw new IllegalArgumentException("Expression validation failed");
                } else {
                    BigInteger rightOperand = numberStack.pop();
                    BigInteger leftOperand = numberStack.pop();
                    BigInteger result = apply(token, leftOperand, rightOperand);
                    numberStack.push(result);
                }
            } else {
                BigInteger newNum = new BigInteger(token, BASE);
                numberStack.push(newNum);
            }
        }
        if (numberStack.size() != 1) {
            throw new IllegalArgumentException("Expression validation failed");
        }
        return numberStack.pop().toString(BASE);
    }
    
    private static BigInteger apply(String operator, BigInteger leftOperand, BigInteger rightOperand) 
            throws IllegalArgumentException {
        if (operator.equals("+")) {
            return leftOperand.add(rightOperand);
        } else if (operator.equals("-")) {
            return leftOperand.subtract(rightOperand);
        } else if (operator.equals("*")) {
            return leftOperand.multiply(rightOperand);
        } else if (operator.equals("/")) {
            if (rightOperand.equals(BigInteger.ZERO)) {
                throw new IllegalArgumentException("Division by zero");
            }
            return leftOperand.divide(rightOperand);
        } else {
            throw new IllegalArgumentException("Shouldn't ever happen");
        }
    }
}
