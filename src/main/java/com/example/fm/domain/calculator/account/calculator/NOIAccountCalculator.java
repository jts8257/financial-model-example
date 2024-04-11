package com.example.fm.domain.calculator.account.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class NOIAccountCalculator extends AccountCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(EbitCalculator.class);

    private final BigDecimal ltv;
    private final BigDecimal interestRate;
    private final BigDecimal taxRate;

    public NOIAccountCalculator(BigDecimal ltv, BigDecimal interestRate, BigDecimal taxRate) {
        this.ltv = ltv;
        this.interestRate = interestRate;
        this.taxRate = taxRate;
    }

    @Override
    public List<BigDecimal> calculate(BigDecimal purchasePrice) {

        if (isFirst()) {
            throw new IllegalStateException("NOI 계산을 위해서는 이전 현금흐름이 필요합니다.");
        }

        List<BigDecimal> prevCalculateResult = prev.calculate(purchasePrice);
        List<BigDecimal> npiList = new java.util.ArrayList<>(prevCalculateResult.stream().map(cash -> {
            if (cash == null) {
                return BigDecimal.ZERO;
            }
            return cash.subtract(purchasePrice.multiply(ltv).multiply(interestRate))
                    .subtract(purchasePrice.multiply(taxRate))
                    .setScale(0, RoundingMode.DOWN);
        }).toList());

        // 최초에 purchasePrice 만큼의 지출 발생
        npiList.add(0, purchasePrice.multiply(BigDecimal.valueOf(-1)));

        LOGGER.info(String.format("NOIAccountCalculator.calculate(%s) = %s", purchasePrice, npiList));
        return npiList;
    }

}
