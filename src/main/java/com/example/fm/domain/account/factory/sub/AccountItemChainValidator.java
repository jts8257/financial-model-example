package com.example.fm.domain.account.factory.sub;

import com.example.fm.domain.account.item.AccountChainItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class AccountItemChainValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountItemChainValidator.class);


    /**
     *
     * @param head AccountChain 의 head 부분
     * @return 검사를 마친 head
     * @throws IllegalArgumentException head 가 실제 head 가 아닐때, head 가 null 일때, 계산식이 비정상적으로 연결되어 있을 때
     * @throws IllegalStateException chain 안에 중복되는 계정이 존재할떄
     */
    public static AccountChainItem validateChain(AccountChainItem head) {
        if (head == null) {
            LOGGER.error("파라미터로 받은 AccountItem 이 null 입니다. 체인 초기화에 실패한것으로 보입니다.");
            throw new IllegalArgumentException("계정 계산 체인의 시작 부분이 null 입니다. 체인 초기화에 실패했습니다.");
        }

        if (!head.isHead()) {
            LOGGER.error("파라미터로 받은 AccountItem 이 head 부분이 아닙니다. 체인이 적절히 구성되어 있지 않습니다.");
            throw new IllegalArgumentException("계정 계산 체인이 head 부터 시작하지 않습니다.");
        }

        Set<AccountChainItem> visited = new HashSet<>();
        AccountChainItem current = head;

        while (current != null) {

            if (visited.contains(current)) {
                LOGGER.error("계정 계산 체인에 중복된 계정이 존재합니다.");
                throw new IllegalStateException("계정 계산 체인에 중복된 계정이 존재합니다.");
            }

            visited.add(current);


            if (!current.isTail() && (current.getLayerLevel() - current.getPredecessor().getLayerLevel() != 1)) {
                LOGGER.error(String.format("계정 계산 체인이 %s -> %s 으로 연결되어서 순차적이지 않습니다.",
                        current.getPredecessor().getClass().getName(), current.getClass().getName()));
                throw new IllegalArgumentException("계정 레벨이 순차적이지 않습니다.");
            }

            current = current.getPredecessor();
        }

        return head;
    }
}
