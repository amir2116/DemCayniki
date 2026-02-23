package com.example.demcayniki.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

  private static final DateTimeFormatter dateTimeFormatter =
      new DateTimeFormatterBuilder()
          .parseStrict()
          .appendPattern("dd.MM.uuuu HH:mm")
          .toFormatter()
          .withResolverStyle(ResolverStyle.STRICT);

  @Override
  public LocalDateTime deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {

    if (parser == null) {
      return null;
    }

    String value = parser.getText();
    if (value == null || value.trim().isEmpty()) {
      return null;
    }

    value = value.trim();

    try {
      return LocalDateTime.parse(value, dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new InvalidFormatException(
          parser,
          "Invalid date format: " + value,
          value,
          LocalDateTime.class
      );
    }
  }
}