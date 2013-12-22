package ru.fizteh.fivt.students.adanilyak.tools;

import java.util.List;

/**
 * User: Alexander
 * Date: 22.11.13
 * Time: 1:59
 */
public class StringCreationTools {
    public static String createString(String className, List<Object> storeableListOfObjects) {
        StringBuilder builder = new StringBuilder(className);
        builder.append("[");
        Object object;
        for (Integer i = 0; i < storeableListOfObjects.size(); ++i) {
            object = storeableListOfObjects.get(i);
            if (object != null) {
                builder.append(object.toString());
            } else {
                builder.append("");
            }
            if (i != storeableListOfObjects.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    public static String cutTimeStamp(String proxyResult) {
        return proxyResult.replaceFirst(" timestamp=\"[0-9]+\"", "").trim();
    }
}
