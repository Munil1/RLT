package com.unframe.rlt.test;

import com.unframe.rlt.*;
import com.unframe.rlt.bin.ByteStack;

import java.util.*;

public class RandomRLT {
    private final Random r;

    // Tunables
    private static final int MAX_STRING = 0x3FFF;
    private static final int MAX_DBLOCK = 256; // keep reasonable for speed

    public RandomRLT(Random r) {
        this.r = r;
    }

    public RLT next(int maxDepth, int budget) {
        RLTValue root = randomRoot(maxDepth, budget);
        return new RLTv1(root, randomCodec());
    }

    // --- ROOT (no STRING allowed)
    private RLTValue randomRoot(int depth, int budget) {
        int t = r.nextInt(3); // OBJECT / ARRAY / DBLOCK
        return switch (t) {
            case 0 -> randomObject(depth, budget);
            case 1 -> randomArray(depth, budget);
            default -> randomDBlock();
        };
    }

    // --- VALUE (all types allowed)
    private RLTValue randomValue(int depth, int budget) {
        if (budget <= 0 || depth <= 0) {
            return randomLeaf();
        }

        int roll = r.nextInt(100);

        if (roll < 30) return randomObject(depth - 1, budget - 1);
        if (roll < 60) return randomArray(depth - 1, budget - 1);
        if (roll < 80) return randomDBlock();
        return randomString();
    }

    // --- LEAF (bias edge cases)
    private RLTValue randomLeaf() {
        int t = r.nextInt(3);
        return switch (t) {
            case 0 -> randomDBlock();
            case 1 -> randomString();
            default -> new RLTArray(RLTValueType.STRING); // empty array edge
        };
    }

    // --- OBJECT
    private RLTObject randomObject(int depth, int budget) {
        RLTObject obj = new RLTObject();

        int size = biasedSize(0, 8); // small but varied

        for (int i = 0; i < size && budget > 0; i++) {
            byte key = (byte) r.nextInt(256);

            // occasional duplicate overwrite (edge case)
            if (r.nextInt(10) == 0 && obj.getKeys().contains(key)) continue;

            obj.addValue(key, randomValue(depth - 1, budget - 1));
            budget--;
        }

        return obj;
    }

    // --- ARRAY
    private RLTArray randomArray(int depth, int budget) {
        RLTValueType subtype = RLTValueType.forRepresentation(r.nextInt(4));
        RLTArray arr = new RLTArray(subtype);

        int size = biasedSize(0, 8);

        for (int i = 0; i < size && budget > 0; i++) {
            arr.append(randomValueOfType(subtype, depth - 1, budget - 1));
            budget--;
        }

        return arr;
    }
    private RLTValue randomValueOfType(RLTValueType type, int depth, int budget) {
        if (budget <= 0 || depth <= 0) {
            return randomLeafOfType(type);
        }

        return switch (type) {
            case OBJECT -> randomObject(depth, budget);
            case ARRAY  -> randomArray(depth, budget);
            case DBLOCK -> randomDBlock();
            case STRING -> randomString();
            default -> throw new IllegalStateException("Unknown type");
        };
    }
    private RLTValue randomLeafOfType(RLTValueType type) {
        return switch (type) {
            case OBJECT -> new RLTObject(); // empty
            case ARRAY  -> new RLTArray(RLTValueType.STRING); // empty safe fallback
            case DBLOCK -> randomDBlock();
            case STRING -> randomString();
            default -> throw new IllegalStateException("Unknown type");
        };
    }

    // --- STRING (with edge cases + escaping stress)
    private RLTString randomString() {
        int len;

        int roll = r.nextInt(100);
        if (roll < 10) len = 0; // empty
        else if (roll < 20) len = 1;
        else if (roll < 25) len = MAX_STRING; // max edge
        else len = r.nextInt(32);

        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            int cRoll = r.nextInt(100);

            char c;
            if (cRoll < 10) c = '"';        // force escape
            else if (cRoll < 20) c = '\\'; // force escape
            else if (cRoll < 25) c = '\n';
            else c = (char) (32 + r.nextInt(95));

            sb.append(c);
        }

        return new RLTString(sb.toString());
    }

    // --- DBLOCK (edge sizes)
    private RLTDBlock randomDBlock() {
        int len;

        int roll = r.nextInt(100);
        if (roll < 10) len = 0;
        else if (roll < 20) len = 1;
        else if (roll < 25) len = 0x3FFF; // max allowed
        else len = r.nextInt(MAX_DBLOCK);

        ByteStack b = new ByteStack(len);

        for (int i = 0; i < len; i++) {
            b.push((byte) r.nextInt(256));
        }

        b.flip();
        return new RLTDBlock(b);
    }

    // --- SIZE BIAS
    private int biasedSize(int min, int max) {
        int roll = r.nextInt(100);

        if (roll < 30) return 0;     // empty
        if (roll < 50) return 1;     // tiny
        if (roll < 60) return max;   // max edge

        return min + r.nextInt(max - min + 1);
    }

    // --- CODEC
    private CodecID randomCodec() {
        return new CodecID(
                r.nextInt(), r.nextInt(), r.nextInt(), r.nextInt(),
                r.nextInt(), r.nextInt(), r.nextInt(), r.nextInt()
        );
    }
}
