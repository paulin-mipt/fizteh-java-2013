package ru.fizteh.fivt.students.dzvonarev.filemap;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Parser {

    public List<Class<?>> parseTypeList(List<String> types) throws ParseException {
        List<Class<?>> temp = new ArrayList<>();
        for (String type : types) {
            switch (type) {
                case "int":
                    temp.add(Integer.class);
                    break;
                case "double":
                    temp.add(Double.class);
                    break;
                case "float":
                    temp.add(Float.class);
                    break;
                case "boolean":
                    temp.add(Boolean.class);
                    break;
                case "long":
                    temp.add(Long.class);
                    break;
                case "byte":
                    temp.add(Byte.class);
                    break;
                case "String":
                    temp.add(String.class);
                    break;
                default:
                    throw new ParseException("wrong type (" + type + ")", 0);
            }
        }
        return temp;
    }

    public ArrayList<Object> parseValueToList(String value) throws ParseException {
        if (value == null || value.trim().isEmpty()) {
            throw new ParseException("wrong type (invalid type of" + value + ")", 0);
        }
        ArrayList<Object> temp = new ArrayList<>();
        JSONArray jArr;
        try {
            jArr = new JSONArray(value);
        } catch (JSONException e) {
            throw new ParseException("wrong type (invalid type of " + value + ")", 0);
        }
        for (int i = 0; i < jArr.length(); ++i) {
            temp.add(jArr.get(i));
        }
        return temp;
    }

    public List<String> getTypesFrom(String str) throws ParseException {
        List temp = new ArrayList();
        if (str.charAt(0) != '(' || str.charAt(str.length() - 1) != ')') {
            throw new ParseException("wrong type (invalid type of " + str + ")", 0);
        }
        String source = str.substring(1, str.length() - 1);
        String[] types = source.trim().split("\\s+");
        if (!typeNamesAreValid(types)) {
            throw new ParseException("wrong type (invalid type of " + str + ")", 0);
        }
        Collections.addAll(temp, types);
        return temp;
    }

    public boolean typeNamesAreValid(String[] types) {
        for (String type : types) {
            if (!(type.equals("int") || type.equals("double") || type.equals("long")
                    || type.equals("float") || type.equals("boolean") || type.equals("String")
                    || type.equals("byte"))) {
                return false;
            }
        }
        return true;
    }

    public boolean canBeCastedTo(Class<?> type, Object obj) {
        if (obj == null || obj.equals(null)) {
            return true;
        }
        if (obj.getClass().equals(Integer.class)) {
            Integer num = (Integer) obj;
            if (type.equals(Byte.class)) {
                return num >= -128 && num <= 127;
            }
            return type.equals(Integer.class) || type.equals(Long.class) || type.equals(Double.class)
                    || type.equals(Float.class);
        }
        if (obj.getClass().equals(Long.class)) {
            return type.equals(Long.class) || type.equals(Double.class);
        }
        if (obj.getClass().equals(Boolean.class)) {
            return type.equals(Boolean.class);
        }
        if (obj.getClass().equals(String.class)) {
            return type.equals(String.class);
        }
        if (obj.getClass().equals(Byte.class)) {
            return !type.equals(String.class) && !type.equals(Boolean.class);
        }
        if (obj.getClass().equals(Float.class)) {
            return type.equals(Double.class) || type.equals(Float.class);
        }
        return obj.getClass().equals(Double.class) && (type.equals(Double.class) || type.equals(Float.class));
    }


}
