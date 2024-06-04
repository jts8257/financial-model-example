## 목표
이 리포지토리는 코드리뷰를 원활하게 진행하기 위해 회사의 재무 모델링 구현 코드의 핵심 사항들만 별도로 구성해놓는데 목표가 있다. 

## Version
- 0.1.0 : 적정 가격 찾기에 대한 재무모델 구성 PoC 단계의 코드 제공
- 0.2.0 : 적정 매출 찾기 시나리오 추가, 캡슐화 및 추상화 단계 추가


## 프로젝트 구조
- 어플리케이션 계층과 도메인 계층의 분리, 도메인 계층은 POJO 로 구성하여 다른 Java 프로젝트로의 이식성 확보
```text

├── application
│   ├── controller
│   ├── dto
│   └── service
└── domain
    ├── account // 계산 계정 모듈 package 
    │   ├── AccountCalculator.java // 
    │   ├── AccountLayer.java // 계산 계정의 단계를 나타내는 enum
    │   ├── factory // 계정을 순차적으로 계산할 수 있도록 chain 을 만들어서 head 를 반환함
    │   └── item // 계정
    ├── financial // 금융 계산 모듈 package 
    │   ├── irr
    │   └── npv
    ├── finder // 특정 값을 찾는 모듈 package
    │   ├── BinarySearchValueFinderTemplate.java // 이진검색 알고리즘으로 특정 값을 찾는 추상 클래스
    │   ├── ValueFinder.java // 특정 값을 찾는 interface 
    │   ├── factory // ValueFinder 인터페이스의 구현체를 추상화하여 전달하는 역할
    │   └── vo

```


## 주요 디자인 패턴

### AccountChainItem 과 책임연쇄 패턴
- 재무모델에 활용될 계산식은 각 회계 계정들의 계산이 연쇄적으로 일어나야한다.
- 각 회계 계정의 계산은 AccountCalculator 인터페이스의 calculate 메서드를 구체화하고, 각 계산식간의 선행 후행관계는 AccountChainItem 를 연결하여 구성한다.

```java
public interface AccountCalculator {
    List<BigDecimal> calculate(BigDecimal value);
}

public abstract class AccountChainItem implements AccountCalculator {
    protected AccountChainItem predecessor; // 선행 계산식
    protected AccountChainItem successor; // 후행 계산식

    /**
     * item chain 의 어느 구간에 들어오든 결국 tail 부분에 inputPredecessor 가 도달하게 만든다.
     * @param inputPredecessor 현재 item chain 의 tail 부분으로 갈 item
     * @return 현재 item 을 반환한다.
     * @throws IllegalArgumentException inputPredecessor 가 successor 에 다른 아이템을 가지고 있을 경우
     */
    public AccountChainItem setTail(AccountChainItem inputPredecessor) {
        ...
        return this;
    }
}
```

### AccountChainItem 구성을 위한 Factory 메서드 패턴, 템플릿 패턴, 싱글톤 패턴
- 책임 연쇄를 통해 AccountCalculator 의 calculate 메서드를 연결하는 chain 은 AccountChainItem 추상 클래스를 통해 가능하다.
- 직접 그 chain 의 연결된 결과물(AccountCalculator)을 반환하는 역할 AccountChainFactory 인터페이스에 일임한다.
- 인터페이스를 구현하는 책임은 ValidAccountChainFactory 템플릿이 지니며, 직접 연결을 구성(compose) 하는 책임은 ValidAccountChainFactory 를 상속한 객체들이 지닌다.
- 
```java
public interface AccountChainFactory {
    /**
     *
     * @param financialModel 적용 재무모델 정보
     * @param value input 이 되는 value
     * @param period 고려 기간
     * @return 위의 사항을 종합하여 적합한 재무 계정 계산기를 반환
     */
    AccountCalculator create(FinancialModel financialModel, BigDecimal value, int period);

}

abstract public class ValidAccountChainFactory implements AccountChainFactory {
    @Override
    public AccountCalculator create(FinancialModel financialModel, BigDecimal value, int period) {
        // ...
        return AccountItemChainValidator.validateChain(compose(financialModel, value, period));

    }

    /**
     *
     * @param financialModel 적용 재무모델 정보
     * @param value input 이 되는 value
     * @param period 고려 기간
     * @return 위의 사항을 종합하여 적합한 재무 계정 계산기 체인을 반환
     */
    abstract public AccountChainItem compose(FinancialModel financialModel, BigDecimal value, int period);
}


public class PriceFindChainFactory extends ValidAccountChainFactory {
    private static volatile PriceFindChainFactory instance;

    // 더블 체킹 락(Double-Check Locking) 패턴을 적용한 싱글톤 패턴
    public static PriceFindChainFactory getInstance() {
        if (instance == null) {
            synchronized (PriceFindChainFactory.class) {
                if (instance == null) {
                    instance = new PriceFindChainFactory();
                }
            }
        }
        return instance;
    }

    private PriceFindChainFactory() {}

    @Override
    public AccountChainItem compose(FinancialModel financialModel, BigDecimal saleSource, int period) {
        // 구현
    }
}
```


## 주요 알고리즘

### IRR 계산기
- NewtonRaphsonIRRCalculator
- `뉴턴-랩슨 방식`을 통해 IRR 을 계산하는 알고리즘 구현
- 마이크로소프트 POI 라이브러리를 계량하여 year, month, day 기준의 IRR 을 계산할 수 있도록함
- BigDecimal 객체를 통해 계산하도록 구성하여 부동소수점 이슈의 영향을 받진 않아 계산결과의 신뢰도를 높임, 다만 속도는 희생됨


### 이진검색 알고리즘 
- BinarySearchValueFinderTemplate
- 목표 값을 찾기위한 이진검색 알고리즘 적용
