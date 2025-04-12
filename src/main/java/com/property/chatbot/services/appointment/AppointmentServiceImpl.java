package com.property.chatbot.services.appointment;

import com.property.chatbot.entities.appointment.Appointment;
import com.property.chatbot.repositories.appointment.AppointmentRepository;
import com.property.chatbot.utils.appointment.AppointmentStatus;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    @Override
    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Override
    public Optional<Appointment> updateAppointmentStatus(Long id, AppointmentStatus status) {
        return appointmentRepository.findById(id)
                .map(appointment -> {
                    appointment.setStatus(status);
                    log.info("Appointment Updated {} status to {}", id, status);
                    return appointmentRepository.save(appointment);
                });
    }

    @Override
    public void markReminderSent(Long id) {
        appointmentRepository.findById(id).ifPresent(appointment -> {
            appointment.setReminderSent(true);
            appointmentRepository.save(appointment);
            log.info("Marked reminder as sent for appointment {}", id);
        });
    }

    @Override
    public void sendReminders(LocalDateTime start, LocalDateTime end) {
        appointmentRepository.findAppointmentsForReminders(start, end)
                .forEach(appointment -> {
                    log.info("Reminder for upcoming appointment at {} for property: {}, Landlord: {}, Tenant: {}",
                            appointment.getAppointmentDateTime(), appointment.getPropertyAddress(), appointment.getLandlordName(), appointment.getTenantName());
                    appointment.setReminderSent(true);
                    appointmentRepository.save(appointment);
                });
    }
}
