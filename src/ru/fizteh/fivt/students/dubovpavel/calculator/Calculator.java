package ru.fizteh.fivt.students.dubovpavel.calculator;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

class Calculator {
    public static final int RADIX = 19;
    private static final char LASTLETTER = 'A' + RADIX - 10 - 1;
    private static final String BUG = "Program contains a bug.";

    static class InappropriateSymbolException extends Exception {
        InappropriateSymbolException(String message) {
            super(message);
        }
    }

    static class InvalidLexemMetException extends Exception {
        InvalidLexemMetException(String message) {
            super(message);
        }
    }

    private enum Lexem {
        NUMBER,
        ADDITION,
        SUBTRACTION,
        MULTIPLICATION,
        DIVISION,
        OBRACKET,
        CBRACKET
    }

    ;

    private enum State {
        START,
        NUMBER,
        ADDITION,
        SUBTRACTION,
        MULTIPLICATION,
        DIVISION,
        OBRACKET,
        CBRACKET,
        UNARYMinus
    }

    ;

    private String expression;
    private ArrayList<Token<Lexem, Integer>> lexems = new ArrayList<Token<Lexem, Integer>>();

    public Calculator(String expression) throws InappropriateSymbolException {
        this.expression = expression;
        int pointer = 0;
        lexems.add(new Token<Lexem, Integer>(Lexem.OBRACKET, -1));
        while (pointer < expression.length()) {
            char currentSymbol = expression.charAt(pointer);
            if (currentSymbol == '+') { //Can not use switch here because I need
                                        // a range of values to determine if it is a number.
                lexems.add(new Token<Lexem, Integer>(Lexem.ADDITION, pointer));
            } else if (currentSymbol == '-') {
                lexems.add(new Token<Lexem, Integer>(Lexem.SUBTRACTION, pointer));
            } else if (currentSymbol == '*') {
                lexems.add(new Token<Lexem, Integer>(Lexem.MULTIPLICATION, pointer));
            } else if (currentSymbol == '/') {
                lexems.add(new Token<Lexem, Integer>(Lexem.DIVISION, pointer));
            } else if (currentSymbol == '(') {
                lexems.add(new Token<Lexem, Integer>(Lexem.OBRACKET, pointer));
            } else if (currentSymbol == ')') {
                lexems.add(new Token<Lexem, Integer>(Lexem.CBRACKET, pointer));
            } else if ('0' <= currentSymbol && currentSymbol <= '9'
                    || 'A' <= currentSymbol && currentSymbol <= LASTLETTER) {
                do {
                    pointer++;
                    if (pointer < expression.length()) {
                        currentSymbol = expression.charAt(pointer);
                    } else {
                        break;
                    }
                }
                while ('0' <= currentSymbol && currentSymbol <= '9'
                        || 'A' <= currentSymbol && currentSymbol <= LASTLETTER);
                pointer--;
                lexems.add(new Token<Lexem, Integer>(Lexem.NUMBER, pointer));
            } else if (!Character.isWhitespace(currentSymbol)) {
                throw new InappropriateSymbolException(
                        String.format("Inappropriate symbol '%c' found.", currentSymbol));
            }
            pointer++;
        }
        lexems.add(new Token<Lexem, Integer>(Lexem.CBRACKET, expression.length()));
    }

    private Integer getNumber(int lexemNumber) throws NumberFormatException {
        Integer result;
        try {
            result = Integer.parseInt(this.expression.substring(lexems.get(lexemNumber - 1).getPointer() + 1,
                    lexems.get(lexemNumber).getPointer() + 1), RADIX);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(String.format("Lexem #%d can not be parsed as a number.", lexemNumber - 1));
        }
        return result;
    }

    private String getInvalidLexemMetExceptionText(int lexemNumber) {
        return String.format("Lexem #%d is invalid.", lexemNumber);
    }

    private void performOperation(Stack<Integer> numbers, Lexem lexem) throws ArithmeticException {
        Integer a = numbers.pop();
        Integer b = numbers.pop();  // Stack is not empty because we use automata,
                                    // which garantees us correct syntax.
        Integer result;
        switch (lexem) {
            case ADDITION:
                result = SafeInteger.add(b, a);
                break;
            case SUBTRACTION:
                result = SafeInteger.subtract(b, a);
                break;
            case MULTIPLICATION:
                result = SafeInteger.multiply(b, a);
                break;
            case DIVISION:
                if (a == 0) {
                    throw new ArithmeticException("Division by zero occured.");
                } else {
                    result = SafeInteger.divide(b, a);
                }
                break;
            default:
                throw new RuntimeException(BUG);
        }
        numbers.push(result);
    }

