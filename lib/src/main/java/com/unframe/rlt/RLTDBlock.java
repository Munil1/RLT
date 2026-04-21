package com.unframe.rlt;

import com.unframe.rlt.bin.ByteStack;

import java.util.Arrays;

/// An unprocessed data block stored as a list of bytes
public class RLTDBlock extends RLTValue {
    private final ByteStack stack;

    /// @throws RLTException If the stack is larger than 16383 bytes(16 KiB, max limit)
    public RLTDBlock(ByteStack stack) {
        if(stack.size() > 16383)throw new RLTException("Rlt data block is too large, it cannot be more than 16383 bytes");
        this.stack = stack;
    }

    public ByteStack getStack() {
        return stack;
    }

    @Override
    public RLTValueType getType() {
        return RLTValueType.DBLOCK;
    }

    /// @return The number of bytes in the block
    @Override
    int getSize() {
        return stack.size();
    }

    /// @return The stack formatted as a json array of byte values
    @Override
    public String toString() {
        return Arrays.toString(stack.getArray());
    }
}
