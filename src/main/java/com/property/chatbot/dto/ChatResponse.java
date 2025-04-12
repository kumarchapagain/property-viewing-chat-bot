package com.property.chatbot.dto;

import com.property.chatbot.entities.appointment.Appointment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String message;
    private boolean isInfoGathered;
    private Appointment appointment;
}
