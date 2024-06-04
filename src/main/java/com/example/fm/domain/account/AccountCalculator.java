package com.example.fm.domain.account;

import java.math.BigDecimal;
import java.util.List;

/**
 * Account 의 세부 구성 및 연결은 AccountItem
 */
public interface AccountCalculator {
    List<BigDecimal> calculate(BigDecimal value);
}
