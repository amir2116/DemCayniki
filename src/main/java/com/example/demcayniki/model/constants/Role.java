package com.example.demcayniki.model.constants;

public enum Role {

    ROLE_SYSTEM(5, "ROLE_SYSTEM"),
    ROLE_SUPER_ADMIN(4, "ROLE_SUPER_ADMIN"),
    ROLE_ADMIN(3, "ROLE_ADMIN"),
    ROLE_MODERATOR(2, "ROLE_MODERATOR"),
    ROLE_SUPPORT(1, "ROLE_SUPPORT"),
    ROLE_USER(0, "ROLE_USER");

    final int code;
    final String name;


    Role(int i, String roleAdmin) {
        this.code = i;
        this.name = roleAdmin;
    }

    public static Role lookup(int code) {
        return switch (code) {
            case 0 -> ROLE_USER;
            case 1 -> ROLE_SUPPORT;
            case 2 -> ROLE_MODERATOR;
            case 3 -> ROLE_ADMIN;
            case 4 -> ROLE_SUPER_ADMIN;
            case 5 -> ROLE_SYSTEM;
            default -> throw new IllegalArgumentException("Invalid code " + code);
        };
    }
}
