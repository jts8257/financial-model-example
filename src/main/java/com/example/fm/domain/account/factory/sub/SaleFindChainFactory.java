package com.example.fm.domain.account.factory.sub;

import com.example.fm.domain.account.item.*;
import com.example.fm.domain.fmodel.FinancialModel;
import com.example.fm.domain.fmodel.ModelAssumption;

import java.math.BigDecimal;

public class SaleFindChainFactory extends ValidAccountChainFactory {
    private static volatile SaleFindChainFactory instance;

    public static SaleFindChainFactory getInstance() {
        if (instance == null) {
            synchronized (SaleFindChainFactory.class) {
                if (instance == null) {
                    instance = new SaleFindChainFactory();
                }
            }
        }
        return instance;
    }

    private SaleFindChainFactory() {}

    @Override
    public AccountChainItem compose(FinancialModel financialModel, BigDecimal purchasePrice, int period) {
        ModelAssumption assumption = ModelAssumption.byModel(financialModel);
        assumption.setPeriod(period);

        return new SaleNOIAccount(assumption.getLtv(), assumption.getInterestRate(), assumption.getTaxRate(), purchasePrice)
                .setTail(new EbitAccount())
                .setTail(new SaleFindCostAccount(assumption.getPeriod(), assumption.getInflationRate()))
                .setTail(new SaleFindSaleAccount(assumption.getCagr(),assumption.getPeriod()));
    }
}
