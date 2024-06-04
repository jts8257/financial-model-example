package com.example.fm.domain.account.item;


import com.example.fm.domain.account.AccountCalculator;
import com.example.fm.domain.account.AccountLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public abstract class AccountChainItem implements AccountCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountChainItem.class);
    protected final String accountName;
    protected final AccountLayer accountLayer;
    protected AccountChainItem predecessor; // 먼저 계산되어야 하는 계산식
    protected AccountChainItem successor; // 다음에 계산되어야 하는 계산식

    public AccountChainItem(String accountName, AccountLayer accountLayer) {
        this.accountName = accountName;
        this.accountLayer = accountLayer;
    }

    /**
     * 현재 item 에 predecessor 가 없다면 [inputPredecessor -> item] 가 된다.
     * 현재 item 에 predecessor 가 있다면 [inputPredecessor -> alreadyPredecessors... -> item] 순서가 되도록 한다.
     * 위의 과정을 반복해서 item chain 의 어느 구간에 들어오든 결국 tail 부분에 inputPredecessor 가 도달하게 만든다.
     * @param inputPredecessor 현재 item chain 의 tail 부분으로 갈 item
     * @return 현재 item 을 반환한다.
     * @throws IllegalArgumentException inputPredecessor 가 successor 에 다른 아이템을 가지고 있을 경우
     */
    public AccountChainItem setTail(AccountChainItem inputPredecessor) {

        if (!inputPredecessor.isHead()) {
            LOGGER.error(String.format("계정 계산 체인 형성을 위해 호출된 setTail 메서드에 이미 선순위 계정 계산(%s) 등을 보유한 %s 가 파라미터로 전달되었습니다.",
                    inputPredecessor.getSuccessor().getClass().getName(), inputPredecessor.getClass().getName()));
            throw new IllegalArgumentException("이미 선순위 계산 계정이 있는 계정 계산 체인 중간에 선순위 계산 계정을 추가하려고 합니다.");
        }

        if (isTail()) { // 현재 item 에 predecessor 가 없다면 [inputPredecessor -> item] 가 된다.
            this.predecessor = inputPredecessor;
            inputPredecessor.successor = this;
        } else { // 현재  item 에 predecessor 가 있다면 [inputPredecessor -> alreadyPredecessors... -> item] 순서가 되도록 한다.
            AccountChainItem alreadyBase = this.predecessor;
            alreadyBase.setTail(inputPredecessor);
        }
        return this;
    }

    public Integer getLayerLevel() {
        return accountLayer != null ? accountLayer.getLayerLevel() : null;
    }

    public AccountChainItem getPredecessor() {return predecessor;}

    public AccountChainItem getSuccessor() {return successor;}

    public boolean isHead() {
        return this.successor == null;
    }

    public boolean isTail() {
        return this.predecessor == null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountChainItem that)) return false;
        return Objects.equals(accountName, that.accountName) && accountLayer == that.accountLayer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountName, accountLayer);
    }

}
