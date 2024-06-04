package com.example.fm.domain.account.item;

import com.example.fm.domain.account.AccountLayer;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

public class EbitAccount extends AccountChainItem {
    public EbitAccount() {
        super(EbitAccount.class.getName(), AccountLayer.EBIT);
    }

    @Override
    public List<BigDecimal> calculate(BigDecimal value) {

        AccountChainItem costCalculator = getPredecessor();
        AccountChainItem saleCalculator = costCalculator.getPredecessor();

        List<BigDecimal> sales = saleCalculator.calculate(value);
        List<BigDecimal> costs = costCalculator.calculate(value);

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
}
