package com.example.fm.domain.finder.vo;


import java.math.BigDecimal;

public class TargetValueAndIRR {
    private final BigDecimal value;
    private final BigDecimal irr;


    public TargetValueAndIRR(BigDecimal value, BigDecimal irr) {
        this.value = value;
        this.irr = irr;
    }


    public BigDecimal getIrr() {
        return irr;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "PriceAndIRR{" +
                "price=" + value +
                ", irr=" + irr +
                '}';
    }
}
