package com.property.chatbot.services.reminder;

import com.property.chatbot.services.appointment.AppointmentService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ReminderService {

    private final AppointmentService appointmentService;

    public ReminderService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void sendReminders() {

        log.info("Checking Appointments for reminders");

        LocalDateTime oneHourDateTime = LocalDateTime.now().plusHours(1);
        appointmentService.sendReminders(oneHourDateTime.minusMinutes(1), oneHourDateTime);
    }

}
