package org.fintech.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ValuteNotFoundException extends RuntimeException {
    private String valuteCode;
    public ValuteNotFoundException(String code) {
        super(String.format("Valute with code %s was not found", code));
        this.valuteCode=code;
    }
}
