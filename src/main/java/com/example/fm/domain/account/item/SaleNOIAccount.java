package com.example.fm.domain.account.item;


import com.example.fm.domain.account.AccountLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


public class SaleNOIAccount extends AccountChainItem {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaleNOIAccount.class);

    private final BigDecimal purchasePrice;
    private final BigDecimal noiSubtract;

    public SaleNOIAccount(BigDecimal ltv, BigDecimal interestRate, BigDecimal taxRate, BigDecimal purchasePrice) {
        super(SaleNOIAccount.class.getName(), AccountLayer.NOI);

        this.purchasePrice = purchasePrice;
        this.noiSubtract = purchasePrice.multiply(ltv).multiply(interestRate)
                        .add(purchasePrice).multiply(taxRate)
                        .setScale(0, RoundingMode.DOWN);
    }

    @Override
    public List<BigDecimal> calculate(BigDecimal value) {

        List<BigDecimal> prevCalculateResult = predecessor.calculate(value);

        List<BigDecimal> noiList = new java.util.ArrayList<>(prevCalculateResult.stream()
                .map(cash -> cash == null ? BigDecimal.ZERO : cash.subtract(noiSubtract).setScale(2, RoundingMode.DOWN)).toList());

        noiList.add(0, purchasePrice.multiply(BigDecimal.valueOf(-1)));

        return noiList;
    }

}
