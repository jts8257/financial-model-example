package com.example.fm.domain.calculator.account;

import com.example.fm.domain.calculator.account.calculator.*;
import com.example.fm.domain.fmodel.FinancialModel;
import com.example.fm.domain.fmodel.ModelAssumption;

import java.math.BigDecimal;

public class AccountCalculatorChainFactory {

    // 객체 생성 불허
    private AccountCalculatorChainFactory () {}

    public static AccountCalculator creteChain(FinancialModel financialModel, BigDecimal saleSource, int period) {

        ModelAssumption assumption = ModelAssumption.byModel(financialModel);
        assumption.setSaleSource(saleSource);
        assumption.setPeriod(period);


        SaleAccountCalculator saleAccountCalculator =
                new SaleAccountCalculator(assumption.getSaleSource(), assumption.getCagr(), assumption.getPeriod());

        CostAccountCalculator costCalculator =
                new CostAccountCalculator(assumption.getPeriod(), assumption.getSaleSource(), assumption.getInflationRate());

        EbitCalculator ebitCalculator = new EbitCalculator();
        NOIAccountCalculator noiAccountCalculator = new NOIAccountCalculator(
                assumption.getLtv(), assumption.getInterestRate(), assumption.getTaxRate());

        saleAccountCalculator.setNext(costCalculator);
        costCalculator.setNext(ebitCalculator);
        ebitCalculator.setNext(noiAccountCalculator);

        return noiAccountCalculator;
    }
}
