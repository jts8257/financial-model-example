package com.example.fm.domain.finder;

import com.example.fm.domain.account.AccountCalculator;
import com.example.fm.domain.account.factory.ConcreteAccountChainFactory;
import com.example.fm.domain.financial.irr.NewtonRaphsonIRRCalculator;
import com.example.fm.domain.financial.npv.StreamNPVCalculator;
import com.example.fm.domain.finder.vo.TargetValueAndIRR;
import com.example.fm.domain.fmodel.FinancialModel;
import com.example.fm.domain.fmodel.Term;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SaleFinderTest {
    private final BigDecimal targetIRR = BigDecimal.valueOf(0.07);
    private final BigDecimal FINDER_PRECISION = BigDecimal.valueOf(10000);
    private final BigDecimal PRECISION = new BigDecimal("1E-3");
    private AccountCalculator calculator;


    @Test
    public void yearCommonCase() {
        FinancialModel model = FinancialModel.SALE_MODEL_2;

        calculator = ConcreteAccountChainFactory.create(model, BigDecimal.valueOf(10000000), 5);

        ValueFinder finder = new SaleFinder(targetIRR, FINDER_PRECISION, 50,
                Term.YEAR, new StreamNPVCalculator(), new NewtonRaphsonIRRCalculator(), calculator);

        TargetValueAndIRR targetValueAndIRR = finder.find(BigDecimal.valueOf(10000000L));

        assertTrue(targetValueAndIRR.getIrr().subtract(targetIRR).abs().compareTo(PRECISION) <= 0);
    }

    @Test
    public void yearLongPeriod() {
        FinancialModel model = FinancialModel.SALE_MODEL_1;

        calculator = ConcreteAccountChainFactory.create(model, BigDecimal.valueOf(100000000), 100);

        ValueFinder finder = new SaleFinder(targetIRR, FINDER_PRECISION, 50,
                Term.YEAR, new StreamNPVCalculator(), new NewtonRaphsonIRRCalculator(), calculator);


        TargetValueAndIRR targetValueAndIRR = finder.find(BigDecimal.valueOf(12000000));


        assertTrue(targetValueAndIRR.getIrr().subtract(targetIRR).abs().compareTo(PRECISION) <=0);
    }

    @Test
    public void yearShortPeriod() {
        FinancialModel model = FinancialModel.SALE_MODEL_3;

        calculator = ConcreteAccountChainFactory.create(model, BigDecimal.valueOf(100000000), 1);

        ValueFinder finder = new SaleFinder(targetIRR, FINDER_PRECISION, 50,
                Term.YEAR, new StreamNPVCalculator(), new NewtonRaphsonIRRCalculator(), calculator);


        TargetValueAndIRR targetValueAndIRR = finder.find(BigDecimal.valueOf(10000000000L));

        assertTrue(targetValueAndIRR.getIrr().subtract(targetIRR).abs().compareTo(PRECISION) <=0);
    }

    @Test
    public void yearNegativeValue() {
        FinancialModel model = FinancialModel.SALE_MODEL_1;

        calculator = ConcreteAccountChainFactory.create(model, BigDecimal.valueOf(100000000), 5);

        ValueFinder finder = new SaleFinder(targetIRR, FINDER_PRECISION, 50,
                Term.YEAR, new StreamNPVCalculator(), new NewtonRaphsonIRRCalculator(), calculator);

        TargetValueAndIRR targetValueAndIRR = finder.find(BigDecimal.valueOf(-10000000000L));

        assertTrue(targetValueAndIRR.getIrr().subtract(targetIRR).abs().compareTo(PRECISION) <=0);
    }


    @Test
    public void monthCommonCase() {
        FinancialModel model = FinancialModel.SALE_MODEL_3;

        calculator = ConcreteAccountChainFactory.create(model, BigDecimal.valueOf(10000000), 36);

        ValueFinder finder = new SaleFinder(targetIRR, FINDER_PRECISION, 50,
                Term.MONTH, new StreamNPVCalculator(), new NewtonRaphsonIRRCalculator(), calculator);

        TargetValueAndIRR targetValueAndIRR = finder.find(BigDecimal.valueOf(10000000000L));

        assertTrue(targetValueAndIRR.getIrr().subtract(targetIRR).abs().compareTo(PRECISION) <=0);
    }

    @Test
    public void monthLongPeriod() {
        FinancialModel model = FinancialModel.SALE_MODEL_3;

        calculator = ConcreteAccountChainFactory.create(model, BigDecimal.valueOf(10000000), 360);

        ValueFinder finder = new SaleFinder(targetIRR, FINDER_PRECISION, 50,
                Term.MONTH, new StreamNPVCalculator(), new NewtonRaphsonIRRCalculator(), calculator);


        TargetValueAndIRR targetValueAndIRR = finder.find(BigDecimal.valueOf(100000000L));

        assertTrue(targetValueAndIRR.getIrr().subtract(targetIRR).abs().compareTo(PRECISION) <=0);
    }
}
