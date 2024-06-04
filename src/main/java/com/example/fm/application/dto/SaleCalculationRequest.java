package com.example.fm.application.dto;

import com.example.fm.domain.fmodel.FinancialModel;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleCalculationRequest {


    @Min(value = 1, message = "모델의 고려 기간은 1 이상이어야합니다.")
    @Max(value = 365, message = "모델의 고려 기간은 365를 넘을 수 없습니다.")
    private int period;

    private FinancialModel financialModel;

    @DecimalMin(value = "100000000", message = "구매가격은 1억 이상부터 계산 할 수 있습니다.")
    @DecimalMax(value = "1000000000000000", message = "구매가격은 100조 이하까지만 계산 할 수 있습니다.")
    private BigDecimal purchasePrice;
}
