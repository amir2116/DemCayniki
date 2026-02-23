package com.example.demcayniki.model.constants.modifiable;

import lombok.Getter;

@Getter
public enum UserStatus {

    ACTIVE(1, "ACTIVE"),
    INACTIVE(0, "INACTIVE"),
    REMOVED(2, "REMOVED"),
    PENDING(3, "PENDING"),
    PENDING_REMOVED(4, "PENDING_REMOVED"),;

    final int code;
    final String name;
    UserStatus(int i, String name) {
        this.code = i;
        this.name = name;
    }

    public static UserStatus lookup(int code) {
        return switch (code) {
            case 0 -> INACTIVE;
            case 1 -> ACTIVE;
            case 2 -> REMOVED;
            case 3 -> PENDING;
            case 4 -> PENDING_REMOVED;
            default -> throw new IllegalArgumentException("Invalid code " + code);
        };
    }
}
