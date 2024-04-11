## 목표
이 리포지토리는 코드리뷰를 원활하게 진행하기 위해 회사의 재무 모델링 구현 코드의 핵심 사항들만 별도로 구성해놓는데 목표가 있다. 


## 프로젝트 구조
- 어플리케이션 계층과 도메인 계층의 분리, 도메인 계층은 POJO 로 구성하여 다른 Java 프로젝트로의 이식성 확보
```text
// application
├── FmApplication.java
├── application
│   ├── controller
│   ├── dto
│   └── service
└── domain
    ├── calculator // 계정 및 IRR, NPV 계산기
    ├── finder // fmodel, calculator 를 이용한 탐색 로직 실행 클래스
    └── fmodel // financial model 관련
```


## 주요 로직

### AccountCalculator 생성
- 여러 회계 계정들이 순서를 가지고 결합하는 특성을 반영하여 calculator 간 chain 을 구성
```java
public abstract class AccountCalculator {
    protected AccountCalculator prev;
    protected AccountCalculator next;
    // ... 이하 생략

    public abstract List<BigDecimal> calculate(BigDecimal value);
}
```

- Factory 클래스에 chain 생성 역할 일임
```java
public class AccountCalculatorChainFactory {

    // 객체 생성 불허
    private AccountCalculatorChainFactory () {}

    public static AccountCalculator creteChain(
            // ...
    ) {
        // ...
        SaleAccountCalculator saleAccountCalculator = new SaleAccountCalculator();
        CostAccountCalculator costCalculator = new CostAccountCalculator();
        EbitCalculator ebitCalculator = new EbitCalculator();
        NOIAccountCalculator noiAccountCalculator = new NOIAccountCalculator();

        saleAccountCalculator.setNext(costCalculator);
        costCalculator.setNext(ebitCalculator);
        ebitCalculator.setNext(noiAccountCalculator);

        return noiAccountCalculator;
    }
}
```
- 회계 계정간 순서를 가지고 결합하는 구조를 Factory 클래스가 생성하도록 일임하는 구조를 통해 이 계정을 계산해야하는 클래스는 오로지 Factory 클래스만 알고 있으면 게산을 수행하는 AccountCalculator 를 획득하는 구조를 만듦, 이를 통해 새로운 요구사항에 유연하게 대응할 수 있도록 함

### IRR 계산기
- 뉴턴-랩슨 방식을 통해 IRR 을 계산하는 알고리즘 구현
- 마이크로소프트 POI 라이브러리를 계량하여 year, month, day 기준의 IRR 을 계산할 수 있도록함
- BigDecimal 객체를 통해 계산하도록 구성하여 부동소수점 이슈의 영향을 받진 않아 계산결과의 신뢰도를 높임, 다만 속도는 희생됨

```java
public class NewtonRaphsonIRRCalculator implements IRRCalculator {
    private int MAX_ITERATION = 1000;
    private BigDecimal PRECISION = new BigDecimal("1E-7");
    private static final Logger LOGGER = LoggerFactory.getLogger(NewtonRaphsonIRRCalculator.class);


    @Override
    public BigDecimal calculateIRR(List<BigDecimal> cashFlow, Term term) {
        BigDecimal x0 = BigDecimal.valueOf(0.1);

        for (int i = 0; i < MAX_ITERATION; i++) {
            // RateTermAdjustor.adjustByTerm(x0, term) 과정을 통해 주어진 연율 이자율이 기간에 맞게 변경되도록함
            final BigDecimal factor = BigDecimal.ONE.add(RateTermAdjustor.adjustByTerm(x0, term));
            BigDecimal denominator = factor;
            if (denominator.signum() == 0) {
                LOGGER.warn("Returning Null because IRR has found an denominator of 0");
                return null;
            }

            BigDecimal fValue = cashFlow.get(0);
            BigDecimal fDerivative = BigDecimal.ZERO;
            for (int t = 1; t < cashFlow.size(); t++) {
                final BigDecimal value = cashFlow.get(t);
                fValue = fValue.add(value.divide(denominator, 15, RoundingMode.DOWN));
                denominator = denominator.multiply(factor);
                fDerivative = fDerivative.subtract(value.multiply(BigDecimal.valueOf(t)).divide(denominator, 15, RoundingMode.DOWN));
            }

            // the essence of the Newton-Raphson Method
            if (fDerivative.signum() == 0) {
                LOGGER.warn("IRR 계산 중 0으로 나누는 오류가 발생했습니다.");
                return null;
            }
            BigDecimal x1 =  x0.subtract(fValue.divide(fDerivative,15, RoundingMode.DOWN));

            if (x1.subtract(x0).abs().compareTo(PRECISION) <= 0) {
                LOGGER.info(String.format("(%d / %d) 회 탐색으로 정밀도 %s 범위 안에서 근사하는 근사치 %s 를 찾아서 반환합니다.",
                        i + 1, MAX_ITERATION,  PRECISION, x1));
                return x1;
            }

            x0 = x1;
        }
        // maximum number of iterations is exceeded
        LOGGER.warn(String.format("%d 회 탐색했지만 IRR 근사치를 찾지 못했습니다. null 을 반환합니다.", MAX_ITERATION));
        return null;
    }

}
```

