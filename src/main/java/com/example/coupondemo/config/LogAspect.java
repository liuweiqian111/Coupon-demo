package com.example.coupondemo.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Around("execution(* com.example.coupondemo.controller..*.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long start = System.currentTimeMillis();
        String method = point.getSignature().getName();
        String clazz = point.getTarget().getClass().getSimpleName();
        Object result = point.proceed();
        long cost = System.currentTimeMillis() - start;
        log.info("[AOP] {}.{} 耗时: {}ms", clazz, method, cost);
        return result;
    }
}
