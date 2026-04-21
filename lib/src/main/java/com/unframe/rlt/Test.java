package com.unframe.rlt;

import java.io.File;
import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        RLTObject o = new RLTObject();
        RLTArray r = new RLTArray(RLTValueType.STRING);
        r.append(new RLTString("abc"));
        r.append(new RLTString("xyz"));
        r.append(new RLTString("mainsail.xyz"));
        o.addValue((byte)23, r);
        o.addValue((byte)12, new RLTString("294i + 41"));
        o.addValue((byte)80, RLTUtil.intDb(365));
        RLT rlt = new RLTv1(o, CodecID.ZERO);
        RLTEncoder en = new RLTEncoder();
        RLTParser p = new RLTParser();
        RLT crlt = p.parse(en.encode(rlt));
        System.out.println(RLTUtil.rltToJson(rlt));
        System.out.println(RLTUtil.rltToJson(crlt));
        System.out.println(en.encode(rlt));
        //File f = new File("C:/Users/Administrator/Desktop/v1.rlt");
        //f.createNewFile();
        //RLT.write(f, rlt);
        //RLT read = RLT.read(f);
        //System.out.println(RLTUtil.rltToJson(read));
    }
}
