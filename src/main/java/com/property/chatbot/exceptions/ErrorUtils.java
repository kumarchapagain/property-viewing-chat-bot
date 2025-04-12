package com.property.chatbot.exceptions;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;

public class ErrorUtils {

  public static Error createError(HttpServletRequest request, String message, Integer code) {
    return Error.builder()
            .code(code)
            .message(message)
            .reqMethod(request.getMethod())
            .url(request.getRequestURL().toString())
            .timestamp(Instant.now())
            .build();
  }

}
