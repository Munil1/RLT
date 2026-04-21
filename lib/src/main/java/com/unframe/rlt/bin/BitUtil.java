package com.unframe.rlt.bin;

public final class BitUtil {
    BitUtil() {}
    public static boolean bitAtIndex(byte bite, int index) {
        return (((bite & 0xFF) >>> index) & 1) != 0;
    }
    public static byte flip(byte in) {
        int out = 0;
        if(((in) & 1) == 1)out |= 128;
        if(((in >>> 1) & 1) == 1)out |= 64;
        if(((in >>> 2) & 1) == 1)out |= 32;
        if(((in >>> 3) & 1) == 1)out |= 16;
        if(((in >>> 4) & 1) == 1)out |= 8;
        if(((in >>> 5) & 1) == 1)out |= 4;
        if(((in >>> 6) & 1) == 1)out |= 2;
        if(((in >>> 7) & 1) == 1)out |= 1;
        return (byte)out;
    }
    public static String byteToString(byte b) {
        char[] bits = new char[8];
        for (int i = 7; i >= 0; i--) {
            bits[7 - i] = ((b >> i) & 1) == 1 ? '1' : '0';
        }
        return new String(bits);
    }
    // // // Packing // // //
    /// @param two The first two bits of this get placed first(top)
    /// @param six The first six bits get placed(below) after two to make one byte
    public static byte pack2x6(byte two, byte six) {
        two = (byte) (two & 0x3);
        six = (byte) (six & 0x3F);
        return (byte) ((two << 6) | six);
    }
    /// @param six The first 6 bits of this get placed first(top)
    /// @param two The first two bits get placed(below) after to make a byte
    public static byte pack6x2(byte six, byte two) {
        two = (byte) (two & 0x3);
        six = (byte) (six & 0x3F);
        return (byte) ((six << 2) | two);
    }
    /// @param one This bit is placed first(top)
    /// @param seven The first seven bits get placed(below) afterwards to make one byte
    public static byte pack1x7(boolean one, byte seven) {
        seven = (byte) (seven & 0x7F);
        if(one)seven |= (byte) 128;
        return seven;
    }
    /// @param seven The first 7 bits of this get placed first(top)
    /// @param one This bit gets placed after(below) to make a byte
    public static byte pack7x1(byte seven, boolean one) {
        seven = (byte) ((seven & 0x3F) << 1);
        if(one)seven |= 1;
        return seven;
    }
    /// @param three The first 3 bits of this get placed first(top)
    /// @param five The first five bits get placed(below) after to make a byte
    public static byte pack3x5(byte three, byte five) {
        three = (byte) (three & 0x7);
        five = (byte) (five & 0x1F);
        return (byte) ((three << 5) | five);
    }
    /// @param five The first five bits of this get placed first(top)
    /// @param three The first three bits get placed after five(below) to make one byte
    public static byte pack5x3(byte five, byte three) {
        three = (byte) (three & 0x7);
        five = (byte) (five & 0x1F);
        return (byte) ((five << 3) | three);
    }
    // // // Unpacking // // //
    /// @param in The top 2 bits get placed in out[0] and the last six in out[1]
    /// @throws ArrayIndexOutOfBoundsException If out cannot hold at least 2 elements
    public static void unpack2x6(byte in, byte[] out) {
        out[0] = (byte) ((in & 0xC0) >>> 6);
        out[1] = (byte) (in & 0x3F);
    }
    /// @param in The top six bits get placed in out[0] and the last two in out[1]
    /// @throws ArrayIndexOutOfBoundsException If out cannot hold at least 2 elements
    public static void unpack6x2(byte in, byte[] out) {
        out[0] = (byte) ((in & 0xFC) >>> 2);
        out[1] = (byte) (in & 3);
    }
    /// @param in The top bit gets placed in out[0] and the last seven in out[1]
    /// @throws ArrayIndexOutOfBoundsException If out cannot hold at least 2 elements
    public static void unpack1x7(byte in, byte[] out) {
        out[0] = (byte) ((in & 0x80) >>> 7);
        out[1] = (byte) (in & 0x7F);
    }
    /// @param in The top seven bits get placed in out[0] and the last bit in out[1]
    /// @throws ArrayIndexOutOfBoundsException If out cannot hold at least 2 elements
    public static void unpack7x1(byte in, byte[] out) {
        out[0] = (byte) ((in & 0xFE) >>> 1);
        out[1] = (byte) (in & 1);
    }
    /// @param in The top 3 bits get placed in out[0] and the last five in out[1]
    /// @throws ArrayIndexOutOfBoundsException If out cannot hold at least 2 elements
    public static void unpack3x5(byte in, byte[] out) {
        out[0] = (byte) ((in & 0xE0) >>> 5);
        out[1] = (byte) (in & 0x1F);
    }
    /// @param in The top five bits get placed in out[0] and the last three in out[1]
    /// @throws ArrayIndexOutOfBoundsException If out cannot hold at least 2 elements
    public static void unpack5x3(byte in, byte[] out) {
        out[0] = (byte) ((in & 0xF8) >>> 3);
        out[1] = (byte) (in & 7);
    }
}
