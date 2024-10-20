package org.fintech.exception;

import lombok.Getter;

@Getter
public class ServiceIsNotAvailableException extends RuntimeException{
    private final String serviceName;
    public ServiceIsNotAvailableException(String serviceName){
        super(String.format("Service: %s - is not available", serviceName));
        this.serviceName = serviceName;
    }
}
