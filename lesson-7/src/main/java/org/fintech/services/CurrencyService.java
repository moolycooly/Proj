package org.fintech.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fintech.client.CbrClient;
import org.fintech.dto.CurrencyDto;
import org.fintech.dto.Valute;
import org.fintech.exceptions.ServerIsNotAvailableException;
import org.fintech.exceptions.ValuteNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {
    private final CbrClient cbrClient;

    @Value("${cbr.scale-convertation}")
    private Integer scaleConveration;

    public CurrencyDto getCurrency(String currencyCode) {
        List<Valute> valuteList = cbrClient
                .getValCurs()
                .orElseThrow(ServerIsNotAvailableException::new)
                .getValuteList();

        return valuteList.stream()
                .filter(valute -> valute.getCharCode().equals(currencyCode))
                .map(valute -> CurrencyDto
                        .builder()
                        .currency(valute.getCharCode())
                        .rate(valute.getRate())
                        .build())
                .findFirst()
                .orElseThrow(()-> new ValuteNotFoundException(currencyCode));

    }
    public BigDecimal convertCurrency(String fromCurrencyCode, String toCurrencyCode, BigDecimal amount) {

        Valute fromValute = cbrClient
                .getValuteByCode(fromCurrencyCode)
                .orElseThrow(()->new ValuteNotFoundException(fromCurrencyCode));
        Valute toValute = cbrClient
                .getValuteByCode(toCurrencyCode)
                .orElseThrow(()->new ValuteNotFoundException(fromCurrencyCode));

        return amount.multiply(fromValute.getRate()).divide(toValute.getRate(),scaleConveration, RoundingMode.HALF_UP);


    }
}
