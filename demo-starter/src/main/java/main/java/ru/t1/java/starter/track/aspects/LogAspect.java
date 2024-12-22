package main.java.ru.t1.java.starter.track.aspects;

import lombok.extern.slf4j.Slf4j;
import main.java.ru.t1.java.starter.track.annotation.TrackException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @AfterThrowing(pointcut = "@annotation(main.java.ru.t1.java.starter.track.annotation.TrackException)", throwing = "exception")
    public void logException(JoinPoint joinPoint, Exception exception) throws NoSuchMethodException {
        TrackException annotation = joinPoint.getTarget()
                .getClass()
                .getMethod(joinPoint.getSignature().getName(), getClassArray(joinPoint))
                .getAnnotation(TrackException.class);

        if (Arrays.asList(annotation.errors()).contains(exception.getClass())) {
            log.error("Exception in method {}: {}", joinPoint.getSignature(), exception.getMessage());
        }
    }

    private Class<?>[] getClassArray(JoinPoint joinPoint) {
        return Stream.of(joinPoint.getArgs()).map(Object::getClass).toArray(Class<?>[]::new);
    }
}
