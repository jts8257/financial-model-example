package com.example.fm.application.service;

import com.example.fm.application.dto.PriceCalculationRequest;
import com.example.fm.application.dto.SaleCalculationRequest;
import com.example.fm.domain.account.AccountCalculator;
import com.example.fm.domain.account.factory.ConcreteAccountChainFactory;
import com.example.fm.domain.finder.ValueFinder;
import com.example.fm.domain.finder.factory.ValueFinderFactory;
import com.example.fm.domain.fmodel.FinancialModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FinancialModelService {


    /**
     *
     * @param dto 적정 구매가격을 찾기 위해 필요한 정보, 사용할 금융모델과 고려기간, 발생 매출 값을 포함한다.
     * @return 모델에 따라 적정 구매 가격을 반환한다.
     */
    public BigDecimal findProperPurchasePriceBy(PriceCalculationRequest dto) {

        FinancialModel financialModel = dto.getFinancialModel();

        AccountCalculator calculatorChainHead =
                ConcreteAccountChainFactory.create(financialModel,dto.getSaleSource(), dto.getPeriod());

        ValueFinder finder = ValueFinderFactory.create(BigDecimal.valueOf(0.1), financialModel, calculatorChainHead);

        return finder.find(BigDecimal.valueOf(100000)).getValue();
    }


    /**
     *
     * @param dto 적정 매출을 찾기 위해 필요한 정보, 사용할 금융모델과 고려기간, 자산 구매 가격을 포함한다.
     * @return 모델에 따라 적정 매출을 반환한다.
     */
    public BigDecimal findProperSaleBy(SaleCalculationRequest dto) {

        FinancialModel financialModel = dto.getFinancialModel();

        AccountCalculator calculatorChainHead =
                ConcreteAccountChainFactory.create(financialModel,dto.getPurchasePrice(), dto.getPeriod());

        ValueFinder finder = ValueFinderFactory.create(BigDecimal.valueOf(0.1), financialModel, calculatorChainHead);

        return finder.find(BigDecimal.valueOf(1000000)).getValue();
    }

}
