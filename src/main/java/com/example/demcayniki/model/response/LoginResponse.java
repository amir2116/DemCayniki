package com.example.demcayniki.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;


public record LoginResponse(String accessToken, String tokenType, String username) {

}
