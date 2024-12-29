package ru.t1.java.demo.model.reqest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountUnblockRequest {
    private Long accountId;
    private String reason;
}
