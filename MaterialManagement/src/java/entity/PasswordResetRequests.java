/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.util.Date;

/**
 *
 * @author Admin
 */
public class PasswordResetRequests {
    private int requestId;
    private String userEmail;
    private Date requestDate;
    private String status;
    private String newPassword;
    private Integer processedBy;
    private Date processedDate;

    public PasswordResetRequests() {}

    public PasswordResetRequests(int requestId, String userEmail, Date requestDate, String status, String newPassword, Integer processedBy, Date processedDate) {
        this.requestId = requestId;
        this.userEmail = userEmail;
        this.requestDate = requestDate;
        this.status = status;
        this.newPassword = newPassword;
        this.processedBy = processedBy;
        this.processedDate = processedDate;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public Integer getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(Integer processedBy) {
        this.processedBy = processedBy;
    }

    public Date getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Date processedDate) {
        this.processedDate = processedDate;
    }
}
