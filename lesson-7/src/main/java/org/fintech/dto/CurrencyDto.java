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
@Schema(name = "Currency")
public class CurrencyDto {
    @Schema(example = "USD")
    private String currency;
    @Schema(example = "98.8")
    private BigDecimal rate;
}
