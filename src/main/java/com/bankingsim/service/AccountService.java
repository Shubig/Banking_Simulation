package com.bankingsim.service;

import com.bankingsim.dao.AccountDao;
import com.bankingsim.model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AccountService {

    private final AccountDao dao = new AccountDao();

    // ---------------------------------------------------------
    // CREATE ACCOUNT
    // ---------------------------------------------------------
    public Account createAccount(int userId, String accountType, BigDecimal initialDeposit) throws Exception {
        Account a = new Account();
        a.setUserId(userId);
        a.setAccountType(accountType);
        a.setBalance(initialDeposit);
        a.setThreshold(new BigDecimal("500.00"));
        return dao.insert(a);
    }

    // ---------------------------------------------------------
    // GET ACCOUNT (Optional)
    // ---------------------------------------------------------
    public Optional<Account> getAccount(int accNo) throws Exception {
        return dao.findByAccountNumber(accNo);
    }

    // ---------------------------------------------------------
    // GET ALL ACCOUNTS OF USER
    // ---------------------------------------------------------
    public List<Account> getAccountsForUser(int userId) throws Exception {
        return dao.findByUserId(userId);
    }

    // ---------------------------------------------------------
    // UPDATE BALANCE
    // ---------------------------------------------------------
    public void updateBalance(int accNo, BigDecimal newBalance) throws Exception {
        dao.updateBalance(accNo, newBalance);
    }

    // ---------------------------------------------------------
    // UPDATE THRESHOLD
    // ---------------------------------------------------------
    public void updateThreshold(int accNo, BigDecimal threshold) throws Exception {
        dao.updateThreshold(accNo, threshold);
    }

    // ---------------------------------------------------------
    // ⭐ NEW → GET BALANCE OF ACCOUNT
    // ---------------------------------------------------------
    public BigDecimal getBalance(int accNo) throws Exception {
        Optional<Account> opt = dao.findByAccountNumber(accNo);

        if (opt.isEmpty()) {
            throw new Exception("Account not found: " + accNo);
        }

        return opt.get().getBalance();
    }
}
