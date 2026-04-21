package com.unframe.rlt;

/// An enum for all the value types in rlt
public enum RLTValueType {
    OBJECT(0), ARRAY(1), DBLOCK(2), STRING(3), EMPTY(3);
    private final int representation;
    RLTValueType(int v) {
        this.representation = v;
    }

    public static RLTValueType forRepresentation(int representation) {
        return switch (representation) {
            case 0 -> OBJECT;
            case 1 -> ARRAY;
            case 2 -> DBLOCK;
            case 3 -> STRING;
            default -> EMPTY;
        };
    }

    public int getRepresentation() {
        return representation;
    }
}
