package com.unframe.rlt;

import java.util.*;

/// Represents an Object in an RLT file which contains a list of values mapped to keys from 0 to 255
public class RLTObject extends RLTValue {
    private final HashMap<Byte, RLTValue> data;

    private RLTObject(HashMap<Byte, RLTValue> data) {
        if(data.size() > 256) throw new RLTException("Cannot create an RLT object with more than 256 key-value pairs");
        this.data = data;
    }
    public RLTObject() {
        this.data = new HashMap<>();
    }

    /// Adds the value to the object or replaces it if it already exists
    public void addValue(byte key, RLTValue v) {
        data.put(key, v);
    }

    /// Returns the value mapped to the provided key in the object
    /// @throws RLTException If the object does not contain the key
    public RLTValue getValue(byte key) throws RLTException {
        if(data.containsKey(key)) return data.get(key);
        else throw new RLTException("RLT object asked for key which is not present");
    }

    /// Returns the string value mapped to the provided key in the object
    /// @throws RLTException If the object does not contain the key or the key is not of type string
    public RLTString getString(byte key) throws RLTException {
        RLTValue v = getValue(key);
        if(v.getType() == RLTValueType.STRING) return (RLTString) v;
        else throw new RLTException("RLT object asked for a string with a key actually mapped to " + v.getType());
    }

    /// Returns the array value mapped to the provided key in the object
    /// @throws RLTException If the object does not contain the key or the key is not of type array
    public RLTArray getArray(byte key) throws RLTException {
        RLTValue v = getValue(key);
        if(v.getType() == RLTValueType.ARRAY) return (RLTArray) v;
        else throw new RLTException("RLT object asked for an array with a key actually mapped to " + v.getType());
    }

    /// Returns the sub-object value mapped to the provided key in the object
    /// @throws RLTException If the object does not contain the key or the key is not of type object
    public RLTObject getObject(byte key) throws RLTException {
        RLTValue v = getValue(key);
        if(v.getType() == RLTValueType.OBJECT) return (RLTObject) v;
        else throw new RLTException("RLT object asked for an object with a key actually mapped to " + v.getType());
    }

    /// Returns the data-block value mapped to the provided key in the object
    /// @throws RLTException If the object does not contain the key or the key is not of type data-block
    public RLTDBlock getDataBlock(byte key) throws RLTException {
        RLTValue v = getValue(key);
        if(v.getType() == RLTValueType.DBLOCK) return (RLTDBlock) v;
        else throw new RLTException("RLT object asked for a data block with a key actually mapped to " + v.getType());
    }

    /// Returns a collection of all the values in the object
    public Collection<RLTValue> getValues() {
        return data.values();
    }

    /// Returns a set of all keys in the object
    public Set<Byte> getKeys() {
        return data.keySet();
    }
    
    @Override
    public RLTValueType getType() {
        return RLTValueType.OBJECT;
    }

    /// @return The number of key-value pairs in the object
    @Override
    int getSize() {
        return data.size();
    }

    /// @return The object formatted as a json object
    public static String toString(RLTObject o) {
        StringBuilder s = new StringBuilder();
        s.append("{\n");
        Set<Byte> keys = o.getKeys();
        boolean first = true;
        for (byte v : keys) {
            if (!first) s.append(",\n");
            first = false;

            s.append(String.format("\t\"%d\":", v));

            switch (o.getValue(v).getType()) {
                case ARRAY -> s.append(Arrays.toString(o.getArray(v).getValues().toArray()));
                case OBJECT -> s.append(toString(o.getObject(v)));
                case STRING -> s.append(o.getString(v).toString());
                case DBLOCK -> s.append(Arrays.toString(o.getDataBlock(v).getStack().getArray()));
            }
        }
        s.append("\n}");
        return s.toString();
    }

    @Override
    public String toString() {
        return RLTObject.toString(this);
    }
}
