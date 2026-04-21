package com.unframe.rlt.bin;

import java.util.NoSuchElementException;

/// A stack for working with byte data
public class ByteStack {
    private byte[] data;
    private int size;
    private boolean isFlipped = false;
    public ByteStack(int capacity) {
        data = new byte[capacity];
    }
    public ByteStack(byte[] dat) {
        data = dat;
        size = dat.length;
    }
    // // // Push methods // // //
    /// Pushes a byte to the top of the stack, top bits first
    public void push(byte b) {
        size++;
        ensureCapacity(size);
        data[size - 1] = b;
    }
    /// Appends one byte to the end of the stack, top bits first
    /// @param two The first two bits of this get appended first(top)
    /// @param six The first six bits get appended(below) after two to make one byte
    public void push2x6(byte two, byte six) {
        two = (byte) (two & 0x3);
        six = (byte) (six & 0x3F);
        push((byte) ((two << 6) | six));
    }
    /// Appends one byte to the end of the stack, top bits first
    /// @param six The first 6 bits of this get appended first(top)
    /// @param two The first two bits get appended(below) after to make a byte
    public void push6x2(byte six, byte two) {
        two = (byte) (two & 0x3);
        six = (byte) (six & 0x3F);
        push((byte) ((six << 2) | two));
    }
    /// Appends one byte to the end of the stack, top bits first
    /// @param one This bit is appended first(top)
    /// @param seven The first seven bits get appended(below) afterwards to make one byte
    public void push1x7(boolean one, byte seven) {
        seven = (byte) (seven & 0x7F);
        if(one)seven |= (byte) 128;
        push(seven);
    }
    /// Appends one byte to the end of the stack, top bits first
    /// @param seven The first 7 bits of this get appended first(top)
    /// @param one This bit gets appended after(below) to make a byte
    public void push7x1(byte seven, boolean one) {
        seven = (byte) ((seven & 0x3F) << 1);
        if(one)seven |= 1;
        push(seven);
    }
    /// Appends one byte to the end of the stack, top bits first
    /// @param three The first 3 bits of this get appended first(top)
    /// @param five The first five bits get appended(below) after to make a byte
    public void push3x5(byte three, byte five) {
        three = (byte) (three & 0x7);
        five = (byte) (five & 0x1F);
        push((byte) ((three << 5) | five));
    }
    /// Appends one byte to the end of the stack, top bits first
    /// @param five The first five bits of this get appended first(top)
    /// @param three The first three bits get appended after five(below) to make one byte
    public void push5x3(byte five, byte three) {
        three = (byte) (three & 0x7);
        five = (byte) (five & 0x1F);
        push((byte) ((five << 3) | three));
    }
    // // // Pop methods // // //
    /// Removes the byte from the top of the stack and returns it
    public byte pop() {
        if(size == 0)throw new NoSuchElementException("Cannot pop from an empty stack");
        byte out = top();
        size--;
        return out;
    }
    /// Removes 4 bytes from the top of the stack and returns an int
    public int pop4() {
        return ((pop() & 0xFF) << 24) |
                ((pop() & 0xFF) << 16) |
                ((pop() & 0xFF) << 8)  |
                (pop() & 0xFF);
    }
    /// Pops one byte from the stack
    /// @param out The first 2 bits get placed in out[0] and the last six in out[1]
    /// @throws ArrayIndexOutOfBoundsException If out cannot hold at least 2 elements
    public void pop2x6(byte[] out) {
        BitUtil.unpack2x6(pop(), out);
    }
    /// Pops one byte from the stack
    /// @param out The first six bits get placed in out[0] and the last two in out[1]
    /// @throws ArrayIndexOutOfBoundsException If out cannot hold at least 2 elements
    public void pop6x2(byte[] out) {
        BitUtil.unpack6x2(pop(), out);
    }
    /// Pops one byte from the stack
    /// @param out The first bit get placed in out[0] and the last seven in out[1]
    /// @throws ArrayIndexOutOfBoundsException If out cannot hold at least 2 elements
    public void pop1x7(byte[] out) {
        BitUtil.unpack1x7(pop(), out);
    }
    /// Pops one byte from the stack
    /// @param out The first seven bits get placed in out[0] and the last bit in out[1]
    /// @throws ArrayIndexOutOfBoundsException If out cannot hold at least 2 elements
    public void pop7x1(byte[] out) {
        BitUtil.unpack7x1(pop(), out);
    }
    /// Pops one byte from the stack
    /// @param out The first 3 bits get placed in out[0] and the last five in out[1]
    /// @throws ArrayIndexOutOfBoundsException If out cannot hold at least 2 elements
    public void pop3x5(byte[] out) {
        BitUtil.unpack3x5(pop(), out);
    }
    /// Pops one byte from the stack
    /// @param out The first five bits get placed in out[0] and the last three in out[1]
    /// @throws ArrayIndexOutOfBoundsException If out cannot hold at least 2 elements
    public void pop5x3(byte[] out) {
        BitUtil.unpack5x3(pop(), out);
    }
    /// @return A copy of the byte at the top of the stack
    public byte top() {
        if(size == 0)throw new NoSuchElementException("Cannot get the top element of an empty stack");
        return data[size-1];
    }
    // // // Util methods // // //
    public void clear() {
        data = new byte[10];
        size = 0;
        isFlipped = false;
    }
    /// This method flips the stack so that the bottom becomes the top and the top becomes the bottom.
    /// THIS DOES NOT MANGLE STORED NUMBER VALUES.
    /// THIS DOES NOT FLIP BYTES.
    /// It only makes the stack run left to right instead of right to left,
    /// <br>i.e. {b/10/20/top} becomes {top/10/20/b}
    public void flip() {
        byte[] f = new byte[data.length];
        for(int i = 1; i <= size; i++) {
            f[i-1] = data[size-i];
        }
        this.data = f;
        isFlipped = !isFlipped;
    }
    public ByteStack copy() {
        ByteStack b = new ByteStack(this.data.length);
        b.size = this.size;
        b.data = this.data;
        b.isFlipped = this.isFlipped;
        return b;
    }
    /// Removes excess occupied memory from the internal byte[]
    public void trim() {
        byte[] b = new byte[size];
        System.arraycopy(data, 0, b, 0, size);
        this.data = b;
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public boolean hasNext() {
        return size != 0;
    }
    public int size() {
        return size;
    }
    /// Returns the part of the array
    public byte[] getArray() {
        byte[] ret = new byte[size];
        System.arraycopy(data, 0, ret, 0, size);
        return ret;
    }
    /// Returns the complete backing array, faster than {@link ByteStack#getArray()}
    public byte[] getData() {
        return data;
    }

    // // // Helper methods // // //
    private void ensureCapacity(int minCapacity) {
        if(data.length >= minCapacity)return;
        int newCapacity = Math.max(0, Math.max(minCapacity, Math.min(40, (int)(data.length * 1.2) + 2)));
        byte[] buffer = new byte[newCapacity];
        System.arraycopy(data, 0, buffer, 0, Math.min(data.length, size));
        data = buffer;
    }
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(isFlipped ? "Stack(Flipped)[T|" : "Stack[B|");
        for(int i = 0; i < size; i++) {
            str.append(BitUtil.byteToString(data[i]));
            str.append('|');
        }
        str.append(isFlipped ? "B]" : "T]");
        return str.toString();
    }
    /// @return An empty ByteStack with an initial capacity of 10
    public static ByteStack empty() {
        return new ByteStack(10);
    }
    /// @return An empty ByteStack with an initial capacity of 50
    public static ByteStack large() {
        return new ByteStack(50);
    }
}
