package com.example.fm.domain.account.item;


import com.example.fm.domain.account.AccountLayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PriceFindSaleAccount extends AccountChainItem {

    private final BigDecimal saleSource;
    private final BigDecimal cagr;
    private final int period;
    public PriceFindSaleAccount(BigDecimal saleSource, BigDecimal cagr, int period) {
        super(PriceFindSaleAccount.class.getName(), AccountLayer.SALE);
        this.saleSource = saleSource;
        this.cagr = cagr;
        this.period = period;
    }

    @Override
    public List<BigDecimal> calculate(BigDecimal value) {

        return IntStream.range(0, period)
                .mapToObj(t -> saleSource
                        .multiply(BigDecimal.ONE.add(cagr).pow(t)).setScale(0, RoundingMode.DOWN))
                .collect(Collectors.toList());
    }

}
