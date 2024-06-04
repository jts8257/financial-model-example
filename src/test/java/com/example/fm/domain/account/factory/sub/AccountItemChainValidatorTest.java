package com.example.fm.domain.account.factory.sub;

import com.example.fm.domain.account.item.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountItemChainValidatorTest {


    @Test
    public void retrieveHeadItSelfAfterValidation() {
        BigDecimal ltv = BigDecimal.ONE;
        BigDecimal r = BigDecimal.ONE;
        BigDecimal t = BigDecimal.ONE;
        BigDecimal s = BigDecimal.ONE;
        BigDecimal c = BigDecimal.ONE;
        BigDecimal cagr = BigDecimal.ONE;
        int period = 10;

        AccountChainItem head = new PriceNOIAccount(ltv, r, t)
                .setTail(new EbitAccount())
                .setTail(new PriceFindCostAccount(period, s, c))
                .setTail(new PriceFindSaleAccount(s, cagr, period));


        assertEquals(head, AccountItemChainValidator.validateChain(head));
    }

    @Test
    public void nullHeadParameter() {

        assertThrows(
                IllegalArgumentException.class,
                () -> AccountItemChainValidator.validateChain(null),
                "파라미터로 받은 AccountItem 이 null 입니다. 체인 초기화에 실패한것으로 보입니다.");
    }

    @Test
    public void notAHeadStateParameter() {

        AccountChainItem tail = new EbitAccount();
        AccountChainItem head = new EbitAccount();
        tail.setTail(head);

        assertThrows(
                IllegalArgumentException.class,
                () -> AccountItemChainValidator.validateChain(head),
                "파라미터로 받은 AccountItem 이 head 부분이 아닙니다. 체인이 적절히 구성되어 있지 않습니다.");
    }

    @Test
    public void chainHaveDuplicatedAccount() {

        AccountChainItem head = new EbitAccount()
                .setTail(new EbitAccount());

        assertThrows(
                IllegalArgumentException.class,
                () -> AccountItemChainValidator.validateChain(head),
                "계정 계산 체인에 중복된 계정이 존재합니다.");
    }

    @Test
    public void reversedAccountLayer() {

        BigDecimal ltv = BigDecimal.ONE;
        BigDecimal r = BigDecimal.ONE;
        BigDecimal t = BigDecimal.ONE;
        BigDecimal s = BigDecimal.ONE;
        BigDecimal c = BigDecimal.ONE;
        BigDecimal cagr = BigDecimal.ONE;
        int period = 10;

        AccountChainItem head = new PriceNOIAccount(ltv, r, t)
                .setTail(new PriceFindCostAccount(period, s, c))
                .setTail(new EbitAccount())
                .setTail(new PriceFindSaleAccount(s, cagr, period));

        assertThrows(
                IllegalArgumentException.class,
                () -> AccountItemChainValidator.validateChain(head),
                "계정 레벨이 순차적이지 않습니다.");
    }

    @Test
    public void singleAccount() {
        AccountChainItem head = new EbitAccount();

        assertEquals(head, AccountItemChainValidator.validateChain(head));
    }

}