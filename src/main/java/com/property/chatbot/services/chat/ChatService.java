package com.property.chatbot.services.chat;

import com.property.chatbot.dto.ChatResponse;
import com.property.chatbot.entities.appointment.Appointment;
import com.property.chatbot.entities.user.User;
import com.property.chatbot.services.appointment.AppointmentService;
import com.property.chatbot.services.user.CustomUserDetailsService;
import com.property.chatbot.utils.appointment.AppointmentParser;
import com.property.chatbot.utils.appointment.AppointmentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

import static com.property.chatbot.utils.appointment.AppointmentParser.isAppointmentInfoComplete;


@Service
@Slf4j
public class ChatService {

    private final AiChatService aiChatService;
    private final AppointmentService appointmentService;
    private final CustomUserDetailsService userDetailsService;

    public ChatService(AiChatService aiChatService, AppointmentService appointmentService, CustomUserDetailsService userDetailsService) {
        this.aiChatService = aiChatService;
        this.appointmentService = appointmentService;
        this.userDetailsService = userDetailsService;
    }

    public ChatResponse processMessage(String message) {
        log.info("Processing user message: {}", message);
        User currentUser = getCurrentUser();
        Appointment appointment = appointmentService.getAppointmentByUser(currentUser);
        if (appointment == null) {
           appointment = new Appointment();
           appointment.setUser(currentUser);
        }
        Appointment extractedAppointment = AppointmentParser.extractAppointmentInformation(message, appointment);

        if (extractedAppointment != null) {
            ChatResponse chatResponse = null;
            if (isAppointmentInfoComplete(extractedAppointment)) {
                extractedAppointment.setStatus(AppointmentStatus.COMPLETED);
                String confirmationMessage = generateAppointmentConfirmation(extractedAppointment);
                chatResponse = ChatResponse.builder().message(confirmationMessage).isInfoGathered(true).appointment(extractedAppointment).build();
            } else {
                extractedAppointment.setStatus(AppointmentStatus.PENDING);
                String missingInfoPrompt = generateMissingInfoPrompt(extractedAppointment);
                chatResponse = ChatResponse.builder().message(missingInfoPrompt).isInfoGathered(false).appointment(extractedAppointment).build();
            }
            appointmentService.saveAppointment(extractedAppointment);
            return chatResponse;
        }
        String aiResponse = aiChatService.getChatResponse(message);
        log.debug("AI response: {}", aiResponse);

        // Return AI response if no appointment info was extracted
        return new ChatResponse(aiResponse, false, null);
    }

    private String generateMissingInfoPrompt(Appointment appointment) {
        StringBuilder prompt = new StringBuilder("Need a bit more information to schedule your property viewing appointment:\n\n");

        if (appointment.getAppointmentDateTime() == null) {
            prompt.append("- When would you like to schedule the viewing? (date and time)\n");
        }

        if (appointment.getPropertyAddress() == null || appointment.getPropertyAddress().isEmpty()) {
            prompt.append("- What is the property address for the viewing?\n");
        }

        if (appointment.getLandlordName() == null || appointment.getLandlordName().isEmpty()) {
            prompt.append("- What is the landlord's name?\n");
        }

        if (appointment.getLandlordEmail() == null || appointment.getLandlordEmail().isEmpty()) {
            prompt.append("- What is the landlord's email address?\n");
        }

        if (appointment.getTenantName() == null || appointment.getTenantName().isEmpty()) {
            prompt.append("- What is the tenant's name?\n");
        }

        if (appointment.getTenantEmail() == null || appointment.getTenantEmail().isEmpty()) {
            prompt.append("- What is the tenant's email address?\n");
        }

        prompt.append("\nPlease provide the missing details so I can complete your appointment scheduling.");
        return prompt.toString();
    }

    private String generateAppointmentConfirmation(Appointment appointment) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        return String.format(
                "Great! I've scheduled a property viewing appointment:\n\n" +
                        "Date: %s\n" +
                        "Time: %s\n" +
                        "Property: %s\n\n" +
                        "Landlord: %s (%s)\n" +
                        "Tenant: %s (%s)\n\n" ,
                appointment.getAppointmentDateTime().format(dateFormatter),
                appointment.getAppointmentDateTime().format(timeFormatter),
                appointment.getPropertyAddress(),
                appointment.getLandlordName(), appointment.getLandlordEmail(),
                appointment.getTenantName(), appointment.getTenantEmail(),
                appointment.getId()
        );
    }

    private User getCurrentUser() {
        return userDetailsService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
