package com.unframe.rlt;

import com.unframe.rlt.bin.ByteStack;

import java.util.ArrayList;
import java.util.Collections;

/// Converts RLT object v1 to a ByteStack
/// @since RLT 1
public class RLTEncoder {
    /// Converts the RLT object into a ByteStack
    public ByteStack encode(RLT rlt) throws RLTException.FileException {
        ByteStack b = ByteStack.large();
        b.push6x2(rlt.getVersion(), (byte)rlt.getValue().getType().getRepresentation()); //Push version and type
        for(int i : rlt.codec.getData()) { //Push codec
            b.push((byte)((i & 0xFF000000) >>> 24));
            b.push((byte)((i & 0xFF0000) >>> 16));
            b.push((byte)((i & 0xFF00) >>> 8));
            b.push((byte)(i & 0xFF));
        }
        if(rlt.getValue().getType() == RLTValueType.STRING) throw new RLTException.FileException("Invalid RLTv1 extension, string type is not allowed in root");
        if(!rlt.isEmpty()) { //Push main value
            pushValue(b, rlt.getValue());
        }
        return b;
    }
    private void pushValue(ByteStack b, RLTValue v) {
        switch (v.getType()) {
            case OBJECT -> pushObject(b, (RLTObject) v);
            case ARRAY -> pushArray(b, (RLTArray) v);
            case DBLOCK -> pushBlock(b, (RLTDBlock) v);
            case STRING -> pushString(b, (RLTString) v);
            //Push nothing for EMPTY
        }
    }
    private void pushObject(ByteStack b, RLTObject v) {
        ArrayList<Byte> keys = new ArrayList<>(v.getKeys());
        Collections.sort(keys);
        b.push2x6((byte) RLTValueType.OBJECT.getRepresentation(), (byte) 0);//Push type 0 for object, last 6 bits empty
        b.push((byte)v.getSize()); //Push object size
        for(Byte key : keys) {
            b.push(key);//Push key
            pushValue(b, v.getValue(key)); //Push value
        }
    }
    private void pushArray(ByteStack b, RLTArray v) {
        b.push2x6((byte) RLTValueType.ARRAY.getRepresentation(), (byte)v.elementType.getRepresentation()); //Push array type and subtype
        b.push((byte)v.getSize()); //Push array size
        for(RLTValue n : v.getValues()) {
            pushValue(b, n); //Push elements
        }
    }
    private void pushString(ByteStack b, RLTString v) {
        int s = v.getSize(); //Assume string length is within bounds
        if (s > 0x3FFF) throw new IllegalStateException("Length overflow for RLTString(invalid extension)");
        b.push2x6((byte)RLTValueType.STRING.getRepresentation(), (byte) ((s & 0x3F00) >> 8)); //Push type string and added length
        b.push((byte)s); //Push remaining length
        for(char i : v.str().toCharArray()) {
            b.push((byte)(i >>> 8));
            b.push((byte)i);
        }
    }
    private void pushBlock(ByteStack b, RLTDBlock v) {
        int s = v.getSize(); //Assume db length is within bounds
        if (s > 0x3FFF) throw new IllegalStateException("Length overflow for RLT DBlock(invalid extension)");
        b.push2x6((byte)RLTValueType.DBLOCK.getRepresentation(), (byte) ((s & 0x3F00) >> 8)); //Push type dblock and added length
        b.push((byte)s); //Push remaining length
        for(byte n : v.getStack().getArray()) {
            b.push(n); //Push data
        }
    }
}
