package com.example.fm.domain.financial.irr;

import com.example.fm.domain.fmodel.Term;

import java.math.BigDecimal;
import java.util.List;

public interface IRRCalculator {

    /**
     *
     * @param cashFlow 현금 흐름
     * @param term list 의 index 가 year, month, day 중 어떤것을 의미하는지
     * @return 주어진 현금흐름의 순 현재가치(Net Present Value) 를 0 이나 그에 근사하게 만드는 할인률을 반환한다.
     */
    BigDecimal calculateIRR(List<BigDecimal> cashFlow, Term term);


}
