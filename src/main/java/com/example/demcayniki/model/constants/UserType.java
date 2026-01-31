package com.example.demcayniki.model.constants;

public enum UserType {
    CONSUMER(0, "CONSUMER"),
    INFLUNCER(1, "INFLUNCER"),
    AMETOURSPECIALIST(2, "AMETOURSPECIALIST"),
    MEDIAFAMOUS(3, "MEDIAFAMOUS"),
    PREFERRENCEFAMOUS(4, "PREFERRENCEFAMOUS"),
    SPECIALIST(5, "SPECIALIST");

    final int code;
    final String name;

    UserType(int i, String name) {
        this.code = i;
        this.name = name;
    }

    public static UserType lookup(int code) {
        return switch (code) {
            case 0 -> CONSUMER;
            case 1 -> INFLUNCER;
            case 2 -> AMETOURSPECIALIST;
            case 3 -> MEDIAFAMOUS;
            case 4 -> PREFERRENCEFAMOUS;
            case 5 -> SPECIALIST;
            default -> throw new IllegalArgumentException("Invalid code " + code);
        };
    }
}