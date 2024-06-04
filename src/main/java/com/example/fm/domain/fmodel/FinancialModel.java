package com.example.fm.domain.fmodel;


public enum FinancialModel {
    PRICE_MODEL_1, PRICE_MODEL_2, PRICE_MODEL_3,
    SALE_MODEL_1, SALE_MODEL_2, SALE_MODEL_3;

    public boolean isPriceModel() {
        return this.name().startsWith("PRICE");
    }

}
