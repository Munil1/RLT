package com.unframe.rlt;

import java.util.HexFormat;

/// A 32-byte unsigned id for rlt file headers
public class CodecID {
    private final int[] data = new int[8];
    public static final CodecID ZERO = new CodecID(0, 0, 0, 0, 0, 0, 0, 0);
    public CodecID(int a, int b, int c, int d, int e, int f, int g, int h) {
        data[0] = a;
        data[1] = b;
        data[2] = c;
        data[3] = d;
        data[4] = e;
        data[5] = f;
        data[6] = g;
        data[7] = h;
    }
    public int[] getData() {
        return data;
    }
    @Override
    public String toString() {
        HexFormat hf = HexFormat.of().withUpperCase();
        StringBuilder sb = new StringBuilder();
        for (int val : data) {
            // Converts each 32-bit int to 8 hex characters
            sb.append(hf.toHexDigits(val));
        }
        return sb.toString();
    }
}
