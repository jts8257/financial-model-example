package com.example.fm.domain.account.factory;

import com.example.fm.domain.account.AccountCalculator;
import com.example.fm.domain.account.factory.sub.PriceFindChainFactory;
import com.example.fm.domain.account.factory.sub.SaleFindChainFactory;
import com.example.fm.domain.fmodel.FinancialModel;

import java.math.BigDecimal;

public class ConcreteAccountChainFactory {

    private ConcreteAccountChainFactory() {}

    public static AccountCalculator create(FinancialModel financialModel, BigDecimal value, int period) {

        AccountChainFactory factory = financialModel.isPriceModel() ?
                PriceFindChainFactory.getInstance() :
                SaleFindChainFactory.getInstance();

        return factory.create(financialModel, value, period);
    }

}
