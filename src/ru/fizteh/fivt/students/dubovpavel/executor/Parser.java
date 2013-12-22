package ru.fizteh.fivt.students.dubovpavel.executor;

import java.util.ArrayList;

public class Parser {
    public static class IncorrectSyntaxException extends Exception {
        public IncorrectSyntaxException(String message) {
            super(message);
        }
    }

    private boolean charIsNotSystem(char c) {
        return !Character.isWhitespace(c) && c != ';' && correspondingBound(c) == '\0';
    }

    private char correspondingBound(char bound) {
        switch (bound) {
            case '\'':
                return '\'';
            case '"':
                return '"';
            case '(':
                return ')';
            case '[':
                return ']';
            default:
                return '\0';
        }
    }

    private boolean exclusiveBound(char bound) {
        switch (bound) {
            case '\'':
            case '"':
                return true;
            case ']':
            case ')':
                return false;
            default:
                return true;
        }
    }

    public ArrayList<Command> getCommands(Dispatcher dispatcher, String line) throws IncorrectSyntaxException {
        int pointer = 0;
        ArrayList<Command> result = new ArrayList<Command>();
        Command command = null;
        while (pointer < line.length()) {
            if (Character.isWhitespace(line.charAt(pointer))) {
                pointer++;
            } else if (correspondingBound(line.charAt(pointer)) != '\0') {
                int start = pointer;
                char corresponding = correspondingBound(line.charAt(pointer));
                for (pointer++; pointer < line.length() && line.charAt(pointer) != corresponding; pointer++) {
                    // Moving towards corresponding char
                }
                if (pointer >= line.length()) {
                    throw new IncorrectSyntaxException(
                            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR, "Bounders are not closed"));
                }
                if (command == null) { // Header
                    pointer++;
                    command = new Command(line.substring(start, pointer), result.size() + 1);
                } else {
                    int left;
                    int right;
                    pointer++;
                    if (exclusiveBound(corresponding)) {
                        left = start + 1;
                        right = pointer - 1;
                    } else {
                        left = start;
                        right = pointer;
                    }
                    command.addArgument(line.substring(left, right));
                }
            } else if (line.charAt(pointer) == ';') {
                if (command != null) {
                    result.add(command);
                    command = null;
                }
                pointer++;
            } else {
                int start = pointer;
                while (pointer < line.length() && charIsNotSystem(line.charAt(pointer))) {
                    pointer++;
                }
                String word = line.substring(start, pointer);
                if (command == null) {
                    command = new Command(word, result.size() + 1);
                } else {
                    command.addArgument(word);
                }
            }
        }
        if (command != null) {
            result.add(command);
        }
        return result;
    }
}
