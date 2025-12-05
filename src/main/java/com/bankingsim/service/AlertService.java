package com.bankingsim.service;

import com.bankingsim.dao.AccountDao;
import com.bankingsim.model.Account;
import java.util.*;
import java.util.concurrent.*;

public class AlertService {
    private final AccountDao accountDao = new AccountDao();
    private final EmailService emailService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public AlertService(EmailService emailService) { this.emailService = emailService; }


    public void start(long initialDelaySec, long periodSec) {
        scheduler.scheduleAtFixedRate(this::scan, initialDelaySec, periodSec, TimeUnit.SECONDS);
    }

    public void stop() { scheduler.shutdownNow(); }

    private void scan() {
        try {
            List<Account> all = accountDao.findAll(); // you can add this method to AccountDao
            for (Account a : all) {
                if (a.getBalance().compareTo(a.getThreshold()) < 0) {
                    emailService.send( new com.bankingsim.dao.UserDao().findById(a.getUserId()).get().getEmail(),
                            "Low Balance AutoAlert", "Account " + a.getAccountNumber() + " balance is low: ₹" + a.getBalance());
                }
            }
        } catch (Exception e) {
            System.err.println("AlertService scan failed: " + e.getMessage());
        }
    }
}
