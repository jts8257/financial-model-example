package com.example.fm.domain.calculator.account.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

public class EbitCalculator extends AccountCalculator {

    @Override
    public List<BigDecimal> calculate(BigDecimal purchasePrice) {

        if (isFirst()) {
            throw exception;
        }

        AccountCalculator costCalculator = getPrev();
        if (costCalculator.isFirst()) {
            throw exception;
        }

        AccountCalculator saleCalculator = costCalculator.getPrev();

        if (!(costCalculator instanceof CostAccountCalculator) ||
                !(saleCalculator instanceof SaleAccountCalculator)) {

            throw exception;
        }

        List<BigDecimal> sales = saleCalculator.calculate(purchasePrice);
        List<BigDecimal> costs = costCalculator.calculate(purchasePrice);


        return IntStream.range(0, sales.size())
                .mapToObj(t -> {
                    try {
                        return sales.get(t).subtract(costs.get(t));
                    } catch (IndexOutOfBoundsException exp) {
                        throw new IllegalStateException("EBIT 계산에 활용되는 sale, cost 발생 기간이 일치하지 않습니다. ");
                    }
                })
                .toList();
    }

    private final RuntimeException exception =
            new IllegalArgumentException( "EBIT 계산을 위해서는 cost, sale 의 계산이 직전에 선행되어야 합니다.");
}
