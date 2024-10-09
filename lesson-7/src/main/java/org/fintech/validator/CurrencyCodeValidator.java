package org.fintech.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Currency;
@Slf4j
public class CurrencyCodeValidator implements ConstraintValidator<ValidCurrencyCode, String> {
    @Override
    public boolean isValid(String code, ConstraintValidatorContext constraintValidatorContext) {
        try{
            Currency.getInstance(code);
            return true;
        }
        catch (NullPointerException | IllegalArgumentException e ) {
            return false;
        }
    }
}
