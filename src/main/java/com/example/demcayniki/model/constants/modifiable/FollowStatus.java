package com.example.demcayniki.model.constants.modifiable;

public enum FollowStatus {

    PENDING(0,  "PENDING"),
    ACCEPTED(1, "ACCEPTED"),
    REJECTED(2, "REJECTED"),
    BLOCKED(3, "BLOCKED"),;

    final int code;
    final String name;
    FollowStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static FollowStatus lookup(int code) {
        return switch (code) {
            case 0 -> PENDING;
            case 1 -> ACCEPTED;
            case 2 -> REJECTED;
            case 3 -> BLOCKED;
            default -> throw new IllegalArgumentException("Invalid code " + code);
        };
    }
}
