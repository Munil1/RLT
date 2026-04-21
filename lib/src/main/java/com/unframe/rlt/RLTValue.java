package com.unframe.rlt;

/// Represents a single value in an RLT file, either a string, data block, object or array
public abstract class RLTValue {
    /// Returns the enum type of the value
    public abstract RLTValueType getType();
    /// Returns the size of the value, different metrics depending on the type
    abstract int getSize();
}
