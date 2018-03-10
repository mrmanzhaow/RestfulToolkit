package com.zhaow.utils;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.intellij.openapi.util.text.StringUtil;

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
}