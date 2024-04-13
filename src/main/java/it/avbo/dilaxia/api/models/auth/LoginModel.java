package it.avbo.dilaxia.api.models.auth;

import com.google.gson.annotations.SerializedName;

public class LoginModel {
    @SerializedName("identifier")
    private String identifier;
    @SerializedName("password")
    private String password;

    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }
}
