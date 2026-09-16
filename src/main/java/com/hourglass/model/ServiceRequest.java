package com.hourglass.model;

import java.sql.Timestamp;

public class ServiceRequest {
    private int id;
    private int serviceId;
    private int requesterId;
    private String status;
    private Timestamp requestedAt;
    private Timestamp acceptedAt;
    private Timestamp completedAt;
    private Timestamp verifiedAt;

    // Display fields for UI rendering
    private String serviceTitle;
    private String providerName;
    private String requesterName;
    private int creditCost;

    // PHASE 4B CHANGE: Field to track if time_transactions record exists
    private boolean transferred;

    public ServiceRequest() {
    }

    public ServiceRequest(int id, int serviceId, int requesterId, String status, 
                          Timestamp requestedAt, Timestamp acceptedAt, 
                          Timestamp completedAt, Timestamp verifiedAt) {
        this.id = id;
        this.serviceId = serviceId;
        this.requesterId = requesterId;
        this.status = status;
        this.requestedAt = requestedAt;
        this.acceptedAt = acceptedAt;
        this.completedAt = completedAt;
        this.verifiedAt = verifiedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Timestamp requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Timestamp getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Timestamp acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }

    public Timestamp getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Timestamp verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public int getCreditCost() {
        return creditCost;
    }

    public void setCreditCost(int creditCost) {
        this.creditCost = creditCost;
    }

    // PHASE 4B CHANGE: Getter and Setter for transferred status
    public boolean isTransferred() {
        return transferred;
    }

    public void setTransferred(boolean transferred) {
        this.transferred = transferred;
    }
}