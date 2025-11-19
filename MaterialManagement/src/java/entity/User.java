package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class User {

    private int userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phone;
    private String avatar;
    private int roleId;
    private String roleName;
    private Integer departmentId;
    private String departmentName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private Status status;
    private boolean mfaEnabled;
    private String mfaSecret;
    private String mfaRecoveryCodes;
    private LocalDateTime lastLogin;
    private int failedLoginAttempts;
    private LocalDate passwordExpiry;
    private LocalDateTime createdAt;
    private Integer createdBy;
    private LocalDateTime updatedAt;
    private Integer updatedBy;
    private LocalDateTime deletedAt;

    public User() {
    }

    public User(User other) {
        this.userId = other.userId;
        this.username = other.username;
        this.passwordHash = other.passwordHash;
        this.fullName = other.fullName;
        this.email = other.email;
        this.phone = other.phone;
        this.avatar = other.avatar;
        this.roleId = other.roleId;
        this.roleName = other.roleName;
        this.departmentId = other.departmentId;
        this.departmentName = other.departmentName;
        this.dateOfBirth = other.dateOfBirth;
        this.gender = other.gender;
        this.status = other.status;
        this.mfaEnabled = other.mfaEnabled;
        this.mfaSecret = other.mfaSecret;
        this.mfaRecoveryCodes = other.mfaRecoveryCodes;
        this.lastLogin = other.lastLogin;
        this.failedLoginAttempts = other.failedLoginAttempts;
        this.passwordExpiry = other.passwordExpiry;
        this.createdAt = other.createdAt;
        this.createdBy = other.createdBy;
        this.updatedAt = other.updatedAt;
        this.updatedBy = other.updatedBy;
        this.deletedAt = other.deletedAt;
    }

    public enum Gender {
        male, female, other
    }

    public enum Status {
        active, inactive, locked
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Compatibility getter for legacy code expecting getPassword().
     */
    public String getPassword() {
        return passwordHash;
    }

    /**
     * Compatibility setter for legacy code expecting setPassword().
     */
    public void setPassword(String password) {
        this.passwordHash = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Compatibility getter for legacy code expecting getPhoneNumber().
     */
    public String getPhoneNumber() {
        return phone;
    }

    /**
     * Compatibility setter for legacy code expecting setPhoneNumber().
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phone = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Compatibility getter for legacy code expecting getUserPicture().
     */
    public String getUserPicture() {
        return avatar;
    }

    /**
     * Compatibility setter for legacy code expecting setUserPicture().
     */
    public void setUserPicture(String userPicture) {
        this.avatar = userPicture;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    public void setMfaEnabled(boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }

    public String getMfaSecret() {
        return mfaSecret;
    }

    public void setMfaSecret(String mfaSecret) {
        this.mfaSecret = mfaSecret;
    }

    public String getMfaRecoveryCodes() {
        return mfaRecoveryCodes;
    }

    public void setMfaRecoveryCodes(String mfaRecoveryCodes) {
        this.mfaRecoveryCodes = mfaRecoveryCodes;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public LocalDate getPasswordExpiry() {
        return passwordExpiry;
    }

    public void setPasswordExpiry(LocalDate passwordExpiry) {
        this.passwordExpiry = passwordExpiry;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
