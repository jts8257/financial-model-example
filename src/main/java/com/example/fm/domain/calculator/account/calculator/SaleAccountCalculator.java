package com.example.fm.domain.calculator.account.calculator;

import com.example.fm.domain.calculator.financial.irr.NewtonRaphsonIRRCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SaleAccountCalculator extends AccountCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaleAccountCalculator.class);

    private final BigDecimal saleSource;
    private final BigDecimal cagr;
    private final int period;
    public SaleAccountCalculator (BigDecimal saleSource, BigDecimal cagr, int period) {
        this.saleSource = saleSource;
        this.cagr = cagr;
        this.period = period;
    }

    @Override
    public List<BigDecimal> calculate(BigDecimal purchasePrice) {

        return IntStream.range(0, period)
                .mapToObj(t -> saleSource
                        .multiply(BigDecimal.ONE.add(cagr).pow(t)).setScale(0, RoundingMode.DOWN))
                .collect(Collectors.toList());
    }

}
