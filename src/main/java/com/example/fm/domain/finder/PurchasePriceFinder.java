package com.example.fm.domain.finder;

import com.example.fm.domain.finder.vo.PriceAndIRR;
import com.example.fm.domain.fmodel.Term;
import com.example.fm.domain.calculator.account.calculator.AccountCalculator;
import com.example.fm.domain.calculator.financial.irr.IRRCalculator;
import com.example.fm.domain.calculator.financial.irr.NewtonRaphsonIRRCalculator;
import com.example.fm.domain.calculator.financial.npv.NPVCalculator;
import com.example.fm.domain.calculator.financial.npv.StreamNPVCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PurchasePriceFinder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PurchasePriceFinder.class);
    private final AccountCalculator accountCalculator;
    private final Term term;
    private final BigDecimal targetIRR;
    private final NPVCalculator npvCalculator = new StreamNPVCalculator();
    private final IRRCalculator irrCalculator = new NewtonRaphsonIRRCalculator();

    public PurchasePriceFinder(Term term, AccountCalculator accountCalculator, BigDecimal targetIRR) {
        this.term = term;
        this.accountCalculator = accountCalculator;
        this.targetIRR = targetIRR;
    }

    /**
     * 이진검색 알고리즘을 활용하여 적정 구매가격 (properPurchasePrice) 를 찾는다.
     * @param inputPrice 최초에 탐색을 시작할 시작점
     * @return {price: 주어진 정밀도 수준에서 신뢰할 수 있는 적정 구매가격 값, irr: 주어진 정밀도 수준에서 신뢰할 수 있는 타깃 irr}
     * @throws RuntimeException 이진검색 최대 검색 횟수에 도달했음에도 주어진 정밀도 수준을 충족하는 적정 구매가격을 찾지 못함
     * @throws IllegalStateException 이진검색을 위한 검색 범위를 정할때 주어진 조건으로 적절한 범위를 지정할 수 없음
     */
    public PriceAndIRR find(BigDecimal inputPrice) {

        if (inputPrice == null || inputPrice.signum() <= 0) {
            LOGGER.warn("적정가격을 찾는 메서드에 최초 가격값이 비정상적으로 입력되었습니다. 10억으로 변환하여 계산합니다.");
            inputPrice = BigDecimal.valueOf(1000000000L);
        }

        // 관련 산업에 따라 허용 오차 정도가 달라진다.
        BigDecimal NPV_PRECISION = BigDecimal.valueOf(10000);

        List<BigDecimal> priceRange = determineSearchRange(inputPrice);
        BigDecimal minPrice = priceRange.get(0);
        BigDecimal maxPrice = priceRange.get(1);

        BigDecimal currentPrice = adjustCurrentPrice(minPrice, maxPrice);

        int iterationCount = 0;
        int MAX_ITERATIONS = 50;
        while (iterationCount < MAX_ITERATIONS) {
            BigDecimal outputNPV = npvCalculator.calculateNPV(accountCalculator.calculate(currentPrice), targetIRR, term).setScale(0, RoundingMode.DOWN);

            if (outputNPV.abs().compareTo(NPV_PRECISION) < 0) { // 정밀도 수준을 만족하는 가격이면 반환한다.
                BigDecimal irr = irrCalculator.calculateIRR(accountCalculator.calculate(currentPrice), term);
                LOGGER.info(String.format("PriceAndIRR FOUND :: from %s -> {price : %s, irr : %f, npv : %s } total iteration : %d",
                        inputPrice, currentPrice, irr, outputNPV, iterationCount));

                return new PriceAndIRR(currentPrice, irr);

            }

            if (outputNPV.signum() > 0) {// 현재가치가 양수면 투하자본을 늘려야한다.
                minPrice = currentPrice;
            } else {
                maxPrice = currentPrice; // 현재가치가 음수면 투하자본을 줄여야한다.
            }

            currentPrice = adjustCurrentPrice(minPrice, maxPrice);
            iterationCount++;
        }

        throw new RuntimeException(String.format("주어진 조건 { price : %s } 으로 IRR %f 를 만족하는 적정 가격을 찾을 수 없습니다.", inputPrice, targetIRR));
    }

    /**
     * 이진검색을 위한 탐색 범위를 지정함, 목표 수익률 (IRR) 수준으로 할인률을 두고 inputPrice 를 10곱, 10 나누기를 반복하여 목표 수익률을 충족하는 구매가격 범위를 지정한다.
     * 최대, 최소 범위를 지정함으로써 Edge Case 가 발생했을 때 더 빠르게 결과값을 반환한다. 다만 이를 위해 Non Edge Case 에서 탐색 속도가 느려지게 된다.
     * @param inputPrice 최초 범위를 지정할 구매 가격
     * @return List[minPrice, maxPrice], size = 2;
     * @throws IllegalStateException 이진검색을 위한 검색 범위를 정할때 주어진 조건으로 적절한 범위를 지정할 수 없음
     */
    private List<BigDecimal> determineSearchRange(BigDecimal inputPrice) {

        BigDecimal npv = npvCalculator.calculateNPV(accountCalculator.calculate(inputPrice), targetIRR, term);
        BigDecimal minPrice = BigDecimal.ZERO;
        BigDecimal maxPrice = inputPrice;


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
            minPrice = inputPrice.divide(BigDecimal.valueOf(10), RoundingMode.DOWN);
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

    private BigDecimal adjustCurrentPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        return minPrice.add(maxPrice).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
    }

}
