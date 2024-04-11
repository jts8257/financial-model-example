package com.example.fm.domain.calculator.account.calculator;

import java.math.BigDecimal;
import java.util.List;

public abstract class AccountCalculator {
    protected AccountCalculator prev;
    protected AccountCalculator next;

    public void setNext(AccountCalculator next) {
        this.next = next;
        next.setPrev(this);
    }

    private void setPrev(AccountCalculator prev) {
        this.prev = prev;
    }

    protected AccountCalculator getPrev() {return prev;}

    public abstract List<BigDecimal> calculate(BigDecimal value);

    public boolean isFirst() {return this.prev == null;}

    public int getCalculatorChainCount() {
        int count = 1;
        AccountCalculator calculator = this;
        while (!calculator.isFirst()) {
            calculator = calculator.getPrev();
            count++;
        }

        return count;
    }
}
