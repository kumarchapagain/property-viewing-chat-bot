package com.property.chatbot.entities.appointment;

import com.property.chatbot.utils.appointment.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;

    private LocalDateTime appointmentDateTime;

    private String propertyAddress;

    private String landlordName;
    private String landlordEmail;
    private String landlordPhone;

    private String tenantName;
    private String tenantEmail;
    private String tenantPhone;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private boolean isReminderSent;

    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;

    @PrePersist
    protected void onCreate() {
        this.dateCreated = LocalDateTime.now();
        this.dateUpdated = LocalDateTime.now();
        if (this.status == null) {
            this.status = AppointmentStatus.PENDING;
        }
        this.isReminderSent = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateUpdated = LocalDateTime.now();
    }
}
