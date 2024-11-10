package ru.t1.java.demo.aop;


import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.entity.DataSourceErrorLogEntity;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class LogDataSourceError {
    private final DataSourceErrorLogRepository repository;
    private final KafkaTemplate<String,String> kafkaTemplate;


    @AfterThrowing(pointcut = "execution(* ru.t1.java.demo.controller.*.*(..))", throwing = "exception")
    public void logDataSourceError(JoinPoint joinPoint, Throwable exception) {
        String message = exception.getMessage() != null ? exception.getMessage() : "no message";
        String stackTrace = getStackTrace(exception);
        String methodSignature = joinPoint.getSignature().toString();

        String key = UUID.randomUUID().toString();
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>("t1_demo_metrics", key, message);
            record.headers().add("error-type", "DATA_SOURCE".getBytes());
            kafkaTemplate.send(record).get();
        } catch (Exception e) {
            DataSourceErrorLogEntity log = DataSourceErrorLogEntity.builder()
                    .message(message)
                    .stackTrace(stackTrace)
                    .methodSignature(methodSignature)
                    .build();
            repository.save(log);
        }
    }

    private String getStackTrace(Throwable throwable) {
        StringBuilder result = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            result.append(element.toString()).append("\n");
        }
        return result.toString();
    }

}
