package org.fintech.controllers.payload;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "Convert currency payload", description = "Payload for convert currency to another")
public class ConvertCurrencyRequest{
    @Schema(description = "Source currency code", example = "USD")
    @NotNull(message = "Code must be not null")
    @ValidCurrencyCode
    private String fromCurrency;

    @NotNull(message = "Code must be not null")
    @ValidCurrencyCode
    @Schema(description = "Target currency code", example = "TRY")
    private String toCurrency;

    @NotNull(message = "Amount must be not null")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Amount of sourse currency ", example = "100.5")
    private BigDecimal amount;
}
