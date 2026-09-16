package com.hourglass.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String email;
    private String password;
    private double timeCreditBalance;
    private Timestamp createdAt;

    public User() {
        this.timeCreditBalance = 1.0;
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.timeCreditBalance = 1.0;
    }

    public User(int id, String name, String email, String password, double timeCreditBalance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.timeCreditBalance = timeCreditBalance;
    }

    // Getters and Setters using 'name'
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public double getTimeCreditBalance() { return timeCreditBalance; }
    public void setTimeCreditBalance(double timeCreditBalance) { this.timeCreditBalance = timeCreditBalance; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // Compatibility methods for 'username' references
    public String getUsername() {
        return name;
    }

    public void setUsername(String username) {
        this.name = username;
    }
}