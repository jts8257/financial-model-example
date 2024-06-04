package com.example.fm.domain.financial;

import com.example.fm.domain.fmodel.Term;
import com.example.fm.domain.financial.helper.FinancialCalculatorTestHelper;
import com.example.fm.domain.financial.irr.IRRCalculator;
import com.example.fm.domain.financial.irr.NewtonRaphsonIRRCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class NewtonRaphsonIRRCalculatorIRRTest {

    private final IRRCalculator calculator = new NewtonRaphsonIRRCalculator();

    @Test
    void 연단위_현금흐름_IRR_계산() {

        BigDecimal irr = calculator.calculateIRR(FinancialCalculatorTestHelper.cashFlowByYear(), Term.YEAR);
        assertNotNull(irr);
    }

    @Test
    void 월단위_현금흐름_IRR_계산() {

        BigDecimal irr = calculator.calculateIRR(FinancialCalculatorTestHelper.cashFlowByMonth(), Term.MONTH);
        assertNotNull(irr);
    }

}