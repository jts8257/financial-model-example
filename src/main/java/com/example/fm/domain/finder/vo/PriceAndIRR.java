package com.example.fm.domain.finder.vo;


import java.math.BigDecimal;

public class PriceAndIRR {
    private final BigDecimal price;
    private final BigDecimal irr;


    public PriceAndIRR(BigDecimal price, BigDecimal irr) {
        this.price = price;
        this.irr = irr;
    }


    public BigDecimal getIrr() {
        return irr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "PriceAndIRR{" +
                "price=" + price +
                ", irr=" + irr +
                '}';
    }
}
