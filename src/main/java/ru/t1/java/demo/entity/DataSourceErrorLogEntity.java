package ru.t1.java.demo.entity;


import jakarta.persistence.*;
import lombok.*;


@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "data_source_error_log")
public class DataSourceErrorLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stack_trace")
    private String stackTrace;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "method_signature", nullable = false)
    private String methodSignature;

}
