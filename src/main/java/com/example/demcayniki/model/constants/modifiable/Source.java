package com.example.demcayniki.model.constants.modifiable;

public enum Source {

    SEARCH(0,"SEARCH"),
    TRENDING(1,"TRENDING");

    final int code;
    final String name;

    Source(int i, String name) {
        this.code = i;
        this.name = name;
    }

    public static Source lookup(int code) {
        return switch (code) {
            case 0 -> SEARCH;
            case 1 -> TRENDING;
            default -> throw new IllegalArgumentException("Invalid code " + code);
        };


    }
}
