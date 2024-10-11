package org.fintech.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Builder
@Data
public class ServerIsNotAvaibale {
    private String message;
    private HttpStatus code;

}