### PurchasePriceFinder
- 특정 범위를 상정한 이진검색 알고리즘 적용
 
```java
public class PurchasePriceFinder {
    //... 계산에 필요한 필드 생략

    /**
     * 이진검색 알고리즘을 활용하여 적정 구매가격 (properPurchasePrice) 를 찾는다.
     * @param inputPrice 최초에 탐색을 시작할 시작점
     * @return {price: 주어진 정밀도 수준에서 신뢰할 수 있는 적정 구매가격 값, irr: 주어진 정밀도 수준에서 신뢰할 수 있는 타깃 irr}
     * @throws RuntimeException 이진검색 최대 검색 횟수에 도달했음에도 주어진 정밀도 수준을 충족하는 적정 구매가격을 찾지 못함
     * @throws IllegalStateException 이진검색을 위한 검색 범위를 정할때 주어진 조건으로 적절한 범위를 지정할 수 없음
     */
    public PriceAndIRR find(BigDecimal inputPrice) {

        if (inputPrice == null || inputPrice.signum() <= 0) {
            LOGGER.warn("적정가격을 찾는 메서드에 최초 가격값이 비정상적으로 입력되었습니다. 10억으로 변환하여 계산합니다.");
            inputPrice = BigDecimal.valueOf(1000000000L);
        }

        // 관련 산업에 따라 허용 오차 정도가 달라진다.
        BigDecimal NPV_PRECISION = BigDecimal.valueOf(10000);

        List<BigDecimal> priceRange = determineSearchRange(inputPrice);
        BigDecimal minPrice = priceRange.get(0);
        BigDecimal maxPrice = priceRange.get(1);

        BigDecimal currentPrice = adjustCurrentPrice(minPrice, maxPrice);

        int iterationCount = 0;
        int MAX_ITERATIONS = 50;
        while (iterationCount < MAX_ITERATIONS) {
            // ... targetIRR 을 할인률로 하여 반복적으로 NPV 를 계산하여 주어진 정밀도 수준의 오차를 충족하는지 체크
            iterationCount++;
        }

        throw new RuntimeException(String.format("주어진 조건 { price : %s } 으로 IRR %f 를 만족하는 적정 가격을 찾을 수 없습니다.", inputPrice, targetIRR));
    }

    /**
     * 이진검색을 위한 탐색 범위를 지정함, 목표 수익률 (IRR) 수준으로 할인률을 두고 inputPrice 를 10곱, 10 나누기를 반복하여 목표 수익률을 충족하는 구매가격 범위를 지정한다.
     * 최대, 최소 범위를 지정함으로써 Edge Case 가 발생했을 때 더 빠르게 결과값을 반환한다. 다만 이를 위해 Non Edge Case 에서 탐색 속도가 느려지게 된다.
     * @param inputPrice 최초 범위를 지정할 구매 가격
     * @return List[minPrice, maxPrice], size = 2;
     * @throws IllegalStateException 이진검색을 위한 검색 범위를 정할때 주어진 조건으로 적절한 범위를 지정할 수 없음
     */
    private List<BigDecimal> determineSearchRange(BigDecimal inputPrice) {
        // ... 이하 생략
        return List.of(minPrice, maxPrice);
    }

    private BigDecimal adjustCurrentPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        return minPrice.add(maxPrice).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
    }

}
```


## 테스트

- 프로젝트의 목적이 domain 계층을 소개하는 것이므로 Application 계층은 최소한만 구현하여 Exception Handling 조차 되지 않음. 이러한 사항이 test 에 반영되어 있다. 
```text
// test
├── FmApplicationTests.java
├── application
│   └── controller
│       └── FinancialModelControllerTest.java
└── domain
    ├── calculator
    │   ├── account
    │   └── financial
    │       ├── NewtonRaphsonAndPoiCompareTest.java
    │       ├── NewtonRaphsonIRRCalculatorIRRTest.java
    │       └── helper
    └── finder
        └── PurchasePriceFinderTest.java
```

`개인의 리포지토리에 public 으로 공개하는 것은 프로젝트 리더의 허가를 받았다.`