package com.property.chatbot.services.appointment;

import com.property.chatbot.entities.appointment.Appointment;
import com.property.chatbot.utils.appointment.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {

    public List<Appointment> getAllAppointments();
    public Optional<Appointment> getAppointmentById(Long id);
    public Appointment saveAppointment(Appointment appointment);
    public Optional<Appointment> updateAppointmentStatus(Long id, AppointmentStatus status);
    public void markReminderSent(Long id);
    public void sendReminders(LocalDateTime start, LocalDateTime end);

}
