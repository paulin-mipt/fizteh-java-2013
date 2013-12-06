package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.IdentityHashMap;

public class FileMapLoggingJson {
    private Object object;
    private IdentityHashMap<Object, Object> identifyAttended = new IdentityHashMap<>();

    FileMapLoggingJson(Object object) {
        this.object = object;
    }

    JSONArray getJSONArray() {
        JSONArray creatingArray = new JSONArray();
        return logArgInJson(creatingArray, object);
    }

    private JSONArray logArgInJson(JSONArray jsonArray, Object obj) {
        JSONArray newCreatingArray = new JSONArray();
        if (obj != null) {
            if (Iterable.class.isAssignableFrom(obj.getClass())) {
                if (identifyAttended.containsKey(obj)) {
                    jsonArray.put("cyclic");
                } else {
                    identifyAttended.put(obj, obj);
                    for (Object element: (Iterable) obj) {
                        try {
                            newCreatingArray = logArgInJson(newCreatingArray, element);
                        } catch (java.lang.ClassCastException e) {
                            newCreatingArray.put(element.toString());
                        }
                    }
                    identifyAttended.remove(obj);
                    jsonArray.put(newCreatingArray);
                }
            } else if (obj.getClass().isArray()) {
                if (identifyAttended.containsKey(obj)) {
                    jsonArray.put("cyclic");
                } else {
                    identifyAttended.put(obj, obj);
                    for (Object element: (Object[]) obj) {
                        try {
                            newCreatingArray = logArgInJson(newCreatingArray, element);
                        } catch (java.lang.ClassCastException e) {
                            newCreatingArray.put(element.toString());
                        }
                    }
                    identifyAttended.remove(obj);
                    jsonArray.put(newCreatingArray);
                }
            } else {
                try {
                    JSONArray copy = new JSONArray();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        copy.put(jsonArray.get(i));
                    }
                    jsonArray.put(obj);
                    if (jsonArray.toString() == null) {
                        jsonArray = copy;
                        jsonArray.put(obj.toString());
                    }
                } catch (java.lang.ClassCastException e) {
                    jsonArray.put(obj.toString());
                }
            }
        } else {
            jsonArray.put(JSONObject.NULL);
        }
        return jsonArray;
    }

}
