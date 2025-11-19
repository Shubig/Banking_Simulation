package com.bankingsim.service;

import com.bankingsim.dao.UserDao;
import com.bankingsim.model.User;
import java.util.Optional;
import java.security.MessageDigest;

public class AuthService {
    private final UserDao userDao = new UserDao();

    public User register(String username, String email, String password) throws Exception {
        Optional<User> existing = userDao.findByUsername(username);
        if (existing.isPresent()) throw new Exception("Username exists");
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(hash(password));
        return userDao.insert(u);
    }

    public User login(String username, String password) throws Exception {
        Optional<User> uo = userDao.findByUsername(username);
        if (uo.isEmpty()) throw new Exception("User not found");
        User u = uo.get();
        if (!u.getPasswordHash().equals(hash(password))) throw new Exception("Invalid credentials");
        return u;
    }

    private String hash(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] b = md.digest(s.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }
}
