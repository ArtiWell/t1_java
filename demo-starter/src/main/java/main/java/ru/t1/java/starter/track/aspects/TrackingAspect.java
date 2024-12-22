package main.java.ru.t1.java.starter.track.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TrackingAspect {

    @Pointcut("@annotation(main.java.ru.t1.java.starter.track.annotation.TrackTime)")
    public void trackTimeMethods() { }

    @Around("trackTimeMethods()")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Method {} executed in {} ms", joinPoint.getSignature(), executionTime);
        }
    }
}
