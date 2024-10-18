package org.fintech.client.payload;

import java.math.BigDecimal;

public record ConvertCurrencyRequest(String fromCurrency, String toCurrency, BigDecimal amount) {
}
