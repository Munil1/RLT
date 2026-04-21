package com.unframe.rlt.bin;

import com.unframe.rlt.RLTDBlock;

/// A codec for converting between objects and data-blocks
public interface Codec<T> {
    T instantiate(RLTDBlock data);
    RLTDBlock encode(T inst);
}
