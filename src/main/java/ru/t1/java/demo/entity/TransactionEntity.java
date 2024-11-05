package ru.t1.java.demo.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Entity
@Setter
@Table(name = "transaction")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "time", nullable = false)
    private Instant time = Instant.now();


    public void setAccount(AccountEntity account) {
        this.account = account;
        account.getTransactions().add(this);
    }
}
