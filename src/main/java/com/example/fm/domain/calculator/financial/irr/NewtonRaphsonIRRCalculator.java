package com.example.fm.domain.calculator.financial.irr;

import com.example.fm.domain.fmodel.Term;
import com.example.fm.domain.calculator.financial.RateTermAdjustor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class NewtonRaphsonIRRCalculator implements IRRCalculator {
    private int MAX_ITERATION = 1000;
    private BigDecimal PRECISION = new BigDecimal("1E-7");
    private static final Logger LOGGER = LoggerFactory.getLogger(NewtonRaphsonIRRCalculator.class);


    @Override
    public BigDecimal calculateIRR(List<BigDecimal> cashFlow, Term term) {
        BigDecimal x0 = BigDecimal.valueOf(0.1);

        for (int i = 0; i < MAX_ITERATION; i++) {

            final BigDecimal factor = BigDecimal.ONE.add(RateTermAdjustor.adjustByTerm(x0, term));
            BigDecimal denominator = factor;
            if (denominator.signum() == 0) {
                LOGGER.warn("Returning Null because IRR has found an denominator of 0");
                return null;
            }

            BigDecimal fValue = cashFlow.get(0);
            BigDecimal fDerivative = BigDecimal.ZERO;
            for (int t = 1; t < cashFlow.size(); t++) {
                final BigDecimal value = cashFlow.get(t);
                fValue = fValue.add(value.divide(denominator, 15, RoundingMode.DOWN));
                denominator = denominator.multiply(factor);
                fDerivative = fDerivative.subtract(value.multiply(BigDecimal.valueOf(t)).divide(denominator, 15, RoundingMode.DOWN));
            }

            // the essence of the Newton-Raphson Method
            if (fDerivative.signum() == 0) {
                LOGGER.warn("IRR 계산 중 0으로 나누는 오류가 발생했습니다.");
                return null;
            }
            BigDecimal x1 =  x0.subtract(fValue.divide(fDerivative,15, RoundingMode.DOWN));

            if (x1.subtract(x0).abs().compareTo(PRECISION) <= 0) {
                LOGGER.info(String.format("(%d / %d) 회 탐색으로 정밀도 %s 범위 안에서 근사하는 근사치 %s 를 찾아서 반환합니다.",
                        i + 1, MAX_ITERATION,  PRECISION, x1));
                return x1;
            }

            x0 = x1;
        }
        // maximum number of iterations is exceeded
        LOGGER.warn(String.format("%d 회 탐색했지만 IRR 근사치를 찾지 못했습니다. null 을 반환합니다.", MAX_ITERATION));
        return null;
    }

}
