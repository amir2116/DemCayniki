package com.example.demcayniki.model.requests;

import static com.example.demcayniki.model.constants.DateConstants.DATE_TIME_FORMAT_EXAMPLE;
import static com.example.demcayniki.model.constants.DateConstants.DATE_TIME_FORMAT_PATTERN;
import static com.example.demcayniki.model.constants.DateConstants.STRING_DATA_TYPE;

import com.example.demcayniki.config.CustomLocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

  @NotBlank
  @NotNull
  @Size(min = 1, max = 100)
  private String name;

  @NotBlank
  @NotNull
  @Size(min = 1, max = 100)
  private String lastName;

  @NotBlank
  @NotNull
  @Size(min = 1, max = 100)

  @NotNull(message = "{validation.sa.person.create.activationDate.not-null}")
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  @Schema(type = STRING_DATA_TYPE, example = DATE_TIME_FORMAT_EXAMPLE, format = DATE_TIME_FORMAT_PATTERN)
  private LocalDateTime birthDate;

  @NumberFormat
  @NotNull
  @Size(min = 1, max = 100)
  int age;

  @NotBlank
  @Email
  @Size(max = 320)
  private String email;

  @NotNull
  @NotBlank
  @Size(min = 8, max = 100)
  private String password;
}
