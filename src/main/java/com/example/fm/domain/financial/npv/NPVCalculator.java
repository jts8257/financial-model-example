package com.example.fm.domain.financial.npv;

import com.example.fm.domain.fmodel.Term;

import java.math.BigDecimal;
import java.util.List;


public interface NPVCalculator {

    /**
     *
     * @param cashFlow 현금흐름
     * @param discountRate 현금흐름에 적용될 할인률
     * @param term list 의 index 가 year, month, day 중 어떤것을 의미하는지
     * @return 주어진 현근흐름을 할인률을 적용하여 순 현재가치 (Net Present Value) 로 만들어서 반환한다.
     */
    BigDecimal calculateNPV(List<BigDecimal> cashFlow, BigDecimal discountRate, Term term);

}
