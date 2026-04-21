package com.unframe.rlt;

import java.util.*;

/// Represents a list of upto 256 values of the same type
public class RLTArray extends RLTValue implements Iterable<RLTValue> {
    public final RLTValueType elementType;
    private final List<RLTValue> values;

    private RLTArray(RLTValueType elementType, List<RLTValue> values) {
        this.elementType = elementType;
        if(values.size() > 256) throw new RLTException("Cannot create an RLT array with more than 256 values");
        this.values = values;
    }
    public RLTArray(RLTValueType elementType) {
        this.elementType = elementType;
        this.values = new ArrayList<>();
    }

    public void append(RLTValue v) throws RLTException {
        if(v.getType() != elementType)
            throw new RLTException(String.format("Invalid insertion attempt: Tried to insert %s into array of type %s", v.getType(), elementType));
        else values.add(v);
    }

    public List<RLTValue> getValues() {
        return values;
    }

    public RLTValue getValue(byte index) {
        return values.get(index);
    }

    @Override
    public RLTValueType getType() {
        return RLTValueType.ARRAY;
    }

    /// @return The number of elements in the array
    @Override
    int getSize() {
        return values.size();
    }

    /// @return The array formatted as a json array
    @Override
    public String toString() {
        return Arrays.toString(values.toArray());
    }

    @Override
    public Iterator<RLTValue> iterator() {
        return values.iterator();
    }
}
