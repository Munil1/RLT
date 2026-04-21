package com.unframe.rlt.bin;

public class Bit4 {
    private static final Bit4[] CACHE = new Bit4[] {
            new Bit4(0), new Bit4(1), new Bit4(2), new Bit4(3),
            new Bit4(4), new Bit4(5), new Bit4(6), new Bit4(7),
            new Bit4(8), new Bit4(9), new Bit4(10), new Bit4(11),
            new Bit4(12), new Bit4(13), new Bit4(14), new Bit4(15)
    };
    private final byte value;
    Bit4(int value) {
        this.value = (byte) (value & 0xF);
    }
    public byte value() {
        return value;
    }
    public static Bit4 of(int v) {
        return CACHE[v & 0xF];
    }
}
