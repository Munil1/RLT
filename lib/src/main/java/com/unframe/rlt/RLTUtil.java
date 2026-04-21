package com.unframe.rlt;

import com.unframe.rlt.bin.ByteStack;

public final class RLTUtil {
    RLTUtil() {}
    public static String rltToJson(RLT rlt) {
        StringBuilder s = new StringBuilder();
        switch (rlt.getValue().getType()) {
            case ARRAY -> s.append(rlt.array().toString());
            case OBJECT -> s.append(RLTObject.toString(rlt.obj()));
            case EMPTY -> s.append("{}");
            case DBLOCK -> s.append(rlt.dblock().toString());
        }
        return s.toString();
    }
    public static RLTDBlock intDb(int n) {
        ByteStack b = new ByteStack(4);
        b.push((byte) ((n & 0xFF000000) >>> 24));
        b.push((byte) ((n & 0x00FF0000) >>> 16));
        b.push((byte) ((n & 0x0000FF00) >>> 8));
        b.push((byte) ((n & 0x000000FF)));
        return new RLTDBlock(b);
    }
}
