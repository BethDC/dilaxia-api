package it.avbo.dilaxia.api.models.auth.enums;

public enum UserRole {
    Student(0),
    Teacher(1);

    private int value;
    UserRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserRole fromValue(int value) {
        for(UserRole role: UserRole.values())
            if(role.getValue() == value)
                return role;
        return null;
    }
}
