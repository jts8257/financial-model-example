package com.example.fm.domain.account.factory.sub;

import com.example.fm.domain.account.item.*;
import com.example.fm.domain.fmodel.FinancialModel;
import com.example.fm.domain.fmodel.ModelAssumption;

import java.math.BigDecimal;

public class PriceFindChainFactory extends ValidAccountChainFactory {
    private static volatile PriceFindChainFactory instance;

    // 더블 체킹 락(Double-Check Locking) 패턴을 적용한 싱글톤 패턴
    public static PriceFindChainFactory getInstance() {
        if (instance == null) {
            synchronized (PriceFindChainFactory.class) {
                if (instance == null) {
                    instance = new PriceFindChainFactory();
                }
            }
        }
        return instance;
    }

    private PriceFindChainFactory() {}

    @Override
    public AccountChainItem compose(FinancialModel financialModel, BigDecimal saleSource, int period) {

        ModelAssumption assumption = ModelAssumption.byModel(financialModel);
        assumption.setSaleSource(saleSource);
        assumption.setPeriod(period);

        return new PriceNOIAccount(assumption.getLtv(), assumption.getInterestRate(), assumption.getTaxRate())
                .setTail(new EbitAccount())
                .setTail(new PriceFindCostAccount(assumption.getPeriod(), assumption.getSaleSource(), assumption.getInflationRate()))
                .setTail(new PriceFindSaleAccount(assumption.getSaleSource(), assumption.getCagr(), assumption.getPeriod()));
    }
}
