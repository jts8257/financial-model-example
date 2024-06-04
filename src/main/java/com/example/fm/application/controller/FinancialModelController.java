package com.example.fm.application.controller;

import com.example.fm.application.dto.PriceCalculationRequest;
import com.example.fm.application.dto.SaleCalculationRequest;
import com.example.fm.application.service.FinancialModelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class FinancialModelController {

    private final FinancialModelService financialModelService;

    @PostMapping("/finance/price")
    public ResponseEntity<BigDecimal> calculateProperPurchasePrice(
            @Valid @RequestBody PriceCalculationRequest requestBody) {

        BigDecimal properPurchasePrice = financialModelService.findProperPurchasePriceBy(requestBody);

        return ResponseEntity.ok(properPurchasePrice);
    }

    @PostMapping("/finance/sale")
    public ResponseEntity<BigDecimal> calculateProperSale(
            @Valid @RequestBody SaleCalculationRequest requestBody) {

        BigDecimal properSale = financialModelService.findProperSaleBy(requestBody);

        return ResponseEntity.ok(properSale);
    }
}
