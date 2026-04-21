package com.unframe.rlt.bin;

import java.util.BitSet;

/// A list of bytes for binary handling, which contains a stack-like data structure of bytes and nibbles
/// <br>The cursor supports inserting and removing from in between elements as well
@Deprecated
public class ByteCursor {
    private BitSet bits = new BitSet();
    /// Creates a new ByteCursor with a set of Bit4s
    public ByteCursor(Bit4... b) {
        this.append(b);
    }
    ByteCursor() {}
    /// The number of nibbles stored
    private int size = 0;
    /// Adds bytes to the top of the stack
    public void append(Bit4... bytes) {
        for(Bit4 b : bytes) {
            push(b);
        }
    }
    /// Adds 4 bits to the top of the stack
    public void push(Bit4 in) {
        byte v = in.value();
        int base = size * 4;
        bits.set(base, BitUtil.bitAtIndex(v, 0));
        bits.set(base + 1, BitUtil.bitAtIndex(v, 1));
        bits.set(base + 2, BitUtil.bitAtIndex(v, 2));
        bits.set(base + 3, BitUtil.bitAtIndex(v, 3));
        size++;
    }
    /// Removes the Bit4 from the top of the stack and returns it
    public Bit4 pop() {
        checkEmpty("Cannot pop from an empty stack");
        Bit4 out = top();
        size--;
        return out;
    }
    /// @return A copy of the Bit4 at the top of the stack
    public Bit4 top() {
        checkEmpty("Cannot get the top element of an empty stack");
        byte ret = 0;
        int base = size * 4;
        if(bits.get(base)) ret |= 1;
        if(bits.get(base + 1)) ret |= 2;
        if(bits.get(base + 2)) ret |= 3;
        if(bits.get(base + 3)) ret |= 4;
        return Bit4.of(ret);
    }
    /// Inserts a Bit4 into the middle of the stack
    /// @param index Inserts the item between index and index + 1 such that the item becomes index + 1
    public void insert(int index, Bit4 in) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException(String.format("Cannot insert at position %d in a stack of length %d", index, size));

        size++;

        for (int i = size - 1; i > index; i--) {
            set(i, get(i - 1));
        }

