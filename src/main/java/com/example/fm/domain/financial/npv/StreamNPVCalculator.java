package com.example.fm.domain.financial.npv;

import com.example.fm.domain.fmodel.Term;
import com.example.fm.domain.financial.RateTermAdjustor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class StreamNPVCalculator implements NPVCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamNPVCalculator.class);
    @Override
    public BigDecimal calculateNPV(List<BigDecimal> cashFlow, BigDecimal discountRate, Term term) {
        BigDecimal adjustedDiscountRate = RateTermAdjustor.adjustByTerm(discountRate, term);

        if (cashFlow.stream().anyMatch(Objects::isNull)) {
            throw new RuntimeException("cash flow 값에 null 이 존재합니다.");
        }
        BigDecimal npv = IntStream.range(0, cashFlow.size())
                .mapToObj(t -> cashFlow.get(t)
                        .multiply(BigDecimal.ONE
                                .divide(adjustedDiscountRate.add(BigDecimal.ONE).pow(t),8, RoundingMode.DOWN)))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
//        LOGGER.info(String.format("calculateNPV(%s, %s) -> %s", cashFlow, discountRate, npv));

        return npv;
    }
}
