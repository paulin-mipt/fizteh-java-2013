package ru.fizteh.fivt.students.dubovpavel.calculator;

public class Main {
    public static void main(String[] args) {
        try {
            StringBuilder concatenator = new StringBuilder();
            for (String arg : args) {
                concatenator.append(arg);
            }
            Calculator objCalculator = new Calculator(concatenator.toString().toUpperCase());
            System.out.println(Integer.toString(objCalculator.calculate(), Calculator.RADIX).toUpperCase());
        } catch (Calculator.InappropriateSymbolException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        } catch (Calculator.InvalidLexemMetException e) {
            System.err.println(e.getMessage());
            System.exit(-2);
        } catch (ArithmeticException e) {
            System.err.println(e.getMessage());
            System.exit(-3);
        } catch (NumberFormatException e) {
            System.err.println(e.getMessage());
            System.exit(-4);
        }
    }
}
