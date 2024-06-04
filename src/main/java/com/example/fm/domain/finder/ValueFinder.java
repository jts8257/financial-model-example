package com.example.fm.domain.finder;

import com.example.fm.domain.finder.vo.TargetValueAndIRR;

import java.math.BigDecimal;

public interface ValueFinder {

    TargetValueAndIRR find(BigDecimal inputValue);
}
