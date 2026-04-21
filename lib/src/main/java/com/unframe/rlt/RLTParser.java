package com.unframe.rlt;

import com.unframe.rlt.bin.ByteStack;

import java.util.NoSuchElementException;

/// Converts BinaryStack to RLT object
/// @since RLT 1
public class RLTParser {
    private final byte[] slot = new byte[2];
    public RLT parse(ByteStack stack) throws RLTException.FileException {
        ByteStack b = stack.copy();
        b.flip();
        b.pop6x2(slot);
        byte version = slot[0];
        byte type = slot[1]; //Only for empty signalling, otherwise it should be repeated with the header
        if(version != 1)throw new RLTException.FileException("Invalid RLT file passed to RLTv1 parser, this RLT file is of version " + version);
        try {
            CodecID c = new CodecID(b.pop4(), b.pop4(), b.pop4(), b.pop4(), b.pop4(), b.pop4(), b.pop4(), b.pop4());
            RLT ret = type == 3 ? RLTv1.empty(c) : new RLTv1(popValue(b), c);

            b.trim();
            if (!b.isEmpty()) throw new RLTException.FileException("Garbage bytes at the end of RLT file");
            return ret;
        } catch(NoSuchElementException exc) { //Indicates the stack is empty
            throw new RLTException.FileException("Corrupt file: expected RLT file header, found end of data", exc);
        }
    }
    private RLTValue popValue(ByteStack b) {
        try {
            b.pop2x6(slot);
            int length = ((slot[1] & 0x3F) << 8) | (b.pop() & 0xFF); //Parse value header
            if (slot[0] == 0 && (slot[1] & 0x3F) != 0) {
                throw new RLTException.FileException("Invalid header: garbage bits before object length, indicates file corruption");
            } else if (slot[0] == 1 && (slot[1] & 0x3C) != 0) {
                throw new RLTException.FileException("Invalid header: garbage bits before array subtype, indicates file corruption");
            }
            return switch (slot[0]) {
                case 0 -> popObject(b, (length & 0xFF));
                case 1 -> popArray(b, (length & 0xFF), (byte) (slot[1] & 3));
                case 2 -> popDBlock(b, (short) length);
                case 3 -> popString(b, (short) length);
                default ->
                        throw new RLTException.FileException("Error in parsing, invalid type found(this shouldn't be possible)");
            };
        } catch(NoSuchElementException exc) { //Indicates the stack is empty
            throw new RLTException.FileException("Corrupt file: expected value header, found end of data", exc);
        }
    }
    // Excludes 'value' header
    private RLTArray popArray(ByteStack b, int length, byte subtype) {
        RLTArray array = new RLTArray(RLTValueType.forRepresentation(subtype));
        for(int i = 0; i < length; i++) {
            try {
                array.append(popValue(b));
            } catch(NoSuchElementException exc) { //Indicates the stack is empty
                throw new RLTException.FileException("Corrupt file: expected array value, found end of data", exc);
            }
        }
        return array;
    }
    // Excludes 'value' header
    private RLTObject popObject(ByteStack b, int length) {
        RLTObject obj = new RLTObject();
        for(int i = 0; i < length; i++) {
            byte key = b.pop();
            try {
                obj.addValue(key, popValue(b));
            } catch(NoSuchElementException exc) { //Indicates the stack is empty
                throw new RLTException.FileException("Corrupt file: expected object entry, found end of data", exc);
            }
        }
        return obj;
    }
    // Excludes 'value' header
    private RLTString popString(ByteStack b, short length) {
        char[] v = new char[length];
        for(int i = 0; i < length; i++) {
            try {
                byte a1 = b.pop();
                v[i] = (char) (((a1 & 0xFF) << 8) | (b.pop() & 0xFF));
            } catch(NoSuchElementException exc) { //Indicates the stack is empty
                throw new RLTException.FileException("Corrupt file: expected string character, found end of data", exc);
            }
        }
        return new RLTString(new String(v));
    }
    // Excludes 'value' header
    private RLTDBlock popDBlock(ByteStack b, short length) {
        ByteStack content = new ByteStack(length);
        for(int i = 0; i < length; i++) {
            try {
                content.push(b.pop());
            } catch(NoSuchElementException exc) { //Indicates the stack is empty
                throw new RLTException.FileException("Corrupt file: expected data block, found end of data", exc);
            }
        }
        return new RLTDBlock(content);
    }
}
