package com.example.fm.domain.calculator.financial.helper;

import java.math.BigDecimal;
import java.util.List;

public class FinancialCalculatorTestHelper {

    public static List<BigDecimal> cashFlowByYear() {
        return List.of(
                BigDecimal.valueOf(-6000000000L), BigDecimal.valueOf(-300000000),
                BigDecimal.valueOf(180000000), BigDecimal.valueOf(220000000),
                BigDecimal.valueOf(250000000), BigDecimal.valueOf(8000000000L));
    }

    public static List<BigDecimal> cashFlowByMonth() {
        return List.of(
                BigDecimal.valueOf(-6000000000L),
                BigDecimal.valueOf(-25000000), BigDecimal.valueOf(-25000000), BigDecimal.valueOf(-25000000),
                BigDecimal.valueOf(-25000000), BigDecimal.valueOf(-25000000), BigDecimal.valueOf(-25000000),
                BigDecimal.valueOf(-25000000), BigDecimal.valueOf(-25000000), BigDecimal.valueOf(-25000000),
                BigDecimal.valueOf(-25000000), BigDecimal.valueOf(-25000000),
                BigDecimal.valueOf(15000000), BigDecimal.valueOf(15000000), BigDecimal.valueOf(15000000),
                BigDecimal.valueOf(15000000), BigDecimal.valueOf(15000000), BigDecimal.valueOf(15000000),
                BigDecimal.valueOf(15000000), BigDecimal.valueOf(15000000), BigDecimal.valueOf(15000000),
                BigDecimal.valueOf(15000000), BigDecimal.valueOf(15000000), BigDecimal.valueOf(15000000),
                BigDecimal.valueOf(18333333), BigDecimal.valueOf(18333333), BigDecimal.valueOf(18333333),
                BigDecimal.valueOf(18333333), BigDecimal.valueOf(18333333), BigDecimal.valueOf(18333333),
                BigDecimal.valueOf(18333333), BigDecimal.valueOf(18333333), BigDecimal.valueOf(18333333),
                BigDecimal.valueOf(18333333), BigDecimal.valueOf(18333333), BigDecimal.valueOf(18333333),
                BigDecimal.valueOf(20833333), BigDecimal.valueOf(20833333), BigDecimal.valueOf(20833333),
                BigDecimal.valueOf(20833333), BigDecimal.valueOf(20833333), BigDecimal.valueOf(20833333),
                BigDecimal.valueOf(20833333), BigDecimal.valueOf(20833333), BigDecimal.valueOf(20833333),
                BigDecimal.valueOf(20833333), BigDecimal.valueOf(20833333),
                BigDecimal.valueOf(8000000000L));
    }
}
