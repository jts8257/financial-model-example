package com.example.fm.domain.account.factory.sub;

import com.example.fm.domain.account.AccountCalculator;
import com.example.fm.domain.account.factory.AccountChainFactory;
import com.example.fm.domain.account.item.AccountChainItem;
import com.example.fm.domain.fmodel.FinancialModel;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

abstract public class ValidAccountChainFactory implements AccountChainFactory {
    @Override
    public AccountCalculator create(FinancialModel financialModel, BigDecimal value, int period) {
        if (financialModel == null) {
            throw new IllegalArgumentException("계정 계산식을 만들기 위한 매개변수가 누락되었습니다.");
        }
        return AccountItemChainValidator.validateChain(compose(financialModel, value, period));

    }

    /**
     *
     * @param financialModel 적용 재무모델 정보
     * @param value input 이 되는 value
     * @param period 고려 기간
     * @return 위의 사항을 종합하여 적합한 재무 계정 계산기 체인을 반환
     */
    abstract public AccountChainItem compose(FinancialModel financialModel, BigDecimal value, int period);

}
