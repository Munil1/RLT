package com.unframe.rlt;

import com.unframe.rlt.bin.ByteStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/// Superclass for all RLT classes
/// @since RLT 1
public abstract class RLT {
    protected final CodecID codec;
    protected RLT(CodecID codec) {
        this.codec = codec;
    }
    public void throwCodec(CodecID pCodec) throws RLTException {
        if(pCodec != codec) throw new RLTException(String.format("Invalid codec for rlt file %s, expected %s", codec, pCodec));
    }
    public abstract byte getVersion();
    public abstract RLTObject obj() throws RLTException;
    public abstract RLTArray array() throws RLTException;
    public abstract RLTDBlock dblock() throws RLTException;
    public abstract boolean isEmpty();
    /// @throws RLTException If the value is null
    public abstract RLTValue getValue() throws RLTException;
    /// Converts the file into an RLT object of the highest compatible version
    /// @throws RLTException.FileException If the version is unrecognized or the file is corrupted
    /// @throws IOException If reading fails for IO related reasons
    public static RLT read(File f) throws RLTException.FileException, IOException {
        ByteStack b = new ByteStack(Files.readAllBytes(f.toPath()));
        byte version = (byte)((b.getData()[0] >>> 2) & 0x3F); //Extract version
        return switch(version) {
            case 1 -> RLTv1.read(b);
            default -> throw new RLTException.FileException("Unrecognized RLT version " + version);
        };
    }
    /// Writes the binary representation for the RLT into the provided file, overwriting existing content
    /// @throws RLTException.FileException If the RLT is invalid or writing fails
    /// @throws IOException If writing fails for IO related reasons
    public static void write(File f, RLT rlt) throws RLTException.FileException, IOException {
        switch (rlt.getVersion()) {
            case 1 -> RLTv1.write(f, rlt);
            default -> throw new RLTException.FileException("Unrecognized RLT version " + rlt.getVersion());
        }
    }
}
