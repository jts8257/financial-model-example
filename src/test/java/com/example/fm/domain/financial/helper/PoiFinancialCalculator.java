package com.example.fm.domain.financial.helper;

import com.example.fm.domain.fmodel.Term;
import com.example.fm.domain.financial.irr.IRRCalculator;
import com.example.fm.domain.financial.npv.NPVCalculator;
import com.example.fm.domain.financial.RateTermAdjustor;
import org.apache.poi.ss.formula.functions.Irr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.apache.poi.ss.formula.functions.FinanceLib.npv;

public class PoiFinancialCalculator implements IRRCalculator, NPVCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PoiFinancialCalculator.class);

    @Override
    public BigDecimal calculateNPV(List<BigDecimal> cashFlow, BigDecimal discountRate, Term term) {

        double npv = npv(RateTermAdjustor.adjustByTerm(discountRate, term).doubleValue(),
                cashFlow.stream().mapToDouble(BigDecimal::doubleValue).toArray());
        return BigDecimal.valueOf(npv).setScale(10, RoundingMode.DOWN);
    }

    @Override
    public BigDecimal calculateIRR(List<BigDecimal> cashFlow, Term term) {

        double outputIRR = Irr.irr(cashFlow.stream().mapToDouble(BigDecimal::doubleValue).toArray());
        if (Double.isNaN(outputIRR)) {
            LOGGER.warn("POI 라이브러리를 이용하여 IRR 근사치를 찾지 못하여 null 을 반환합니다.");
            return null;
        }
        return RateTermAdjustor.adjustByTerm(BigDecimal.valueOf(outputIRR), term);
    }
}
