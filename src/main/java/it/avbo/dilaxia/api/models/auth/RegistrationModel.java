package it.avbo.dilaxia.api.models.auth;

public class RegistrationModel {
    private String username;

    private String email;

    private char sex;

    private String birthday;

    private String password;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
