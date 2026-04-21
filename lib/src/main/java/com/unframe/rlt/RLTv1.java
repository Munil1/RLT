package com.unframe.rlt;

import com.unframe.rlt.bin.ByteStack;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/// An object of RLT version 1
/// This loads the whole file into RAM and uses {@link RLTEncoder} and {@link RLTParser} for processing
/// @since RLT 1
public class RLTv1 extends RLT {
    protected final RLTValueType type;
    protected final RLTValue value;
    public RLTv1(RLTValue v, CodecID codec) {
        super(codec);
        this.type = v.getType();
        this.value = v;
    }
    RLTv1(CodecID codec) {
        super(codec);
        type = RLTValueType.EMPTY;
        value = null;
    }
    public static RLTv1 empty(CodecID codec) {
        return new RLTv1(codec);
    }
    @Override
    public byte getVersion() {
        return 1;
    }
    @Override
    public RLTObject obj() {
        if(type != RLTValueType.OBJECT) throw new RLTException("Invalid type for rlt file, expected object but got " + type);
        else return (RLTObject) value;
    }
    @Override
    public RLTArray array() {
        if(type != RLTValueType.ARRAY) throw new RLTException("Invalid type for rlt file, expected array but got " + type);
        else return (RLTArray) value;
    }
    @Override
    public RLTDBlock dblock() {
        if(type != RLTValueType.DBLOCK) throw new RLTException("Invalid type for rlt file, expected data block but got " + type);
        else return (RLTDBlock) value;
    }
    public boolean isEmpty() {
        return type == RLTValueType.EMPTY || value == null;
    }

    @Override
    public RLTValue getValue() {
        return value;
    }

    /// Converts the file into an RLT object of RLTv1
    /// @throws RLTException.FileException If the version is not 1 or the file is corrupted
    public static RLT read(ByteStack b) throws RLTException.FileException {
        RLTParser p = new RLTParser();
        return p.parse(b);
    }
    /// Writes the binary representation for the RLT into the provided file, overwriting existing content
    /// @throws RLTException.FileException If the RLT is invalid or writing fails
    /// @throws IOException If writing fails for IO related reasons
    public static void write(File f, RLT rlt) throws RLTException.FileException, IOException {
        try(BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f))) {
            RLTEncoder en = new RLTEncoder();
            ByteStack b = en.encode(rlt);
            out.write(b.getArray());
        }
    }
}
