package org.fintech.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInit {
    @Autowired
    @Qualifier("baseThreadPool")
    private ExecutorService dataLoaderFixedThreadPool;

    @Autowired
    @Qualifier("baseScheduleThreadPool")
    private ScheduledExecutorService dataInitScheduleThreadPool;

    private final CategoryService categoryService;
    private final LocationService locationService;

    @Value("${data-init.duration}")
    private long duration;

    @Value("${data-init.initial-delay}")
    private long initialDelay;

    @EventListener(ApplicationStartedEvent.class)
    public void init() {
        dataInitScheduleThreadPool.scheduleAtFixedRate(this::initData,initialDelay,duration, TimeUnit.SECONDS);
    }

    public void initData() {

        long start = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(2);
        dataLoaderFixedThreadPool.submit(()-> {
            categoryService.init();
            latch.countDown();
        });
        dataLoaderFixedThreadPool.submit(()-> {
            locationService.init();
            latch.countDown();
        });

        try {
            latch.await();
            long end = System.currentTimeMillis();
            log.info("Time to load data: {}ms",end-start);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
