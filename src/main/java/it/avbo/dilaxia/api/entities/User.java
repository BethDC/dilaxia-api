package it.avbo.dilaxia.api.entities;

import it.avbo.dilaxia.api.entities.enums.UserRole;

import java.sql.Date;

public class User {
    public final String username;
    public final String email;
    public final char sex;
    public final Date birthday;
    public final UserRole role;
    public final byte[] passwordHash;
    public final byte[] salt;

    public User(String username, String email, char sex, Date birthday, UserRole role, byte[] passwordHash, byte[] salt) {
        this.username = username;
        this.email = email;
        this.sex = sex;
        this.birthday = birthday;
        this.role = role;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }
}
