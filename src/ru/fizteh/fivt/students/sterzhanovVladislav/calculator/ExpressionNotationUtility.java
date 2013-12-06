package ru.fizteh.fivt.students.sterzhanovVladislav.calculator;


import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionNotationUtility {
    
    public static Queue<String> convertToReversePolishNotation(String infixExpression, 
            String validNumMatcher, String validOperatorMatcher) throws IllegalArgumentException {
        /* Initialize buffers */
        Queue<String> converted = new LinkedList<String>();
        Stack<String> operatorStack = new Stack<String>();

        /* Handle unary minuses */
        infixExpression = infixExpression.replaceAll(" *\\( *\\- *", "(0-").replaceAll("^ *\\- *", "0-");
        if (infixExpression.charAt(0) == '-') {
            infixExpression = "0" + infixExpression;
        }
        
        /* Initialize regex */
        String capturingRegex = "(" + validNumMatcher + "|" + validOperatorMatcher + ")";
        Matcher nextToken = Pattern.compile(capturingRegex).matcher(infixExpression);
        
        /* Shunting-yard implementation */
        while (nextToken.find()) {
            String token = nextToken.group(1);
            if (token.equals("(")) {
                operatorStack.push("(");
            } else if (token.equals(")")) {
                while (!operatorStack.empty() && !operatorStack.peek().equals("(")) {
                    converted.add(operatorStack.pop());
                }
                if (!operatorStack.empty()) {
                    operatorStack.pop();
                } else {
                    throw new IllegalArgumentException("Unmatched parenthesis");
                }
            } else if (token.matches(validOperatorMatcher)) {
                while (!operatorStack.empty() && (precedence(token) <= precedence(operatorStack.peek()))) {
                    converted.add(operatorStack.pop());
                }
                operatorStack.push(token);
            } else {
                converted.add(token);
            }
        }
        infixExpression = infixExpression.replaceAll(capturingRegex, "").replaceAll(" ", "");
        if (!infixExpression.isEmpty()) {
            throw new IllegalArgumentException("Illegal symbols found");
        }
        
        while (!operatorStack.isEmpty()) {
            if (operatorStack.peek().equals("(")) {
                throw new IllegalArgumentException("Unmatched parenthesis");
            }
            converted.add(operatorStack.pop());
        }
        return converted;
    }
    
    private static int precedence(String operator) {
        if (operator.equals("-") || operator.equals("+")) {
            return 1;
        } else if (operator.equals("*") || operator.equals("/")) {
            return 2;
        } else {
            return 0;
        }
    }
}
