package ru.t1.java.demo.model.reqest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientUnblockRequest {
    private Long clientId;
    private String reason;
}
