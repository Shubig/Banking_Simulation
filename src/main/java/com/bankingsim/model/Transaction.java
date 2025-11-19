package com.bankingsim.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private String transactionId;
    private Integer fromAccount; // nullable
    private Integer toAccount;   // nullable
    private BigDecimal amount;
    private String type;
    private LocalDateTime createdAt;

    public Transaction() {}
    public Transaction(String transactionId, Integer fromAccount, Integer toAccount, BigDecimal amount, String type) {
        this.transactionId = transactionId; this.fromAccount = fromAccount; this.toAccount = toAccount;
        this.amount = amount; this.type = type;
    }
    // getters/setters
    public String getTransactionId() { return transactionId; }
    public Integer getFromAccount() { return fromAccount; }
    public Integer getToAccount() { return toAccount; }
    public BigDecimal getAmount() { return amount; }
    public String getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
