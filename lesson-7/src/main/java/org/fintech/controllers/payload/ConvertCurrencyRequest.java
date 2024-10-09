package org.fintech.controllers.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fintech.validator.ValidCurrencyCode;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvertCurrencyRequest{
    @NotNull
    @ValidCurrencyCode
    private String fromCurrency;
    @NotNull
    @ValidCurrencyCode
    private String toCurrency;
    @NotNull
    @Positive
    private BigDecimal amount;
}
