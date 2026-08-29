package com.github.k.genki0913.verify.auth;

import java.time.LocalDateTime;

import com.github.k.genki0913.verify.common.entity.AuditMetadata;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String role;

    @Column(nullable = false)
    private final boolean enabled = true;

    @Column(nullable = false)
    private Integer failedAttempts = 0;

    @Column(nullable = false)
    private boolean accountLocked = false;

    private LocalDateTime lockTime;

    private String resetToken;

    private LocalDateTime resetTokenExpiry;

    @Embedded
    private final AuditMetadata audit = new AuditMetadata();

    protected Account() {
    }

    private Account(String email, String password, String name, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.failedAttempts = 0;
        this.accountLocked = false;
    }

    static Account createUser(String email, String password, String name) {
        requireNonBlank(email, "Email");
        requireNonBlank(password, "Password");
        requireNonBlank(name, "Name");

        return new Account(email, password, name, "ROLE_USER");
    }

    static Account createAdmin(String email, String password, String name) {
        requireNonBlank(email, "Email");
        requireNonBlank(password, "Password");
        requireNonBlank(name, "Name");

        return new Account(email, password, name, "ROLE_ADMIN");
    }

    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be null or blank");
        }
    }

    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getName() {
        return this.name;
    }

    public String getRole() {
        return this.role;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public Integer getFailedAttempts() {
        return this.failedAttempts;
    }

    public boolean getAccountLocked() {
        return this.accountLocked;
    }

    public LocalDateTime getLockTime() {
        return this.lockTime;
    }

    public String getResetToken() {
        return this.resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return this.resetTokenExpiry;
    }

    public AuditMetadata getAudit() {
        return this.audit;
    }

    void recordFailedLogin(int maxAttempts) {
        this.failedAttempts++;
        if (this.failedAttempts >= maxAttempts) {
            this.accountLocked = true;
            this.lockTime = LocalDateTime.now();
        }
    }

    @SuppressWarnings("PMD.NullAssignment")
    void resetFailedLogin() {
        this.failedAttempts = 0;
        this.accountLocked = false;
        this.lockTime = null;
    }

    void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    void issuePasswordResetToken(String token, LocalDateTime expiry) {
        this.resetToken = token;
        this.resetTokenExpiry = expiry;
    }
}
