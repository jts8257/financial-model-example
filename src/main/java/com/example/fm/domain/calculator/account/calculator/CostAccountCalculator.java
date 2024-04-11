package com.example.fm.domain.calculator.account.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CostAccountCalculator extends AccountCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(CostAccountCalculator.class);
    private final int period;
    private final BigDecimal saleSource;
    private final BigDecimal inflationRate;

    public CostAccountCalculator (int period, BigDecimal saleSource, BigDecimal inflationRate) {
        this.period = period;
        this.saleSource = saleSource;
        this.inflationRate = inflationRate;
    }

    @Override
    public List<BigDecimal> calculate(BigDecimal purchasePrice) {

        return IntStream.range(0, period)
                .mapToObj(t -> saleSource
                        .multiply(BigDecimal.ONE.add(inflationRate).pow(t))
                        .multiply(BigDecimal.valueOf(0.2)))
                .collect(Collectors.toList());
    }

}
