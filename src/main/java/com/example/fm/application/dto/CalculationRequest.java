package com.example.fm.application.dto;

import com.example.fm.domain.fmodel.FinancialModel;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CalculationRequest {


    @Min(value = 1, message = "모델의 고려 기간은 1 이상이어야합니다.")
    @Max(value = 365, message = "모델의 고려 기간은 365를 넘을 수 없습니다.")
    private int period;

    private FinancialModel financialModel;

    @DecimalMin(value = "1", message = "발생 매출은 1 이상이어야 합니다.")
    @DecimalMax(value = "0x7fffffff", message = "단일 기간 발생매출은 2,147,483,647 이하여야 합니다.")
    private BigDecimal saleSource;
}
