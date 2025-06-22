package com.example.jsontreeview;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a single node in a JSON tree structure.
 */

public class JsonNode {
    private String key;
    private String value;
    private String valueType;
    private List<JsonNode> children;
    private boolean expanded;
    private int depth;

    //Constructor for a JsonNode with key and value.
    public JsonNode(String key, String value) {
        this.key = key;
        this.value = value;
        this.valueType = detectType(value); // Detect type based on value
        this.children = new ArrayList<>();
        this.expanded = false;
        this.depth = 0;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    //Updates the value and re-detects the type.
    public void setValue(String value) {
        this.value = value;
        this.valueType = detectType(value); // Recalculate type on value update
    }

    //Returns the detected value type (string, number, boolean, or null).
    public String getValueType() {
        return valueType;
    }

    public List<JsonNode> getChildren() {
        return children;
    }

    public void setChildren(List<JsonNode> children) {
        this.children = children;
    }

    //Checks if the node has any children.
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    //Detects the type of the value: string, number, boolean, or null.
    private String detectType(String value) {
        if (value == null || value.equalsIgnoreCase("null")) return "null";
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) return "boolean";
        try {
            Double.parseDouble(value);
            return "number";
        } catch (NumberFormatException e) {
            return "string";
        }
    }
}
