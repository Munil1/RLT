package com.unframe.rlt;

/// A wrapper around the String class representing a String value in an RLT file
public class RLTString extends RLTValue {
    private final String v;

    public RLTString(String v) {
        if(v.length() > 16383)throw new RLTException("RLTString is too large, cannot be instantiated with more than 16383 characters");
        this.v = v;
    }

    public String str() {
        return v;
    }

    @Override
    public RLTValueType getType() {
        return RLTValueType.STRING;
    }

    /// @return The number of characters in the string value
    @Override
    int getSize() {
        return v.length();
    }

    /// @return The string formatted as a json escaped string with double quotes
    @Override
    public String toString() {
        return "\"" + escape(str()) + "\"";
    }

    private String escape(String s) {
        return s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
