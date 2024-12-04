package org.fintech.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fintech.service.ExampleService;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ExampleController {

    private final ExampleService exampleService;

    @GetMapping
    public void example() {
        var uuid = UUID.randomUUID().toString();
        try(var mdc = MDC.putCloseable("requestId", uuid)) {
            log.info("Start processing");
            exampleService.start();
            log.info("Task completed!");
        }
    }

    @GetMapping("/stack-overflow")
    public void getStackOverflow() {
        exampleService.getStackOverflow();
    }

    @GetMapping("/out-of-memory")
    public void getOutOfMemoryError() {
        exampleService.getOutOfMemory();
    }
}
