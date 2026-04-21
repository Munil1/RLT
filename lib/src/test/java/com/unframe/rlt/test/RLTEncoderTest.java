package com.unframe.rlt.test;

import com.unframe.rlt.*;
import com.unframe.rlt.bin.ByteStack;

import java.util.*;

public class RLTEncoderTest {
    public static void main(String[] args) {
        //System.out.println(RLTUtil.rltToJson(new RandomRLT().next(20)));
        //fuzzTest(10000, 2, 5, 3452345234623462456L);
        long seed = 295601333128990239L;
        Random r = new Random(seed);
        RandomRLT rrlt = new RandomRLT(r);
        //mutateFuzz(rrlt.next(1 + r.nextInt(5), 5), seed, 10000);
        simpleGuidedFuzz(14345654635673562L, rrlt.next(1, 1));
    }
    public static void fuzzTest(int iterations, int depth, int budget, long seed) {
        Random r = new Random(seed);

        RandomRLT gen = new RandomRLT(r);
        RLTEncoder encoder = new RLTEncoder();
        RLTParser parser = new RLTParser();

        for (int i = 0; i < iterations; i++) {
            try {
                // Generate random structure
                RLT original = gen.next(depth + r.nextInt(5), budget);

                // Encode → Parse → Encode
                ByteStack encoded1 = encoder.encode(original);
                RLT parsed = parser.parse(encoded1);
                ByteStack encoded2 = encoder.encode(parsed);

                // Compare raw bytes
                if (!Arrays.equals(encoded1.getArray(), encoded2.getArray())) {
                    System.out.println("❌ MISMATCH at iteration " + i);
                    System.out.println("Seed: " + seed);

                    System.out.println("Original:");
                    System.out.println(RLTUtil.rltToJson(original));

                    System.out.println("Parsed:");
                    System.out.println(RLTUtil.rltToJson(parsed));

                    System.out.println("Encoded1: " + Arrays.toString(encoded1.getArray()));
                    System.out.println("Encoded2: " + Arrays.toString(encoded2.getArray()));

                    return;
                }

            } catch (Exception e) {
                System.out.println("💥 EXCEPTION at iteration " + i);
                System.out.println("Seed: " + seed);
                e.printStackTrace();
                return;
            }
        }

        System.out.println("✅ Fuzz passed: " + iterations + " iterations (seed=" + seed + ")");
    }
    public static void mutateFuzz(RLT seedRlt, long seed, int iterations) {
        Random r = new Random(seed);
        RLTEncoder enc = new RLTEncoder();
        RLTParser par = new RLTParser();

        byte[] base = enc.encode(seedRlt).getArray();

        for (int i = 0; i < iterations; i++) {
            // Random length: truncate OR extend
            int newLen = r.nextInt(base.length * 2 + 1);
            byte[] m = Arrays.copyOf(base, newLen);

            // Random bit flips
            int flips = 1 + r.nextInt(8);
            for (int j = 0; j < flips; j++) {
                if (m.length == 0) break;
                int idx = r.nextInt(m.length);
                m[idx] ^= (byte) (1 << r.nextInt(8));
            }

            try {
                par.parse(new ByteStack(m));
            } catch (RLTException.FileException e) {
                // ✅ expected: clean rejection
            } catch (Throwable t) {
                // ❌ unexpected failure → report immediately
                System.out.println("? CRASH at iteration " + i);
                System.out.println("Seed: " + seed);
                System.out.println("Input: " + Arrays.toString(m));
                t.printStackTrace();
                return;
            }
        }

        System.out.println("? mutateFuzz passed: " + iterations + " iterations (seed=" + seed + ")");
    }
    public static void simpleGuidedFuzz(long seed, RLT n) {
        Random r = new Random(seed);

        RLTEncoder en = new RLTEncoder();
        List<byte[]> corpus = new ArrayList<>();
        corpus.add(en.encode(n).getArray()); // initial seed

        Set<String> seen = new HashSet<>();

        for (int i = 0; i < 100000; i++) {
            byte[] base = corpus.get(r.nextInt(corpus.size()));
            byte[] mutated = mutate(base, r);

            String result;

            try {
                new RLTParser().parse(new ByteStack(mutated));
                result = "OK";
            } catch (Exception e) {
                result = e.getClass().getSimpleName() + ":" + e.getMessage();
                result = result.replaceAll("\\d+", "#");
                //result = e.getClass().getSimpleName();
            }

            if (seen.add(result)) {
                corpus.add(mutated);
                System.out.println("New behavior: " + result + " (corpus=" + corpus.size() + ")");
            }
        }
    }
    static byte[] mutate(byte[] in, Random r) {
        byte[] out = Arrays.copyOf(in, in.length);

        int ops = 1 + r.nextInt(8);

        for (int i = 0; i < ops; i++) {
            int type = r.nextInt(4);

            switch (type) {
                case 0 -> { // flip bit
                    int idx = r.nextInt(out.length);
                    out[idx] ^= (byte) (1 << r.nextInt(8));
                }
                case 1 -> { // insert
                    byte[] tmp = new byte[out.length + 1];
                    int pos = r.nextInt(tmp.length);
                    System.arraycopy(out, 0, tmp, 0, pos);
                    tmp[pos] = (byte) r.nextInt();
                    System.arraycopy(out, pos, tmp, pos + 1, out.length - pos);
                    out = tmp;
                }
                case 2 -> { // delete
                    if (out.length > 1) {
                        byte[] tmp = new byte[out.length - 1];
                        int pos = r.nextInt(out.length);
                        System.arraycopy(out, 0, tmp, 0, pos);
                        System.arraycopy(out, pos + 1, tmp, pos, out.length - pos - 1);
                        out = tmp;
                    }
                }
                case 3 -> { // overwrite
                    int idx = r.nextInt(out.length);
                    out[idx] = (byte) r.nextInt();
                }
            }
        }

        return out;
    }
}
