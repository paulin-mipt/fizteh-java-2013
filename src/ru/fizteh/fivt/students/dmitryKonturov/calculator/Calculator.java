package ru.fizteh.fivt.students.dmitryKonturov.calculator;

import java.math.BigInteger;

public class Calculator {

    private Calculator() {

    }

    public static void main(String[] args) {
        StringBuilder argsBuilder = new StringBuilder();

        for (String arg : args) {
            argsBuilder.append(arg);
            argsBuilder.append(' ');
        }

        String argsString = argsBuilder.toString().toLowerCase();

        try {
            MyCalculator calc = new MyCalculator();
            String result = calc.calculate(argsString);
            System.out.println(result);
        } catch (WrongExpression e) {
            System.err.println(e);
            System.exit(1);
        } catch (ArithmeticException e) {
            System.err.println(e);
            System.exit(2);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(3);
        }
    }
}


class MyCalculator {
    private final int numberBase = 17;
    private String expression;
    private int curPosition;
    private Lexeme curLexeme;
    private BigInteger curNumber;

    private boolean isDigit(char c) {
        return Character.isDigit(c) || ('a' <= c && c <= 'g');
    }

    private Lexeme getLexeme() throws WrongExpression {
        while (curPosition < expression.length() && Character.isWhitespace(expression.charAt(curPosition))) {
            ++curPosition;
        }

        if (curPosition >= expression.length()) {
            return Lexeme.END;
        }

        char c = expression.charAt(curPosition);

        switch (c) {
            case '+':
                ++curPosition;
                return Lexeme.PLUS;

            case '-':
                ++curPosition;
                return Lexeme.MINUS;

            case '/':
                ++curPosition;
                return Lexeme.DIV;

            case '*':
                ++curPosition;
                return Lexeme.MUL;

            case '(':
                ++curPosition;
                return Lexeme.OPEN_BR;

            case ')':
                ++curPosition;
                return Lexeme.CLOSE_BR;

            default:
                int rightIndex = curPosition;
                while (isDigit(expression.charAt(rightIndex))) {
                    if (expression.length() <= rightIndex) {
                        break;
                    }
                    ++rightIndex;
                }
                try {
                    curNumber = new BigInteger(expression.substring(curPosition, rightIndex), numberBase);
                    curPosition = rightIndex;
                    return Lexeme.NUMBER;
                } catch (Exception e) {
                    throw new WrongExpression(String.format("Нельзя выделить число или операцию в выражении,"
                            + "начиная с позиции %d.", curPosition));

                }
        }

    }

    private void nextLexeme() throws WrongExpression {
        curLexeme = getLexeme();
    }

    private BigInteger calculateExpression(boolean isInBrackets) throws WrongExpression {
        BigInteger result = calculateSummand();
        BigInteger tmp;
        while (true) {
            switch (curLexeme) {
                case END:
                    return result;

                case CLOSE_BR:
                    if (isInBrackets) {
                        return result;
                    } else {
                        throw new WrongExpression(String.format("Нарушен баланс скобок в позиции %d.", curPosition));
                    }

                case PLUS:
                    nextLexeme();
                    tmp = calculateSummand();
                    result = result.add(tmp);
                    break;

                case MINUS:
                    nextLexeme();
                    tmp = calculateSummand();
                    result = result.subtract(tmp);
                    break;

                default:
                    throw new WrongExpression(String.format("Нельзя выделить слагаемое в позиции %d.", curPosition));

            }
        }
    }

    private BigInteger calculateSummand() throws WrongExpression {
        BigInteger result = calculateMultiplier();
        BigInteger tmp;
        while (true) {
            switch (curLexeme) {
                case END:
                    return result;

                case MUL:
                    nextLexeme();
                    tmp = calculateMultiplier();
                    result = result.multiply(tmp);
                    break;

                case DIV:
                    nextLexeme();
                    tmp = calculateMultiplier();
                    try {
                        result = result.divide(tmp);
                    } catch (ArithmeticException e) {
                        throw new WrongExpression("Произошло деление на ноль.");
                    }
                    break;

                default:
                    return result;
            }
        }
    }

    private BigInteger calculateMultiplier() throws WrongExpression {
        switch (curLexeme) {
            case END:
                throw new WrongExpression("Найден конец выражения, однако ожидался множитель.");

            case NUMBER:
                nextLexeme();
                return curNumber;

            case MINUS:
                throw new WrongExpression("Унарный минус не поддерживаается.");

            case PLUS:
                throw new WrongExpression("Унарный плюс не поддерживается.");

            case OPEN_BR:
                nextLexeme();
                BigInteger result = calculateExpression(true);
                if (curLexeme == Lexeme.CLOSE_BR) {
                    nextLexeme();
                    return result;
                } else {
                    throw new WrongExpression(String.format("Нарушен баланс скобок в позиции %d.", curPosition));
                }

            case CLOSE_BR:
                throw new WrongExpression(String.format("Ожидалось непустое выражение в скобках или "
                                                       + "число в позиции %d.", curPosition));

            default:
                throw new WrongExpression(String.format("Ожидалось выражение в скобках или число в позиции %d, "
                                          + "найдено %s. Возможно записаны две операции подряд.",
                                          curPosition, curLexeme.toString()));
        }
    }

    public String calculate(String expr) throws WrongExpression {
        expression = expr;
        curPosition = 0;
        curLexeme = getLexeme();
        if (curLexeme == Lexeme.END) {
            throw new WrongExpression("Пустое выражение.");
        }
        BigInteger result = calculateExpression(false);

        if (curLexeme != Lexeme.END) {
            throw new WrongExpression(String.format("Несколько последних символов были проигнорированы.\n"
                                                    + "Результат без них равен: %s.", result));
        }

        return result.toString(numberBase);
    }

    enum Lexeme {
        PLUS,
        MINUS,
        DIV,
        MUL,
        OPEN_BR,
        CLOSE_BR,
        NUMBER,
        END
    }

}

class WrongExpression extends Exception {
    private String errorDescription;

    WrongExpression(String err) {
        errorDescription = err;
    }

    @Override
    public String toString() {
        return ("Неправильное выражение: " + errorDescription);
    }
}

