package org.fintech.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerIsNotAvailableException extends  RuntimeException{
    public ServerIsNotAvailableException(){
        super("Server is not available");
    }
}
