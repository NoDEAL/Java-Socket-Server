package com.nodeal.socket.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JSONUtil {
    private static JSONParser jsonParser;
    public static JSONParser getJsonParser() {
        if (jsonParser == null) jsonParser = new JSONParser();

        return jsonParser;
    }

    public static JSONObject makeOKMessage() {
        return makeOKMessage(new JSONObject());
    }

    public static JSONObject makeOKMessage(JSONObject jsonObject) {
        jsonObject.put("result", true);

        return jsonObject;
    }

    public static JSONObject makeErrorMessage(String code) {
        return makeErrorMessage(new JSONObject(), code, getCallerClassName());
    }

    public static JSONObject makeErrorMessage(JSONObject jsonObject, String code, String created) {
        jsonObject.put("result", false);
        jsonObject.put("code", code);
        jsonObject.put("created", created);

        return jsonObject;
    }

    private static String getCallerClassName() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        for (int i = 1; i < stackTraceElements.length; i++) {
            StackTraceElement stackTraceElement = stackTraceElements[i];

            if (!stackTraceElement.getClassName().equals(JSONUtil.class.getName()) && stackTraceElement.getClassName().indexOf("java.lang.Thread") != 0) {
                return stackTraceElement.getClassName();
            }
        }

        return null;
    }
}
