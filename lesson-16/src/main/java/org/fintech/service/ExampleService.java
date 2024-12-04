package org.fintech.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public void getOutOfMemory() {

        List<Integer> list = new ArrayList<>();
        while(true) {
            list.add(1);
        }
    }

    public void getStackOverflow() {
        getStackOverflow();
    }
}
