package com.example.jsontreeview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class for parsing JSON strings into JsonNode trees and vice versa.
 */

public class JsonParserUtil {

    //Parses a JSON string into a list of JsonNode trees.
    public static List<JsonNode> parseJson(String jsonString) throws JSONException {
        List<JsonNode> nodes = new ArrayList<>();
        Object json = new JSONTokener(jsonString).nextValue();

        if (json instanceof JSONObject) {
            nodes.addAll(parseObject((JSONObject) json, "root"));
        } else if (json instanceof JSONArray) {
            nodes.addAll(parseArray((JSONArray) json, "root"));
        }

        return nodes;
    }

    //Recursively parses a JSONObject into JsonNodes.
    private static List<JsonNode> parseObject(JSONObject obj, String key) throws JSONException {
        JsonNode parent = new JsonNode(key, "");
        List<JsonNode> children = new ArrayList<>();

        Iterator<String> keys = obj.keys();
        while (keys.hasNext()) {
            String childKey = keys.next();
            Object value = obj.get(childKey);

            if (value instanceof JSONObject) {
                children.addAll(parseObject((JSONObject) value, childKey));
            } else if (value instanceof JSONArray) {
                children.addAll(parseArray((JSONArray) value, childKey));
            } else {
                String valStr = value == JSONObject.NULL ? "null" : value.toString();
                children.add(new JsonNode(childKey, valStr));
            }
        }

        parent.setChildren(children);
        List<JsonNode> result = new ArrayList<>();
        result.add(parent);
        return result;
    }

    //Recursively parses a JSONArray into JsonNodes.
    private static List<JsonNode> parseArray(JSONArray arr, String key) throws JSONException {
        JsonNode parent = new JsonNode(key, "");
        List<JsonNode> children = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            Object item = arr.get(i);

            if (item instanceof JSONObject) {
                children.addAll(parseObject((JSONObject) item, "[" + i + "]"));
            } else if (item instanceof JSONArray) {
                children.addAll(parseArray((JSONArray) item, "[" + i + "]"));
            } else {
                String valStr = item == JSONObject.NULL ? "null" : item.toString();
                children.add(new JsonNode("[" + i + "]", valStr));
            }
        }

        parent.setChildren(children);
        List<JsonNode> result = new ArrayList<>();
        result.add(parent);
        return result;
    }

    //Rebuilds a JSONObject from a list of JsonNode trees.
    public static JSONObject buildJsonFromNodes(List<JsonNode> nodes) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        for (JsonNode node : nodes) {
            String key = node.getKey();
            String value = node.getValue();
            List<JsonNode> children = node.getChildren();

            if (node.hasChildren()) {
                if (key.equals("root")) {
                    JSONObject childObj = buildJsonFromNodes(children);
                    Iterator<String> keys = childObj.keys();
                    while (keys.hasNext()) {
                        String childKey = keys.next();
                        jsonObject.put(childKey, childObj.get(childKey));
                    }
                } else if (isArrayNode(children)) {
                    JSONArray array = buildJsonArray(children);
                    jsonObject.put(key, array);
                } else {
                    JSONObject childObject = buildJsonFromNodes(children);
                    jsonObject.put(key, childObject);
                }
            } else {
                jsonObject.put(key, parseValue(value));
            }
        }

        return jsonObject;
    }

    //Checks if the node list represents an array (i.e., all keys are [index]).
    private static boolean isArrayNode(List<JsonNode> nodes) {
        for (JsonNode node : nodes) {
            String key = node.getKey();
            if (key != null && !key.matches("\\[\\d+\\]")) {
                return false;
            }
        }
        return true;
    }

    //Builds a JSONArray from a list of JsonNodes.
    private static JSONArray buildJsonArray(List<JsonNode> nodes) throws JSONException {
        JSONArray array = new JSONArray();
        for (JsonNode node : nodes) {
            if (node.hasChildren()) {
                if (isArrayNode(node.getChildren())) {
                    array.put(buildJsonArray(node.getChildren()));
                } else {
                    array.put(buildJsonFromNodes(node.getChildren()));
                }
            } else {
                array.put(parseValue(node.getValue()));
            }
        }
        return array;
    }


    //Parses a value string into the correct type (null, boolean, number, or string).
    private static Object parseValue(String value) {
        if (value == null) return JSONObject.NULL;
        value = value.trim();
        if (value.equalsIgnoreCase("null")) return JSONObject.NULL;
        if (value.equalsIgnoreCase("true")) return true;
        if (value.equalsIgnoreCase("false")) return false;
        try {
            if (value.contains(".")) return Double.parseDouble(value);
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return value;
        }
    }
}
