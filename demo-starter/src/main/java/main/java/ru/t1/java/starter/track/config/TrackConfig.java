package main.java.ru.t1.java.starter.track.config;

import main.java.ru.t1.java.starter.track.aspects.LogAspect;
import main.java.ru.t1.java.starter.track.aspects.TrackingAspect;
import main.java.ru.t1.java.starter.track.validation.DatabaseValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrackConfig {

    @Bean
    public TrackingAspect trackingAspect() {
        return new TrackingAspect();
    }

    @Bean
    public LogAspect logAspect() {
        return new LogAspect();
    }

    @Bean
    public DatabaseValidator databaseValidator() {
        return new DatabaseValidator();
    }
}
