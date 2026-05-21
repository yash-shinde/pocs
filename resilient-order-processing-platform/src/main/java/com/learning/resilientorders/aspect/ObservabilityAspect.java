package com.learning.resilientorders.aspect;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ObservabilityAspect {

    private static final Logger log = LoggerFactory.getLogger(ObservabilityAspect.class);

    private final MeterRegistry meterRegistry;

    public ObservabilityAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Around("@annotation(monitoredOperation)")
    public Object around(ProceedingJoinPoint joinPoint, MonitoredOperation monitoredOperation) throws Throwable {
        String operationName = monitoredOperation.value().isBlank()
            ? joinPoint.getSignature().toShortString()
            : monitoredOperation.value();

        Timer.Sample sample = Timer.start(meterRegistry);
        long startNanos = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
            meterRegistry.counter("orders.operation.success", "operation", operationName).increment();
            sample.stop(meterRegistry.timer("orders.operation.duration", "operation", operationName, "status", "success"));
            log.info("operation={} status=success durationMs={} correlationId={}", operationName, durationMs, MDC.get("correlationId"));
            return result;
        } catch (Throwable ex) {
            Counter counter = meterRegistry.counter("orders.operation.failure", "operation", operationName, "exception", ex.getClass().getSimpleName());
            counter.increment();
            sample.stop(meterRegistry.timer("orders.operation.duration", "operation", operationName, "status", "failure"));
            log.error("operation={} status=failure correlationId={} message={}", operationName, MDC.get("correlationId"), ex.getMessage());
            throw ex;
        }
    }
}
