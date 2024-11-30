package org.fintech.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExampleService {

    private final Counter counter;

    public ExampleService(MeterRegistry meterRegistry) {
        counter = meterRegistry.counter("example_service", "name", "custom_metric");
    }

    public void start() {
        try {
            log.info("start...");
            counter.increment();
            Thread.sleep(1000);
            log.info("finish!");
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }
}
