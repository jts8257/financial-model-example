package com.example.fm.domain.finder.factory;

import com.example.fm.domain.account.AccountCalculator;
import com.example.fm.domain.financial.irr.IRRCalculator;
import com.example.fm.domain.financial.irr.NewtonRaphsonIRRCalculator;
import com.example.fm.domain.financial.npv.NPVCalculator;
import com.example.fm.domain.financial.npv.StreamNPVCalculator;
import com.example.fm.domain.finder.PriceFinder;
import com.example.fm.domain.finder.SaleFinder;
import com.example.fm.domain.finder.ValueFinder;
import com.example.fm.domain.fmodel.FinancialModel;
import com.example.fm.domain.fmodel.ModelAssumption;
import com.example.fm.domain.fmodel.Term;

import java.math.BigDecimal;

public class ValueFinderFactory {
    private static final IRRCalculator irrCalculator = new NewtonRaphsonIRRCalculator();
    private static final NPVCalculator npvCalculator = new StreamNPVCalculator();
    private static final BigDecimal PRICE_FINDER_PRECISION = BigDecimal.valueOf(1000000);
    private static final BigDecimal SALE_FINDER_PRECISION = BigDecimal.valueOf(10000);
    private static final int MAX_FIND_ITERATION = 50;

    private ValueFinderFactory() {}

    public static ValueFinder create(BigDecimal targetIRR, FinancialModel financialModel, AccountCalculator calculator) {
        Term term = ModelAssumption.byModel(financialModel).getTerm();
        return financialModel.isPriceModel() ?
                new PriceFinder(targetIRR, PRICE_FINDER_PRECISION, MAX_FIND_ITERATION, term, npvCalculator, irrCalculator, calculator) :
                new SaleFinder(targetIRR, SALE_FINDER_PRECISION, MAX_FIND_ITERATION, term, npvCalculator, irrCalculator, calculator);
    }
}
