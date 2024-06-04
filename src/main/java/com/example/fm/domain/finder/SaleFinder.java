package com.example.fm.domain.finder;

import com.example.fm.domain.account.AccountCalculator;
import com.example.fm.domain.financial.irr.IRRCalculator;
import com.example.fm.domain.financial.npv.NPVCalculator;
import com.example.fm.domain.finder.vo.TargetValueAndIRR;
import com.example.fm.domain.fmodel.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class SaleFinder extends BinarySearchValueFinderTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaleFinder.class);
    private final Term term;
    private final NPVCalculator npvCalculator;
    private final IRRCalculator irrCalculator;
    private final AccountCalculator accountCalculator;

    public SaleFinder(
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
            initialInput = BigDecimal.valueOf(1000000L);
        }
        return initialInput;
    }

    @Override
    protected List<BigDecimal> initializeRange(BigDecimal input) {

        BigDecimal npv = calculateOutputBy(input);

        BigDecimal minPrice = BigDecimal.ZERO;
        BigDecimal maxPrice = input;

        int searchCount = 0;
        final int maxSearchCount = 100;
        while (npv.signum() < 0) {
            minPrice = maxPrice;
            maxPrice = maxPrice.multiply(BigDecimal.valueOf(10));
            npv = calculateOutputBy(maxPrice);

            if (++searchCount > maxSearchCount) {
                throw new IllegalStateException("주어진 조건으로 적정 매출을 탐색을 할 수 없습니다.");
            }
        }


        searchCount = 0;
        if (minPrice.signum() == 0) {
            minPrice = input.divide(BigDecimal.valueOf(100), RoundingMode.DOWN);
            npv = calculateOutputBy(minPrice);

            while (npv.signum() > 0) {
                minPrice = minPrice.divide(BigDecimal.valueOf(100), RoundingMode.DOWN);
                npv = calculateOutputBy(minPrice);

                if (++searchCount > maxSearchCount) {
                    throw new IllegalStateException("주어진 조건으로 적정 매출을 탐색을 할 수 없습니다.");
                }
            }
            maxPrice = minPrice.multiply(BigDecimal.valueOf(100));

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
            BigDecimal min = range.stream().min(BigDecimal::compareTo).orElseThrow(() -> new IllegalArgumentException(""));
            return List.of(min, input); // 현재가치가 양수면 발생 매출을 줄여야한다.
        } else {

            BigDecimal max = range.stream().max(BigDecimal::compareTo).orElseThrow(() -> new IllegalArgumentException(""));
            return List.of(input, max); // 현재가치가 음수면 발생 매출을 늘려야한다.
        }
    }

    @Override
    public TargetValueAndIRR find(BigDecimal inputSale) {

        if (inputSale == null || inputSale.signum() <= 0) {
            LOGGER.warn("적정가격을 찾는 메서드에 최초 가격값이 비정상적으로 입력되었습니다. 10억으로 변환하여 계산합니다.");
            inputSale = BigDecimal.valueOf(100000L);
        }

        // 관련 산업에 따라 허용 오차 정도가 달라진다.
        BigDecimal NPV_PRECISION = BigDecimal.valueOf(1000);

        List<BigDecimal> saleRange = determineSearchRange(inputSale);
        BigDecimal minSale = saleRange.get(0);
        BigDecimal maxSale = saleRange.get(1);

        BigDecimal currentSale = adjustCurrentPrice(minSale, maxSale);

        int iterationCount = 0;
        int MAX_ITERATIONS = 50;
        while (iterationCount < MAX_ITERATIONS) {
            BigDecimal outputNPV = npvCalculator.calculateNPV(accountCalculator.calculate(currentSale), targetIRR, term).setScale(0, RoundingMode.DOWN);

            if (outputNPV.abs().compareTo(NPV_PRECISION) < 0) { // 정밀도 수준을 만족하는 가격이면 반환한다.
                BigDecimal irr = irrCalculator.calculateIRR(accountCalculator.calculate(currentSale), term);
                LOGGER.info(String.format("TargetValueAndIRR FOUND :: from %s -> {value : %s, irr : %f, npv : %s } total iteration : %d",
                        inputSale, currentSale, irr, outputNPV, iterationCount));

                return new TargetValueAndIRR(currentSale, irr);
            }


            if (outputNPV.signum() < 0) {
                minSale = currentSale; // 현재가치가 음수면 발생 매출을 늘려야한다.
            } else {
                maxSale = currentSale; // 현재가치가 양수면 발생 매출을 줄여야한다.
            }

            currentSale = adjustCurrentPrice(minSale, maxSale);

            iterationCount++;
        }

        throw new RuntimeException(String.format("주어진 조건 { sale : %s } 으로 IRR %f 를 만족하는 적정 매출을 찾을 수 없습니다.", inputSale, targetIRR));
    }


    private List<BigDecimal> determineSearchRange(BigDecimal inputSale) {

        BigDecimal npv = npvCalculator.calculateNPV(accountCalculator.calculate(inputSale), targetIRR, term);

        BigDecimal minPrice = BigDecimal.ZERO;
        BigDecimal maxPrice = inputSale;


        int searchCount = 0;
        final int maxSearchCount = 100;
        while (npv.signum() < 0) {
            minPrice = maxPrice;
            maxPrice = maxPrice.multiply(BigDecimal.valueOf(10));
            npv = npvCalculator.calculateNPV(accountCalculator.calculate(maxPrice), targetIRR, term);

            if (++searchCount > maxSearchCount) {
                throw new IllegalStateException("주어진 조건으로 적정 매출을 탐색을 할 수 없습니다.");
            }
        }


        searchCount = 0;
        if (minPrice.signum() == 0) {
            minPrice = inputSale.divide(BigDecimal.valueOf(100), RoundingMode.DOWN);
            npv = npvCalculator.calculateNPV(accountCalculator.calculate(minPrice), targetIRR, term);

            while (npv.signum() > 0) {
                minPrice = minPrice.divide(BigDecimal.valueOf(100), RoundingMode.DOWN);
                npv = npvCalculator.calculateNPV(accountCalculator.calculate(minPrice), targetIRR, term);

                if (++searchCount > maxSearchCount) {
                    throw new IllegalStateException("주어진 조건으로 적정 매출을 탐색을 할 수 없습니다.");
                }
            }
            maxPrice = minPrice.multiply(BigDecimal.valueOf(100));

        }

        LOGGER.info("range : " + List.of(minPrice, maxPrice));
        return List.of(minPrice, maxPrice);
    }

    private BigDecimal adjustCurrentPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        return minPrice.add(maxPrice).divide(BigDecimal.valueOf(2),10, RoundingMode.HALF_UP);
    }

}
