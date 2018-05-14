package com.zhaow.utils;

import com.google.gson.*;
import com.intellij.openapi.util.text.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaow
 */
public class JsonUtils {
    public JsonUtils() {
    }

    public static boolean isValidJson(String json) {
        if (StringUtil.isEmptyOrSpaces(json)) {
            return false;
        }
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }


    public static String format(String str) {
        JsonParser parser = new JsonParser();
        JsonElement parse = parser.parse(str);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(parse);
        return json;
    }
}