package com.example.fm.domain.calculator.financial;


import com.example.fm.domain.fmodel.Term;
import com.example.fm.domain.calculator.financial.helper.FinancialCalculatorTestHelper;
import com.example.fm.domain.calculator.financial.helper.PoiFinancialCalculator;
import com.example.fm.domain.calculator.financial.irr.IRRCalculator;
import com.example.fm.domain.calculator.financial.irr.NewtonRaphsonIRRCalculator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
public class NewtonRaphsonAndPoiCompareTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewtonRaphsonAndPoiCompareTest.class);
    private final IRRCalculator newtonRaphsonIRRCalculator = new NewtonRaphsonIRRCalculator();
    private final IRRCalculator poiIRRCalculator = new PoiFinancialCalculator();
    private static final BigDecimal PRECISION = new BigDecimal("1E-7");

    @Test
    public void 연단위_NR_방법과_POI_라이브러리_결과값에_큰_차이가_없다() {
        BigDecimal irr1 = newtonRaphsonIRRCalculator.calculateIRR(FinancialCalculatorTestHelper.cashFlowByYear(), Term.YEAR);
        BigDecimal irr2 = poiIRRCalculator.calculateIRR(FinancialCalculatorTestHelper.cashFlowByYear(), Term.YEAR);
        LOGGER.info("irr1 :" + irr1);
        LOGGER.info("irr2 :" + irr2);

        assertTrue(irr1.subtract(irr2).abs().compareTo(PRECISION) < 0);
    }

    @Test
    public void 월단위_POI_라이브러리는_값을_찾지_못한다() {
        BigDecimal irr1 = newtonRaphsonIRRCalculator.calculateIRR(FinancialCalculatorTestHelper.cashFlowByMonth(), Term.MONTH);
        BigDecimal irr2 = poiIRRCalculator.calculateIRR(FinancialCalculatorTestHelper.cashFlowByMonth(), Term.MONTH);

        LOGGER.info("irr1 :" + irr1);
        assertTrue(irr1.signum() != 0);
        assertNotNull(irr1);
        assertNull(irr2);
    }
}
