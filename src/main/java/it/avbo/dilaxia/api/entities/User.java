package it.avbo.dilaxia.api.entities;

import it.avbo.dilaxia.api.models.auth.enums.UserRole;
import jakarta.persistence.*;

@Entity
@Table(name = "users") // 'user' è una parola ristretta
public class User {
    @Id
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;

    private UserRole role;
    private byte[] passwordDigest;
    private byte[] salt;

    public User() {
    }

    public User(String username, String email, byte[] passwordDigest, byte[] salt) {
        this.username = username;
        this.email = email;
        this.passwordDigest = passwordDigest;
        this.salt = salt;
    }

    public User(String username, String email, UserRole role, byte[] passwordDigest, byte[] salt) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.passwordDigest = passwordDigest;
        this.salt = salt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPasswordDigest() {
        return passwordDigest;
    }

    public void setPasswordDigest(byte[] passwordDigest) {
        this.passwordDigest = passwordDigest;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
