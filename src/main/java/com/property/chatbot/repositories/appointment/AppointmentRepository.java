package com.property.chatbot.repositories.appointment;

import com.property.chatbot.entities.appointment.Appointment;
import com.property.chatbot.entities.user.User;
import com.property.chatbot.utils.appointment.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByStatus(AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDateTime BETWEEN ?1 AND ?2 AND a.status = ?3 AND a.isReminderSent = false")
    List<Appointment> findAppointmentsForReminders(LocalDateTime start, LocalDateTime end);

    Optional<Appointment> findByUser(User user);
}
