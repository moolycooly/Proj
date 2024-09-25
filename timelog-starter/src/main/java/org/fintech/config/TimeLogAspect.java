package org.fintech.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimeLogAspect {
    private final Logger logger = LoggerFactory.getLogger(TimeLogAspect.class);
    @Pointcut("@within(org.fintech.config.Timelog)")
    public void timeLogAnnotationPointCutClasses() {}
    @Pointcut("@annotation(org.fintech.config.Timelog)")
    public void timeLogAnnotationPointCutMethods() {}
    @Around("timeLogAnnotationPointCutClasses() || timeLogAnnotationPointCutMethods()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        logger.info("In Method: {}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        Object object = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        logger.info("Out Method: {} , time: {} ms",joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName(),endTime - startTime);
        return object;
    }
}
