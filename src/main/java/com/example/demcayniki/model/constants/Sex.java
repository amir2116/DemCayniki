package com.example.demcayniki.model.constants;

public enum Sex {
  MALE(1),
  FEMALE(2),
  NOT_PRESENT(0);

  int code;

  Sex(int code) {
    this.code = code;
  }
}