    private void popBracketBlock(Stack<Integer> numbers, Stack<Lexem> operations)
            throws InvalidLexemMetException, ArithmeticException {
        try {
            Lexem currentLexem;
            while ((currentLexem = operations.pop()) != Lexem.OBRACKET) {
                performOperation(numbers, currentLexem);
            }
        } catch (EmptyStackException e) {
            throw new InvalidLexemMetException("Bracket sequence is not balanced.");
        }
    }

    private int getOperationPriority(Lexem operation) {
        switch (operation) {
            case ADDITION:
            case SUBTRACTION:
                return 0;
            case MULTIPLICATION:
            case DIVISION:
                return 1;
            case OBRACKET:
                return -1;
            default:
                throw new RuntimeException(BUG);
        }
    }

    private void pushOperation(Stack<Integer> numbers, Stack<Lexem> operations, Lexem operation)
            throws ArithmeticException {
        while (!operations.empty() && getOperationPriority(operations.peek()) >= getOperationPriority(operation)) {
            performOperation(numbers, operations.pop());
        }
        operations.push(operation);
    }

    private State operationToState(Lexem operation) {
        switch (operation) {
            case ADDITION:
                return State.ADDITION;
            case SUBTRACTION:
                return State.SUBTRACTION;
            case MULTIPLICATION:
                return State.MULTIPLICATION;
            case DIVISION:
                return State.DIVISION;
            default:
                throw new RuntimeException(BUG);
        }
    }

    public Integer calculate() throws InvalidLexemMetException, NumberFormatException, ArithmeticException {
        State state = State.START;
        Stack<Lexem> operations = new Stack<Lexem>();
        Stack<Integer> numbers = new Stack<Integer>();
        for (int i = 0; i < lexems.size(); i++) {
            Lexem lexemType = lexems.get(i).getLexem();
            switch (state) { //If we identify Lexem with State this code might be shorter;
                             // I don't like to use dirty tricks on Java tho.
                case START:
                    switch (lexemType) {
                        case OBRACKET:
                            operations.push(Lexem.OBRACKET);
                            state = State.OBRACKET;
                            break;
                        default:
                            throw new InvalidLexemMetException(getInvalidLexemMetExceptionText(i));
                    }
                    break;
                case OBRACKET:
                    if (lexemType == Lexem.SUBTRACTION) {
                        numbers.push(-1);
                        pushOperation(numbers, operations, Lexem.MULTIPLICATION);
                        state = State.UNARYMinus;
                        break;
                    }
                case UNARYMinus:
                    switch (lexemType) {
                        case NUMBER:
                            numbers.push(getNumber(i));
                            state = State.NUMBER;
                            break;
                        case OBRACKET:
                            operations.push(Lexem.OBRACKET);
                            state = State.OBRACKET;
                            break;
                        default:
                            throw new InvalidLexemMetException(getInvalidLexemMetExceptionText(i));
                    }
                    break;
                case NUMBER:
                    switch (lexemType) {
                        case ADDITION:
                        case SUBTRACTION:
                        case MULTIPLICATION:
                        case DIVISION:
                            pushOperation(numbers, operations, lexemType);
                            state = operationToState(lexemType);
                            break;
                        case CBRACKET:
                            popBracketBlock(numbers, operations);
                            state = State.CBRACKET;
                            break;
                        default:
                            throw new InvalidLexemMetException(getInvalidLexemMetExceptionText(i));
                    }
                    break;
                case ADDITION:
                case SUBTRACTION:
                case MULTIPLICATION:
                case DIVISION:
                    switch (lexemType) {
                        case NUMBER:
                            numbers.push(getNumber(i));
                            state = State.NUMBER;
                            break;
                        case OBRACKET:
                            operations.push(Lexem.OBRACKET);
                            state = State.OBRACKET;
                            break;
                        default:
                            throw new InvalidLexemMetException(getInvalidLexemMetExceptionText(i));
                    }
                    break;
                case CBRACKET:
                    switch (lexemType) {
                        case ADDITION:
                        case SUBTRACTION:
                        case MULTIPLICATION:
                        case DIVISION:
                            pushOperation(numbers, operations, lexemType);
                            state = operationToState(lexemType);
                            break;
                        case CBRACKET:
                            popBracketBlock(numbers, operations);
                            state = State.CBRACKET;
                            break;
                        default:
                            throw new InvalidLexemMetException(getInvalidLexemMetExceptionText(i));
                    }
                    break;
                default:
                    throw new RuntimeException(BUG);
            }
        }
        if (!operations.empty()) {
            throw new InvalidLexemMetException("Expression is incorrect.");
        }
        if (numbers.size() != 1) { //It should never happen as far as I understand.
            throw new RuntimeException(BUG);
        }
        return numbers.pop();
    }
}
