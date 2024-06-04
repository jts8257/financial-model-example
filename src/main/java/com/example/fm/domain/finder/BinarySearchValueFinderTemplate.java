package com.example.fm.domain.finder;

import com.example.fm.domain.finder.vo.TargetValueAndIRR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public abstract class BinarySearchValueFinderTemplate implements ValueFinder {
    private static final Logger LOGGER = LoggerFactory.getLogger(BinarySearchValueFinderTemplate.class);
    protected final BigDecimal targetIRR;
    protected final BigDecimal PRECISION;
    protected final int MAX_ITERATIONS;
    public BinarySearchValueFinderTemplate(BigDecimal targetIRR, BigDecimal PRECISION, int MAX_ITERATIONS) {
        this.targetIRR = targetIRR;
        this.PRECISION = PRECISION;
        this.MAX_ITERATIONS = MAX_ITERATIONS;
    }

    /**
     * 이진검색 알고리즘을 활용하여 적정 값을 를 찾는다.
     * @param initialInput 최초에 탐색을 시작할 시작점
     * @return {value: 주어진 정밀도 수준에서 신뢰할 수 있는 적정 구매가격 값, irr: 주어진 정밀도 수준에서 신뢰할 수 있는 타깃 irr}
     * @throws RuntimeException 이진검색 최대 검색 횟수에 도달했음에도 주어진 정밀도 수준을 충족하는 적정 값을 찾지 못함
     * @throws IllegalStateException 이진검색을 위한 검색 범위를 정할때 주어진 조건으로 적절한 범위를 지정할 수 없음
     */
    @Override
    public TargetValueAndIRR find(BigDecimal initialInput) {

        List<BigDecimal> range = initializeRange(convertInitialInput(initialInput));
        BigDecimal input = calculateMidFrom(range);

        int iterationCount = 0;
        while (iterationCount++ < MAX_ITERATIONS) {
            BigDecimal output = calculateOutputBy(input);

            if (output.abs().compareTo(PRECISION) < 0) {
                return new TargetValueAndIRR(input, calculateIRRBy(input));
            }

            range = adjustRangeBy(output, range);
//            LOGGER.info(String.format("[%d] calculateOutputBy(%s) = %s | range -> %s", iterationCount,
//                    input.setScale(0, RoundingMode.HALF_UP),
//                    output.setScale(0, RoundingMode.HALF_UP),
//                    range));
            input = calculateMidFrom(range);

        }

        throw new RuntimeException(String.format("주어진 조건으로 IRR %f 를 만족하는 적정 가격을 찾을 수 없습니다.", targetIRR));
    }

    protected abstract BigDecimal convertInitialInput(BigDecimal initialInput);

    /**
     * 이진검색을 수행할 범위를 계산한다.
     * @param inputValue 최초 범위 계산에 활용될 값
     * @return [최소 범위, 최대 범위]
     */
    protected abstract List<BigDecimal> initializeRange(BigDecimal inputValue);


    /**
     * 주어진 값을 이용해서 output value 를 계산한다.
     * @param input 계산에 활용될 값
     * @return 주어진 조건하에서 계산된 적정 가격 혹은 적정 매출
     */
    protected abstract BigDecimal calculateOutputBy(BigDecimal input);

    /**
     *
     * @param input IRR 계산을 위해 활용될 값
     * @return 계산된 IRR
     */
    protected abstract BigDecimal calculateIRRBy(BigDecimal input);

    /**
     * 이진 탐색을 위해 범위값을 조정한다.
     * @param output 현재 계산된 output
     */
    protected abstract List<BigDecimal> adjustRangeBy(BigDecimal output, List<BigDecimal> range);


    /**
     *
     * @param valueRange 중앙값을 가져올 valueRange
     * @return valueRange 의 (min + max) / 2, 소수점 4자리까지 허용, 반올림
     */
    protected BigDecimal calculateMidFrom(List<BigDecimal> valueRange) {
        BigDecimal max = valueRange.stream().max(BigDecimal::compareTo).orElseThrow();
        BigDecimal min = valueRange.stream().min(BigDecimal::compareTo).orElseThrow();
        return max.add(min).divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_UP);
    }
}
