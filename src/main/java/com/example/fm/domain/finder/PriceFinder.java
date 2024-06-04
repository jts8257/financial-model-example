package com.example.fm.domain.finder;

import com.example.fm.domain.account.AccountCalculator;
import com.example.fm.domain.financial.irr.IRRCalculator;
import com.example.fm.domain.financial.npv.NPVCalculator;
import com.example.fm.domain.fmodel.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PriceFinder extends BinarySearchValueFinderTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(PriceFinder.class);
    private final Term term;
    private final NPVCalculator npvCalculator;
    private final IRRCalculator irrCalculator;
    private final AccountCalculator accountCalculator;

    public PriceFinder(
            BigDecimal targetIRR, BigDecimal PRECISION, int MAX_ITERATIONS,
            Term term, NPVCalculator npvCalculator, IRRCalculator irrCalculator,  AccountCalculator accountCalculator) {

        super(targetIRR, PRECISION, MAX_ITERATIONS);


        this.term = term;
        this.accountCalculator = accountCalculator;
        this.irrCalculator = irrCalculator;
        this.npvCalculator = npvCalculator;
    }


    @Override
    protected BigDecimal convertInitialInput(BigDecimal initialInput) {
        if (initialInput == null || initialInput.signum() <= 0) {
            initialInput = BigDecimal.valueOf(1000000000L);
        }
        return initialInput;
    }

    @Override
    protected List<BigDecimal> initializeRange(BigDecimal inputValue) {
        BigDecimal npv = npvCalculator.calculateNPV(accountCalculator.calculate(inputValue), targetIRR, term);
        BigDecimal minPrice = BigDecimal.ZERO;
        BigDecimal maxPrice = inputValue;


        int searchCount = 0;
        final int maxSearchCount = 30;
        while (npv.signum() > 0) {
            minPrice = maxPrice;
            maxPrice = maxPrice.multiply(BigDecimal.valueOf(10));
            npv = npvCalculator.calculateNPV(accountCalculator.calculate(maxPrice), targetIRR, term);

            if (++searchCount > maxSearchCount) {
                throw new IllegalStateException("주어진 조건으로 적정 구매가격 탐색을 할 수 없습니다.");
            }
        }


        searchCount = 0;
        if (minPrice.signum() == 0) {
            minPrice = inputValue.divide(BigDecimal.valueOf(10), RoundingMode.DOWN);
            npv = npvCalculator.calculateNPV(accountCalculator.calculate(minPrice), targetIRR, term);

            while (npv.signum() < 0) {
                minPrice = minPrice.divide(BigDecimal.valueOf(10), RoundingMode.DOWN);
                npv = npvCalculator.calculateNPV(accountCalculator.calculate(minPrice), targetIRR, term);

                if (++searchCount > maxSearchCount) {
                    throw new IllegalStateException("주어진 조건으로 적정 구매가격 탐색을 할 수 없습니다.");
                }
            }
            maxPrice = minPrice.multiply(BigDecimal.valueOf(10));

        }

        return List.of(minPrice, maxPrice);
    }

    @Override
    protected BigDecimal calculateOutputBy(BigDecimal input) {
        return npvCalculator
                .calculateNPV(accountCalculator.calculate(input), targetIRR, term)
                .setScale(2, RoundingMode.DOWN);
    }

    @Override
    protected BigDecimal calculateIRRBy(BigDecimal input) {
        return irrCalculator.calculateIRR(accountCalculator.calculate(input), term);
    }

    @Override
    protected List<BigDecimal> adjustRangeBy(BigDecimal output, List<BigDecimal> range) {
        BigDecimal input = calculateMidFrom(range);
        if (output.signum() > 0) {
            BigDecimal max = range.stream().max(BigDecimal::compareTo).orElseThrow(() -> new IllegalArgumentException(""));
            return List.of(input, max); // 현재가치가 양수면 투하자본을 늘려야한다.
        } else {
            BigDecimal min = range.stream().min(BigDecimal::compareTo).orElseThrow(() -> new IllegalArgumentException(""));
            return List.of(min, input); // 현재가치가 음수면 투하자본을 줄여야한다.
        }
    }
}
