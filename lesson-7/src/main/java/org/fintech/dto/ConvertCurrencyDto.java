package org.fintech.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "Converted currency", description = "response")
public class ConvertCurrencyDto {
    @Schema(example = "USD")
    private String fromCurrency;
    @Schema(example = "TRY")
    private String toCurrency;
    @Schema(example = "3595.3194")
    private BigDecimal convertedAmount;
}

