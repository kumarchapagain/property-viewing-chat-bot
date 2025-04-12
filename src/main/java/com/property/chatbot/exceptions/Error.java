package com.property.chatbot.exceptions;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Error {

    private static final long serialVersionUID = 1L;
    /**
     * HTTP status code set by the origin server.
     */
    private Integer code;

    /**
     * Error message.
     */
    private String message;

    /**
     * Url of request that produced the error.
     */
    private String url = "Not available";

    /**
     * Method of request that produced the error.
     */
    private String reqMethod = "Not available";

    /**
     * Timestamp
     */
    private Instant timestamp;

    Map<String, String> errors = new HashMap<>();
}
