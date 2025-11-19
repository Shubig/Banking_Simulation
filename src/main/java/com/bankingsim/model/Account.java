package com.bankingsim.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {
    private int accountNumber;
    private int userId;
    private String accountType;
    private BigDecimal balance;
    private BigDecimal threshold;
    private LocalDateTime createdAt;



    public Account() {}
    public Account(int accountNumber, int userId, String accountType, BigDecimal balance, BigDecimal threshold) {
        this.accountNumber = accountNumber; this.userId = userId; this.accountType = accountType;
        this.balance = balance; this.threshold = threshold;
    }
    // getters/setters
    public int getAccountNumber() { return accountNumber; }
    public void setAccountNumber(int accountNumber) { this.accountNumber = accountNumber; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public BigDecimal getThreshold() { return threshold; }
    public void setThreshold(BigDecimal threshold) { this.threshold = threshold; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

}
