package ru.fizteh.fivt.students.podoltseva.calculator;

import java.util.ArrayList;
import java.util.Stack;
import java.io.IOException;

public class Calculator {
	private static int radix = 10;
	
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Error! The string is empty.");
			System.exit(1);
		}
		try {
			StringBuilder buildInputString = new StringBuilder();
			for (String i : args) {
				buildInputString.append(i);
				buildInputString.append(' ');
			}
			String inputString = buildInputString.toString();	
			if (inputString.equals(" ")) {
				System.err.println("Error! The string is empty.");
				System.exit(1);
			}
			ArrayList<String> inPolishNotation = reverseToPolishNotation(inputString);
			Integer result = calculate(inPolishNotation);
			System.out.println(Integer.toString(result, radix));
		}
		catch(IOException exception) {
			System.err.println(exception.getMessage());
			System.exit(1);
		}
		catch(NumberFormatException exception) {
			System.err.println(exception.getMessage());
			System.exit(2);
		}
		catch(ArithmeticException exception) {
			System.err.println(exception.getMessage());
			System.exit(3);			
		}		
	}
	
	public static ArrayList<String> reverseToPolishNotation(String inputString) throws IOException {
		boolean ifMinusUnar = true;
		int bracketBalance = 0;
		Stack<String> operatorStack = new Stack<String>();
		ArrayList<String> returnValue = new ArrayList<String>();
		boolean ifLastNumber = false;
		boolean ifWasSpace = false;
		boolean ifWasCloseBracket = false;
		char currentSymbol = ' ';
		StringBuilder currentNumber = new StringBuilder();
		for (int i = 0; i < inputString.length(); ++i) {
			currentSymbol = inputString.charAt(i);
			switch (currentSymbol) {
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				case '0':
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
				case 'G':
				case 'H': 
					if (ifLastNumber && ifWasSpace) {
						throw new IOException("Error. Two numbers in a row or extra space.");
					}
					if (ifWasCloseBracket) {
						throw new IOException("Error. An operator is missed after closing bracket.");
					}
					currentNumber.append(currentSymbol);
					ifLastNumber = true;
					ifMinusUnar = false;
					break;
				case '(':
					if (ifLastNumber) {
						throw new IOException("Error. An operator is missed before some opening bracket.");
					}
					if (ifWasCloseBracket) {
						throw new IOException("Error. An operator is missed after closing bracket.");
					}
					bracketBalance++;
					ifMinusUnar = true;
					ifLastNumber = false;
					operatorStack.add("(");					
					break;
				case ')':
					bracketBalance--;
					if (bracketBalance < 0) {
						throw new IOException("Error. The bracket balance is wrong.");
					}
					if (!ifLastNumber && !operatorStack.peek().equals("(")) {
						throw new IOException("Error. A number is missed before some closing bracket.");
					}
					ifWasCloseBracket = true;
					if (currentNumber.length() != 0) {
						returnValue.add((new Integer(Integer.parseInt(currentNumber.toString(), radix))).toString());
						currentNumber.delete(0, currentNumber.length());
					}
					String operator = operatorStack.pop();
					if (operator.equals("(") && !ifLastNumber) {
						throw new IOException("Error. Brackets are empty.");
					}
					while (!(operator.equals("("))) {
						returnValue.add(operator);
						operator = operatorStack.pop();
					}
					ifMinusUnar = false;
					ifLastNumber = false;
					break;
				case '+':
					if (!ifLastNumber && !ifWasCloseBracket){
						throw new IOException("Error. A number is missed before operator +.");
					}
					if(ifLastNumber) {
						returnValue.add((new Integer(Integer.parseInt(currentNumber.toString(), radix))).toString());
						currentNumber.delete(0, currentNumber.length());
					}
					while (!operatorStack.empty()  &&
							!operatorStack.peek().equals("(")) {
						returnValue.add(operatorStack.pop());
					}
					operatorStack.add("+");
					ifLastNumber = false;
					break;
				case '-':
					if (!ifLastNumber && !ifMinusUnar && !ifWasCloseBracket) {
						throw new IOException("Error. A number is missed before operator -.");
					}
					if (ifMinusUnar) {
						currentNumber.append('-');
					} else {
						if (ifLastNumber) {
							returnValue.add((new Integer(Integer.parseInt(currentNumber.toString(), radix))).toString());
							currentNumber.delete(0, currentNumber.length());
						}
						while (!operatorStack.empty() &&
								!operatorStack.peek().equals("(")) {
							returnValue.add(operatorStack.pop());
						}
						operatorStack.add("-");
					}
					ifLastNumber = false;
					ifMinusUnar = false;
					break;
				case '*':
					if (!ifLastNumber && !ifWasCloseBracket) {
						throw new IOException("Error. A number is missed before operator *.");
					}
					if (ifLastNumber) {
						returnValue.add((new Integer(Integer.parseInt(currentNumber.toString(), radix))).toString());
						currentNumber.delete(0, currentNumber.length());
					}
					while (!operatorStack.empty() && !operatorStack.peek().equals("(") &&
							(operatorStack.peek().equals("*") || operatorStack.peek().equals("/"))) {
						returnValue.add(operatorStack.pop());
					}
					operatorStack.add("*");
					ifLastNumber = false;
					break;
				case '/':
					if (!ifLastNumber && !ifWasCloseBracket) {
						throw new IOException("Error. A number is missed before operator /.");
					}
					if (ifLastNumber) {
						returnValue.add((new Integer(Integer.parseInt(currentNumber.toString(), radix))).toString());
						currentNumber.delete(0, currentNumber.length());
					}
					while (!operatorStack.empty() && !operatorStack.peek().equals("(") &&
							(operatorStack.peek().equals("*") || operatorStack.peek().equals("/"))) {
						returnValue.add(operatorStack.pop());
					}
					operatorStack.add("/");
					ifLastNumber = false;
					break;
				case ' ':
					ifWasSpace = true;
					break;
				default:
					throw new IOException("Error. Invalid symbols.");							
			}	
			if (currentSymbol != ' ') {
				ifWasSpace = false;
			}
			if (currentSymbol != ')' && currentSymbol != ' ') {
				ifWasCloseBracket = false;
			}
		}		
		if (currentSymbol == '+' || currentSymbol == '-' || currentSymbol == '*' || currentSymbol == '/') {
			throw new IOException("Error. Unexpected end of input.");
		}
		if (bracketBalance > 0) {
			throw new IOException("Error. Bracket balance is wrong.");
		}
		if (currentNumber.length() > 0) {
			returnValue.add((new Integer(Integer.parseInt(currentNumber.toString(), radix))).toString());
		}
		while (!operatorStack.empty()) {	
			returnValue.add(operatorStack.pop());
		}
		return returnValue;
	}
	
	public static Integer calculate(ArrayList<String> PolishNotation) throws IOException, NumberFormatException, ArithmeticException {
		Stack<Integer> numberStack = new Stack<Integer>();
		String currentSymbol;
		Integer leftOperand;
		Integer rightOperand;
		for (int i = 0; i < PolishNotation.size(); ++i) {
			currentSymbol = PolishNotation.get(i);
			if (currentSymbol.equals("+")) {
				rightOperand = numberStack.pop();
				leftOperand = numberStack.pop();
				if (Integer.signum(rightOperand) == Integer.signum(leftOperand) &&
					Math.abs(leftOperand) > Math.abs(Integer.MAX_VALUE - rightOperand)) {
					throw new IOException("Error. Integer overflow.");
				}
				numberStack.add(leftOperand + rightOperand);
			} else if (currentSymbol.equals("-")) {
				rightOperand = numberStack.pop();
				leftOperand = numberStack.pop();
				if (Integer.signum(rightOperand) != Integer.signum(leftOperand) &&
					Math.abs(leftOperand) > Math.abs(Integer.MAX_VALUE - rightOperand)) {
					throw new NumberFormatException("Error. Integer overflow.");
				}
				numberStack.add(leftOperand - rightOperand);
			} else if (currentSymbol.equals("*")) {
				rightOperand = numberStack.pop();
				leftOperand = numberStack.pop();
				if (rightOperand != 0 &&
						Math.abs(leftOperand) > Math.abs(Integer.MAX_VALUE / rightOperand)) {
					throw new NumberFormatException("Error. Integer overflow.");
				}
				numberStack.add(leftOperand * rightOperand);
			} else if (currentSymbol.equals("/")) {
				rightOperand = numberStack.pop();
				leftOperand = numberStack.pop();
				if (rightOperand == 0) {
					throw new ArithmeticException("Division by zero.");
				}
				numberStack.add(leftOperand / rightOperand);
			} else {
				numberStack.add(Integer.parseInt(currentSymbol));
			}
		}
		return numberStack.pop();
	}


}