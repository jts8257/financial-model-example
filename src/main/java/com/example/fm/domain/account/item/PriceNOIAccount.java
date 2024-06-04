package com.example.fm.domain.account.item;


import com.example.fm.domain.account.AccountLayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PriceNOIAccount extends AccountChainItem {
    private final BigDecimal ltv;
    private final BigDecimal interestRate;
    private final BigDecimal taxRate;

    public PriceNOIAccount(BigDecimal ltv, BigDecimal interestRate, BigDecimal taxRate) {
        super(PriceNOIAccount.class.getName(), AccountLayer.NOI);

        this.ltv = ltv;
        this.interestRate = interestRate;
        this.taxRate = taxRate;
    }

    @Override
    public List<BigDecimal> calculate(BigDecimal value) {

        List<BigDecimal> prevCalculateResult = predecessor.calculate(value);
        List<BigDecimal> npiList = new java.util.ArrayList<>(prevCalculateResult.stream().map(cash -> {
            if (cash == null) {
                return BigDecimal.ZERO;
            }
            return cash.subtract(value.multiply(ltv).multiply(interestRate))
                    .subtract(value.multiply(taxRate))
                    .setScale(0, RoundingMode.DOWN);
        }).toList());

        // 최초에 purchasePrice 만큼의 지출 발생
        npiList.add(0, value.multiply(BigDecimal.valueOf(-1)));

        return npiList;
    }

}
