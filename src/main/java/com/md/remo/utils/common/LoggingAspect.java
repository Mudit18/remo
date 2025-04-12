package com.md.remo.utils.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    // Define a pointcut for methods in all controllers
    @Pointcut("execution(public * com.md.remo.controller..*.*(..))")
    public void transactionMethods() {}

    @Around("transactionMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.info("{} succeeded in {}ms", joinPoint.getSignature().toShortString(), (endTime - startTime));
            return result;
        } catch (Exception ex) {
            long endTime = System.currentTimeMillis();
            log.error("{} failed in {}ms. Exception: {}", joinPoint.getSignature().toShortString(), (endTime - startTime), ex.getMessage());
            throw ex;
        }
    }
}