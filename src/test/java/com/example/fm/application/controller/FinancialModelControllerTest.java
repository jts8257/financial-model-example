package com.example.fm.application.controller;

import com.example.fm.application.dto.PriceCalculationRequest;
import com.example.fm.application.service.FinancialModelService;
import com.example.fm.domain.fmodel.FinancialModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@WebMvcTest(FinancialModelController.class)
public class FinancialModelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FinancialModelService financialModelService;

    @Test
    public void validRequest() throws Exception {
        PriceCalculationRequest requestBody = new PriceCalculationRequest();
        requestBody.setFinancialModel(FinancialModel.PRICE_MODEL_1);
        requestBody.setPeriod(5);
        requestBody.setSaleSource(BigDecimal.valueOf(10000));

        BigDecimal expectedPrice = BigDecimal.valueOf(123456.78);
        Mockito.when(financialModelService.findProperPurchasePriceBy(Mockito.any(PriceCalculationRequest.class)))
                .thenReturn(expectedPrice);

        mockMvc.perform(MockMvcRequestBuilders.post("/finance/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedPrice.toString()));
    }


    @Test
    public void wrongRequest_saleSource() throws Exception {
//        이런식으로 작성하면 Validation Exception 에 대해서도 테스트가 가능함, 하지만 이렇게 처리하기 위해서는 @ExceptionHandler 를 별도로 작성해야 하고
//        이는 domain 영역을 소개하는 이 프로젝트 목적에서 어긋나기 때문에 추가로 작성하지 않음.

//        CalculationRequest requestBody = new CalculationRequest();
//        requestBody.setFinancialModel(FinancialModel.MODEL_1);
//        requestBody.setPeriod(5);
//        requestBody.setSaleSource(BigDecimal.valueOf(-10000));
//
//        BigDecimal expectedPrice = BigDecimal.valueOf(123456.78);
//        Mockito.when(financialModelService.findPurchasePriceBy(Mockito.any(CalculationRequest.class)))
//                .thenReturn(expectedPrice);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/finance/price")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(requestBody)))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}