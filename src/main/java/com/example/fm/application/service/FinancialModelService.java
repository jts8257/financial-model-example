package com.example.fm.application.service;

import com.example.fm.application.dto.CalculationRequest;
import com.example.fm.domain.calculator.account.AccountCalculatorChainFactory;
import com.example.fm.domain.calculator.account.calculator.AccountCalculator;
import com.example.fm.domain.finder.PurchasePriceFinder;
import com.example.fm.domain.fmodel.ModelAssumption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FinancialModelService {


    /**
     *
     * @param dto 적정 구매가격을 찾기 위해 필요한 정보, 사용할 금융모델과 고려기간, 발생 매출 값을 포함한다.
     * @return 모델에 따라 적정 구매 가격을 반환한다.
     */
    public BigDecimal findPurchasePriceBy(CalculationRequest dto) {

        ModelAssumption assumption = ModelAssumption.byModel(dto.getFinancialModel());

        AccountCalculator accountCalculator =
                AccountCalculatorChainFactory.creteChain(dto.getFinancialModel(), dto.getSaleSource(), dto.getPeriod());


        PurchasePriceFinder finder = new PurchasePriceFinder(
                assumption.getTerm(), accountCalculator, BigDecimal.valueOf(0.1));

        return finder.find(BigDecimal.valueOf(100000)).getPrice();
    }
}
