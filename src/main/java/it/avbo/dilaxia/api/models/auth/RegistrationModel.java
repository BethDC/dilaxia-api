package it.avbo.dilaxia.api.models.auth;

import com.google.gson.annotations.SerializedName;

public class RegistrationModel {
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("sex")
    private char sex;
    @SerializedName("birthday")
    private String birthday;
    @SerializedName("password")
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

    public String getBirthday() {
        return birthday;
    }

    public char getSex() {
        return sex;
    }
}
