package com.example.fm.domain.account.item;


import com.example.fm.domain.account.AccountLayer;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PriceFindCostAccount extends AccountChainItem {
    private final int period;
    private final BigDecimal saleSource;
    private final BigDecimal inflationRate;

    public PriceFindCostAccount(int period, BigDecimal saleSource, BigDecimal inflationRate) {

        super(PriceFindCostAccount.class.getName(), AccountLayer.COST);
        this.period = period;
        this.saleSource = saleSource;
        this.inflationRate = inflationRate;
    }

    @Override
    public List<BigDecimal> calculate(BigDecimal value) {

        return IntStream.range(0, period)
                .mapToObj(t -> saleSource
                        .multiply(BigDecimal.ONE.add(inflationRate).pow(t))
                        .multiply(BigDecimal.valueOf(0.2)))
                .collect(Collectors.toList());
    }

}
