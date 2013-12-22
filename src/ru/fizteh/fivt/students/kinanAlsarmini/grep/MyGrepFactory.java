package ru.fizteh.fivt.students.kinanAlsarmini.grep;

import ru.fizteh.fivt.file.GrepFactory;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class MyGrepFactory implements GrepFactory {
    @Override
    public MyGrep create(String strPattern) {
        if (strPattern == null) {
            throw new IllegalArgumentException("Invalid pattern.");
        }

        Pattern pattern;
        try {
            pattern = Pattern.compile(strPattern);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid pattern.");
        }

        return new MyGrep(pattern);
    }
}
