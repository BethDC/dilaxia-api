package it.avbo.dilaxia.api.entities.enums;

public enum UserRole {
    Student('S'),
    Teacher('I'),
    External('E'),
    Admin('A');

    private final char value;
    UserRole(char value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserRole fromValue(char value) {
        for(UserRole role: UserRole.values())
            if(role.getValue() == value)
                return role;
        return null;
    }
}
