package it.avbo.dilaxia.api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // 'user' è una parola ristretta
public class User {
    @Id
    @Column(unique = true)
    private String username;
    private byte[] passwordDigest;
    private byte[] salt;

    public User() {
    }

    public User(String username, byte[] passwordDigest, byte[] salt) {
        this.username = username;
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
}
