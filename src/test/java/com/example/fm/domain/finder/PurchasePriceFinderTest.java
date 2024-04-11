package com.example.fm.domain.finder;

import com.example.fm.domain.calculator.account.AccountCalculatorChainFactory;
import com.example.fm.domain.calculator.account.calculator.AccountCalculator;
import com.example.fm.domain.finder.vo.PriceAndIRR;
import com.example.fm.domain.fmodel.FinancialModel;
import com.example.fm.domain.fmodel.Term;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PurchasePriceFinderTest {
    private final BigDecimal targetIRR = BigDecimal.valueOf(0.07);
    private final BigDecimal PRECISION = new BigDecimal("1E-3");
    private AccountCalculator accountCalculator;

    @Test
    public void yearCommonCase() {

        accountCalculator = AccountCalculatorChainFactory.creteChain(
                FinancialModel.MODEL_2, BigDecimal.valueOf(100000000), 5);

        PurchasePriceFinder finder = new PurchasePriceFinder(Term.YEAR, accountCalculator, targetIRR);

        PriceAndIRR priceAndIRR = finder.find(BigDecimal.valueOf(10000000000L));


        assertTrue(priceAndIRR.getIrr().subtract(targetIRR).abs().compareTo(PRECISION) <= 0);
    }

    @Test
    public void yearLongPeriod() {

        accountCalculator = AccountCalculatorChainFactory.creteChain(
                FinancialModel.MODEL_1, BigDecimal.valueOf(100000000), 100);

        PurchasePriceFinder finder = new PurchasePriceFinder(Term.YEAR, accountCalculator, targetIRR);

        PriceAndIRR priceAndIRR = finder.find(BigDecimal.valueOf(10000000000L));


        assertTrue(priceAndIRR.getIrr().subtract(targetIRR).abs().compareTo(PRECISION) <=0);
    }

    @Test
    public void yearShortPeriod() {

        accountCalculator = AccountCalculatorChainFactory.creteChain(
                FinancialModel.MODEL_3, BigDecimal.valueOf(100000000), 1);

        PurchasePriceFinder finder = new PurchasePriceFinder(Term.YEAR, accountCalculator, targetIRR);

        PriceAndIRR priceAndIRR = finder.find(BigDecimal.valueOf(10000000000L));

        assertTrue(priceAndIRR.getIrr().subtract(targetIRR).abs().compareTo(PRECISION) <=0);
    }

    @Test
    public void yearNegativePrice() {

        accountCalculator = AccountCalculatorChainFactory.creteChain(
                FinancialModel.MODEL_1, BigDecimal.valueOf(100000000), 5);

        PurchasePriceFinder finder = new PurchasePriceFinder(Term.YEAR, accountCalculator, targetIRR);

        PriceAndIRR priceAndIRR = finder.find(BigDecimal.valueOf(-10000000000L));

        assertTrue(priceAndIRR.getIrr().subtract(targetIRR).abs().compareTo(PRECISION) <=0);
    }


    @Test
    public void monthCommonCase() {

        accountCalculator = AccountCalculatorChainFactory.creteChain(
                FinancialModel.MODEL_3, BigDecimal.valueOf(10000000), 36);

        PurchasePriceFinder finder = new PurchasePriceFinder(Term.MONTH, accountCalculator, targetIRR);

        PriceAndIRR priceAndIRR = finder.find(BigDecimal.valueOf(10000000000L));

        assertTrue(priceAndIRR.getIrr().subtract(targetIRR).abs().compareTo(PRECISION) <=0);
    }

    @Test
    public void monthLongPeriod() {

        accountCalculator = AccountCalculatorChainFactory.creteChain(
                FinancialModel.MODEL_3, BigDecimal.valueOf(10000000), 360); // 30년

        PurchasePriceFinder finder = new PurchasePriceFinder(Term.MONTH, accountCalculator, targetIRR);

        PriceAndIRR priceAndIRR = finder.find(BigDecimal.valueOf(10000000000L));

        assertTrue(priceAndIRR.getIrr().subtract(targetIRR).abs().compareTo(PRECISION) <=0);
    }
}