package org.fintech.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fintech.dto.ConvertCurrencyDto;
import org.fintech.controllers.payload.ConvertCurrencyRequest;
import org.fintech.dto.CurrencyDto;
import org.fintech.services.CurrencyService;
import org.fintech.validator.ValidCurrencyCode;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
@Tag(name = "Currency controller", description = "Get currency by code and convert one currency to another")
@RestController
@RequestMapping(value = "/currencies")
@RequiredArgsConstructor
@Slf4j
@Validated

public class CurrencyRestController {
    private final CurrencyService currencyService;

    @Operation(
            summary = "Get exchange rate",
            description = "Getting exchange rate by currency code from url",
            responses ={
                @ApiResponse(responseCode = "200", description = "Success",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyDto.class))),
                @ApiResponse(responseCode = "400", description = "Unvalid currency code",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                @ApiResponse(responseCode = "404", description = "Currency code was not found",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "503", description = "Cbr is not available",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )

    @GetMapping("/rates/{code}")
    public CurrencyDto getCurrencyRate(@PathVariable("code") @ValidCurrencyCode String code){
        return currencyService.getCurrency(code);
    }


    @Operation(
            summary = "Convert amount of currency to another currency",
            description = "Getting currency codes, amount from request and returns converted amount",
            responses ={
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConvertCurrencyDto.class))),
                    @ApiResponse(responseCode = "400", description = "Unvalid currency codes or amount is 0 or negative",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "404", description = "One of currency codes was not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "503", description = "Cbr is not available",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
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
