package com.example.fm.domain.fmodel;

import java.math.BigDecimal;
import java.util.Objects;

public class ModelAssumption {

    private final Term term;
    private BigDecimal saleSource;
    private int period;
    private final BigDecimal cagr;
    private final BigDecimal inflationRate;
    private final BigDecimal ltv;
    private final BigDecimal interestRate;
    private final BigDecimal taxRate;

    public static ModelAssumption byModel(FinancialModel model) {
        return switch (model) {
            case PRICE_MODEL_1, SALE_MODEL_1 -> new ModelAssumption(Term.YEAR, BigDecimal.valueOf(0.03), BigDecimal.valueOf(0.02),
                    BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.05),BigDecimal.valueOf(0.1));
            case PRICE_MODEL_2, SALE_MODEL_2 -> new ModelAssumption(Term.YEAR, BigDecimal.valueOf(0.02), BigDecimal.valueOf(0.025),
                    BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.07), BigDecimal.valueOf(0.08));
            case PRICE_MODEL_3, SALE_MODEL_3 -> new ModelAssumption(Term.YEAR, BigDecimal.valueOf(0.035), BigDecimal.valueOf(0.02),
                    BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.055), BigDecimal.valueOf(0.095));
        };
    }

    private ModelAssumption(
            Term term, BigDecimal cagr, BigDecimal inflationRate, BigDecimal ltv, BigDecimal interestRate, BigDecimal taxRate) {
        this.term = term;
        this.cagr = Objects.isNull(cagr) ? BigDecimal.ZERO : cagr;
        this.inflationRate = Objects.isNull(inflationRate) ? BigDecimal.ZERO : inflationRate;
        this.ltv = Objects.isNull(ltv) ? BigDecimal.ZERO : ltv;
        this.interestRate = Objects.isNull(interestRate) ? BigDecimal.ZERO : interestRate;
        this.taxRate = Objects.isNull(taxRate) ? BigDecimal.ZERO : taxRate;;
    }

    public Term getTerm() {
        return term;
    }

    public BigDecimal getLtv() {return ltv;}

    public BigDecimal getSaleSource() {
        return saleSource;
    }

    public int getPeriod() {
        return period;
    }

    public BigDecimal getCagr() {
        return cagr == null ? BigDecimal.ZERO : cagr;
    }

    public BigDecimal getInflationRate() {
        return inflationRate;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setSaleSource(BigDecimal saleSource) {this.saleSource = saleSource;}
    public void setPeriod(int period) {this.period = period;}

    @Override
    public String toString() {
        return "ModelAssumption{" +
                "term=" + term +
                ", saleSource=" + saleSource +
                ", period=" + period +
                ", cagr=" + cagr +
                ", inflationRate=" + inflationRate +
                ", ltv=" + ltv +
                ", interestRate=" + interestRate +
                ", taxRate=" + taxRate +
                '}';
    }
}
