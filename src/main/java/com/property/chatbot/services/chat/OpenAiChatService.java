package com.property.chatbot.services.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OpenAiChatService implements AiChatService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String getChatResponse(String message) {
        String prompt = "You are assisting in scheduling property viewings between landlords and tenants. " +
                "Here is the user input: " + message;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", Collections.singletonList(
                new HashMap<String, String>() {{
                    put("role", "user");
                    put("content", prompt);
                }}
        ));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions", entity, String.class);
        String responseBody = response.getBody();
        log.debug("AI response: {}", responseBody);
        return extractMessageFromResponse(responseBody);
    }

    private String extractMessageFromResponse(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResponse);
            JsonNode choicesNode = rootNode.path("choices");
            if (choicesNode.isArray() && !choicesNode.isEmpty()) {
                JsonNode contentNode = choicesNode.get(0).path("message").path("content");
                if (contentNode.isTextual()) {
                    String content = contentNode.asText();
                    log.debug("Extracted message: {}", content);
                    return content;
                }
            }
            log.error("No content found in response: {}", jsonResponse);
            throw new IllegalStateException("Invalid response format: no content found");
        } catch (Exception e) {
            log.error("Failed to parse OpenAI response: {}", jsonResponse, e);
            throw new RuntimeException("Error parsing OpenAI response", e);
        }
    }

}
