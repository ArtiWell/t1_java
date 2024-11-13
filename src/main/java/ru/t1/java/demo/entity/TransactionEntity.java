package ru.t1.java.demo.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.t1.java.demo.emums.TransactionStatus;

import java.time.Instant;

@Getter
@Entity
@Setter
@Table(name = "transaction")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", unique = true, nullable = false)
    private String transactionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "time", nullable = false)
    private Instant time = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    public void setAccount(AccountEntity account) {
        this.account = account;
        account.getTransactions().add(this);
    }
}
