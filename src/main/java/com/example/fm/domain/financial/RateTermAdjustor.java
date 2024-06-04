package com.example.fm.domain.financial;

import com.example.fm.domain.fmodel.Term;

import java.math.BigDecimal;

public class RateTermAdjustor {

    public static BigDecimal adjustByTerm(BigDecimal discountRate, Term term) {

        return switch (term) {
            case YEAR -> discountRate;
            case MONTH -> BigDecimal.valueOf(Math.pow(1 + discountRate.doubleValue(), 1.0/12) - 1);
            case DAY -> BigDecimal.valueOf(Math.pow(1 + discountRate.doubleValue(), 1.0/365) - 1);
        };
    }
}
