package ru.fizteh.fivt.students.dubovpavel.proxy;

public class ProxyUtils {
    public static String generateRepr(Object obj, String internal) {
        StringBuilder repr = new StringBuilder(obj.getClass().getSimpleName());
        repr.append('[');
        repr.append(internal);
        repr.append(']');
        return repr.toString();
    }
}
