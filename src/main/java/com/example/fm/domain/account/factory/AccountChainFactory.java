package com.example.fm.domain.account.factory;

import com.example.fm.domain.account.AccountCalculator;
import com.example.fm.domain.fmodel.FinancialModel;

import java.math.BigDecimal;

public interface AccountChainFactory {

    /**
     *
     * @param financialModel 적용 재무모델 정보
     * @param value input 이 되는 value
     * @param period 고려 기간
     * @return 위의 사항을 종합하여 적합한 재무 계정 계산기를 반환
     */
    AccountCalculator create(FinancialModel financialModel, BigDecimal value, int period);

}
