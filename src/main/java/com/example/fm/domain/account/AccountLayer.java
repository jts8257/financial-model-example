package com.example.fm.domain.account;

public enum AccountLayer {
    SALE(1), COST(2), EBIT(3), NOI(4);

    private final int level;

    AccountLayer(int level) {
        this.level = level;
    }

    public int getLayerLevel() {
        return this.level;
    }
}
