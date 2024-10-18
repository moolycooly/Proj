package org.fintech.client.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConvertCurrencyResponse {
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal convertedAmount;
}
