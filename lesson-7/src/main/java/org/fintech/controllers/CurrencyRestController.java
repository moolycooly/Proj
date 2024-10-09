package org.fintech.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fintech.dto.ConvertCurrencyDto;
import org.fintech.controllers.payload.ConvertCurrencyRequest;
import org.fintech.dto.CurrencyDto;
import org.fintech.services.CurrencyService;
import org.fintech.validator.ValidCurrencyCode;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/currencies")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CurrencyRestController {
    private final CurrencyService currencyService;
    @GetMapping("/rates/{code}")
    public CurrencyDto getCurrencyRate(@PathVariable("code") @ValidCurrencyCode String code){
        return currencyService.getCurrency(code);
    }
    @PostMapping("/convert")
    public ConvertCurrencyDto convertCurrency(@RequestBody @Valid ConvertCurrencyRequest convertCurrency) {
        BigDecimal convertedValue = currencyService.convertCurrency
                (convertCurrency.getFromCurrency(),convertCurrency.getToCurrency(),convertCurrency.getAmount());
        return ConvertCurrencyDto
                .builder()
                .fromCurrency(convertCurrency.getFromCurrency())
                .toCurrency(convertCurrency.getToCurrency())
                .convertedAmount(convertedValue)
                .build();
    }
}
