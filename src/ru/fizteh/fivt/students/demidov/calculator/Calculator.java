package ru.fizteh.fivt.students.demidov.calculator;

import java.io.IOException;
import java.util.Deque;
import java.util.ArrayDeque;

public class Calculator {
	private static Deque<Integer> numbers = new ArrayDeque<Integer>();	
	private static Deque<String> operations = new ArrayDeque<String>();
	final static int Radix = 19;
	
	public static int getOperand() throws IOException {
		if (numbers.isEmpty()) {
			throw new IOException("wrong expression");
		}
		return numbers.pop();
	}
	
	public static void countOperation() throws IOException {
		String operation; 		
		if (!(operations.isEmpty())) {
			operation = operations.pop();
		} else {
			throw new IOException("wrong expression");
		}
		
		if (operation.equals("(")) {
			throw new IOException("mismatched quotes");
		}
		
		int secondOperand = getOperand();
		int firstOperand = getOperand();
		
		if (operation.equals("+")) {
			if (((secondOperand <= 0) && (firstOperand >= 0)) || ((secondOperand >= 0) && (firstOperand <= 0)) 
					|| (Integer.MAX_VALUE - Math.abs(secondOperand) >= Math.abs(firstOperand))) {
				numbers.push(firstOperand + secondOperand);
			} else {
				throw new IOException("Integer overflow");
			}
		} else if (operation.equals("-")) {
			if (((secondOperand >= 0) && (firstOperand >= 0)) || ((secondOperand <= 0) && (firstOperand <= 0)) 
					|| (Integer.MAX_VALUE - Math.abs(secondOperand) >= Math.abs(firstOperand))) {
				numbers.push(firstOperand - secondOperand);
			} else {
				throw new IOException("Integer overflow");
			}
		} else if (operation.equals("*")) {
			if ((secondOperand == 0) || (Integer.MAX_VALUE / Math.abs(secondOperand) >= Math.abs(firstOperand))) {
				numbers.push(firstOperand * secondOperand);
			} else {
				throw new IOException("Integer overflow");
			}
		} else if (operation.equals("/")) {
			if (secondOperand != 0) {
				numbers.push(firstOperand / secondOperand);
			} else {
				throw new IOException("/ by zero");
			}
		}
	}
	
	public static int getPriority(String operation) {
		if (operation.matches("[+-]")) {
			return 1;
		} else if (operation.matches("[*/]")) {
			return 2;
		} else {
			return 0;
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < args.length; ++i) {
			int posInArgument = 0;
			while (posInArgument < args[i].length()) {
				String currentToken = Character.toString(args[i].charAt(posInArgument));	
				if (currentToken.matches("[0-9A-Ia-i]")){
					while ((posInArgument + 1 < args[i].length()) 
							&& (Character.toString(args[i].charAt(posInArgument + 1)).matches("[0-9A-Ia-i]"))) {
						currentToken += Character.toString(args[i].charAt(posInArgument + 1));
						++posInArgument;
					}
					try {					
						numbers.push(Integer.parseInt(currentToken, Radix));
					} catch(NumberFormatException catchedException) {
						System.err.println(catchedException);
						System.exit(1);	
					}
				} else if (currentToken.equals("(")) {
					operations.push("(");
				} else if (currentToken.equals(")")) {
					while (!(operations.isEmpty()) && !(operations.peek().equals("("))) {
						try {
							countOperation();
						} catch(IOException catchedException) {
							System.err.println(catchedException);
							System.exit(1);	
						}
					}
					if (!(operations.isEmpty())) {
						operations.pop();
					} else {
						System.err.println("mismatched quotes");
						System.exit(1);	
					}
				} else if (currentToken.matches("[*/+-]")) {
					while (!(operations.isEmpty()) && (getPriority(operations.peek()) >= getPriority(currentToken))) {
						try {
							countOperation();
						} catch(IOException catchedException) {
							System.err.println(catchedException);
							System.exit(1);	
						}
					}
					operations.push(currentToken);
				} else if (!currentToken.equals(" ")) {
					System.err.println("wrong expression");
					System.exit(1);	
				}
				
				++posInArgument;
			}
		}
		
		while (!operations.isEmpty()) {
			try {
				countOperation();
			} catch(IOException catchedException) {
				System.err.println(catchedException);
				System.exit(1);	
			}
		}
		
		if (numbers.isEmpty()) {
			System.err.println("wrong expression");
			System.exit(1);	
		} else {
			Integer result = numbers.pop();
			if (!numbers.isEmpty()) {
				System.err.println("wrong expression");
				System.exit(1);	
			} else {
				System.out.println(Integer.toString(result, Radix));	
			}			
		}
	}
}
