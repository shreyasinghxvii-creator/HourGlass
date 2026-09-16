package com.hourglass.model;

import java.sql.Timestamp;

public class Service {
    private int id;
    private int providerId;
    private String title;
    private String description;
    private String category;
    private String mode;
    private String location;
    private int durationHours;
    private int creditCost;
    private String status;
    private Timestamp createdAt;
    private String providerName;
    private int requestCount;

    public Service() {
    }

    public Service(int id, int providerId, String title, String description, String category, 
                   String mode, String location, int durationHours, int creditCost, 
                   String status, Timestamp createdAt) {
        this.id = id;
        this.providerId = providerId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.mode = mode;
        this.location = location;
        this.durationHours = durationHours;
        this.creditCost = creditCost;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(int durationHours) {
        this.durationHours = durationHours;
    }

    public int getCreditCost() {
        return creditCost;
    }

    public void setCreditCost(int creditCost) {
        this.creditCost = creditCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }
}