        set(index, in);
    }
    /// Replaces the index no on the stack
    public void set(int index, Bit4 in) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException(String.format("Cannot set position %d in a stack of length %d", index, size));

        byte v = in.value();
        int base = index * 4;

        bits.set(base, BitUtil.bitAtIndex(v, 0));
        bits.set(base + 1, BitUtil.bitAtIndex(v, 1));
        bits.set(base + 2, BitUtil.bitAtIndex(v, 2));
        bits.set(base + 3, BitUtil.bitAtIndex(v, 3));
    }
    /// Pops out index no. on the stack, removing it
    public Bit4 pop(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException(String.format("Cannot pop at position %d in a stack of length %d", index, size));

        Bit4 r = get(index);

        for (int i = index; i <size - 1; i++) {
            set(i, get(i + 1));
        }
        size--;
        return r;
    }
    /// Copies index no on the stack
    public Bit4 get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException(String.format("Cannot access position %d in a stack of length %d", index, size));

        int base = index * 4;
        int value = 0;

        if (bits.get(base)) value |= 1;
        if (bits.get(base + 1)) value |= 2;
        if (bits.get(base + 2)) value |= 4;
        if (bits.get(base + 3)) value |= 8;

        return Bit4.of((byte) value);
    }
    /// @return An indexed section of the stack, inclusive of end elements
    public ByteCursor subStackInclusive(int start, int end) {
        if (start < 0 || end < 0 || start >= size || end >= size) throw new IndexOutOfBoundsException(String.format("Cannot take subStack [%d, %d] from stack of length %d", start, end, size));

        if (start > end) throw new IllegalArgumentException(String.format("Start index %d cannot be greater than end index %d", start, end));

        ByteCursor cursor = empty();

        for (int i = start; i <= end; i++) {
            cursor.push(this.get(i));
        }

        return cursor;
    }
    /// @return The Cursor as an array of Bit4s, starting with the bottom element and ending with the top
    public Bit4[] toArray() {
        Bit4[] r = new Bit4[size];
        for(int i = 0; i < size; i++) {
            r[i] = this.get(i);
        }
        return r;
    }
    /// @return The Cursor as an array of bytes, starting with the bottom element and ending with the top, in order(highest bits first)
    public byte[] toByteArray() {
        int byteCount = (size + 1) / 2; // ceil(size / 2)
        byte[] out = new byte[byteCount];

        for (int i = 0; i < byteCount; i++) {
            int b = 0;
            int base = i * 8;
            if (bits.get(base)) b |= 1;
            if (bits.get(base + 1)) b |= 2;
            if (bits.get(base + 2)) b |= 4;
            if (bits.get(base + 3)) b |= 8;
            if (bits.get(base + 4)) b |= 16;
            if (bits.get(base + 5)) b |= 32;
            if (bits.get(base + 6)) b |= 64;
            if (bits.get(base + 7)) b |= 128;
            out[i] = (byte)b;
        }

        return out;
    }
    /// Pushes byte to the top of the stack, in order(top bit first)
    public void push(byte b) {
        int base = size * 4;
        bits.set(base, BitUtil.bitAtIndex(b, 7));
        bits.set(base + 1, BitUtil.bitAtIndex(b, 6));
        bits.set(base + 2, BitUtil.bitAtIndex(b, 5));
        bits.set(base + 3, BitUtil.bitAtIndex(b, 4));
        bits.set(base + 4, BitUtil.bitAtIndex(b, 3));
        bits.set(base + 5, BitUtil.bitAtIndex(b, 2));
        bits.set(base + 6, BitUtil.bitAtIndex(b, 1));
        bits.set(base + 7, BitUtil.bitAtIndex(b, 0));
        size += 2;
    }
    /// Appends one byte to the end of the stack(high nibble first)
    /// @param two The first two bits of this get appended first
    /// @param six The first six bits get appended after two to make one byte
    public void push2x6(byte two, byte six) {
        two = (byte) (two & 3);
        six = (byte) (six & 0b111111);
        push((byte) ((two << 6) | six));
    }
    /// Appends one byte to the end of the stack(high nibble first)
    /// @param six The first 6 bits of this get appended first
    /// @param two The first two bits get appended after to make a byte
    public void push6x2(byte six, byte two) {
        two = (byte) (two & 3);
        six = (byte) (six & 0b111111);
        push((byte) ((six << 2) | two));
    }
    /// Returns the number of nibbles in the cursor's stack
    public int size() {
        return size;
    }
    /// Removes excess occupied memory from the internal BitSet
    public void trim() {
        bits = bits.get(0, size * 4);
    }
    /// Empties the cursor's stack
    public void clear() {
        size = 0;
        bits = new BitSet();
    }
    /// Flips the cursor so that the top of the stack becomes the bottom
    public ByteCursor flip() {
        ByteCursor out = empty();
        for(int i = 0; i < size; i += 2) {
            byte b = (byte) ((get(i).value() & 0x0F) | ((get(i + 1).value() & 0xF0) >>> 4));
            out.push(BitUtil.flip(get(i).value()));
        }
        return null;
    }
    /// Returns a copy of the cursor with the stack
    public ByteCursor copy() {
        return subStackInclusive(0, size-1);
    }
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("ByteStack[B|");
        for(byte b : toByteArray()) {
            char[] bits = new char[4];
            for (int i = 3; i >= 0; i--) {
                bits[3 - i] = ((b >> i) & 1) == 1 ? '1' : '0';
            }
            str.append(bits);
            str.append('|');
        }
        str.append("T]");
        return str.toString();
    }
    private void checkEmpty(String m) {
        if(size == 0) throw new IndexOutOfBoundsException(m);
    }
    /// @return An empty ByteCursor
    public static ByteCursor empty() {
        return new ByteCursor();
    }
}
