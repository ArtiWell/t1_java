package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Around("@annotation(Metric) && @annotation(metricAnnotation)")
    public Object trackExecutionTime(ProceedingJoinPoint joinPoint, Metric metricAnnotation) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        if (executionTime > metricAnnotation.value()) {
            String key = UUID.randomUUID().toString();
            String message = String.format("Method %s took %d ms to execute",
                    joinPoint.getSignature().toString(), executionTime);
            kafkaTemplate.send("t1_demo_metrics", key, message);
        }

        return result;
    }
}
