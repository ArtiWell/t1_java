package ru.t1.java.demo.aop;


import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.entity.DataSourceErrorLogEntity;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

@Aspect
@Component
@RequiredArgsConstructor
public class LogDataSourceError {
    private final DataSourceErrorLogRepository repository;


    @AfterThrowing(pointcut = "execution(* ru.t1.java.demo.controller.*.*(..))", throwing = "exception")
    public void logDataSourceError(JoinPoint joinPoint, Throwable exception) {
        DataSourceErrorLogEntity log = DataSourceErrorLogEntity.builder()
                .message(exception.getMessage() != null ? exception.getMessage() : "no message")
                .stackTrace(getStackTrace(exception))
                .methodSignature(joinPoint.getSignature().toString())
                .build();
        repository.save(log);
    }

    private String getStackTrace(Throwable throwable) {
        StringBuilder result = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            result.append(element.toString()).append("\n");
        }
        return result.toString();
    }

}
