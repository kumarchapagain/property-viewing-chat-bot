package com.property.chatbot.utils.appointment;

import com.property.chatbot.entities.appointment.Appointment;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class AppointmentParser {

    public static Appointment extractAppointmentInformation(String message, Appointment appointment) {

        extractDateTime(message, appointment);

        extractPropertyAddress(message, appointment);

        extractContactInfo(message, appointment);

        if (appointment.getAppointmentDateTime() == null &&
                appointment.getPropertyAddress() == null &&
                appointment.getLandlordName() == null &&
                appointment.getTenantName() == null) {
            return null;
        }
        return appointment;
    }

    private static void extractDateTime(String message, Appointment appointment) {
        // Look for date patterns like "on 2023-12-15" or "December 15, 2023"
        Pattern datePattern = Pattern.compile("(?:on|for|at)\\s+(?:(\\d{4}-\\d{2}-\\d{2})|(\\w+\\s+\\d{1,2},?\\s+\\d{4}))");
        Matcher dateMatcher = datePattern.matcher(message);

        if (dateMatcher.find()) {
            String dateStr = dateMatcher.group(1) != null ? dateMatcher.group(1) : dateMatcher.group(2);

            // Look for time patterns like "at 3:00 PM" or "at 15:00"
            Pattern timePattern = Pattern.compile("(?:at|from)\\s+(\\d{1,2}(?::\\d{2})?\\s*(?:am|pm|AM|PM)?|(\\d{2}:\\d{2}))");
            Matcher timeMatcher = timePattern.matcher(message);

            String timeStr = "12:00";  // Default time if not specified
            if (timeMatcher.find()) {
                timeStr = timeMatcher.group(1);
            }

            try {
                // Parse the date and time
                LocalDateTime dateTime;
                DateTimeFormatter formatter;

                if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    dateTime = LocalDateTime.parse(dateStr + " " + timeStr, formatter);
                } else {
                    formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy HH:mm");
                    dateTime = LocalDateTime.parse(dateStr + " " + timeStr, formatter);
                }

                appointment.setAppointmentDateTime(dateTime);
            } catch (DateTimeParseException e) {
                log.error("Error parsing date and time: {} {}", dateStr, timeStr, e);
            }
        }
    }

    private static void extractPropertyAddress(String message, Appointment appointment) {
        // Look for address patterns
        Pattern addressPattern = Pattern.compile("(?:at|for|property|address)[:\\s]+(\\d+[^,]+,[^,]+(?:,[^,]+)*)");
        Matcher addressMatcher = addressPattern.matcher(message);

        if (addressMatcher.find()) {
            appointment.setPropertyAddress(addressMatcher.group(1).trim());
        }
    }

    private static void extractContactInfo(String message, Appointment appointment) {
        // Extract landlord information
        Pattern landlordPattern = Pattern.compile("landlord[:\\s]+([^,]+)");
        Matcher landlordMatcher = landlordPattern.matcher(message);

        if (landlordMatcher.find()) {
            appointment.setLandlordName(landlordMatcher.group(1).trim());

            // Try to extract landlord email
            Pattern emailPattern = Pattern.compile("(?:landlord|owner)(?:'s)?\\s+email[:\\s]+([\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,})");
            Matcher emailMatcher = emailPattern.matcher(message);

            if (emailMatcher.find()) {
                appointment.setLandlordEmail(emailMatcher.group(1).trim());
            }

            // Try to extract landlord phone
            Pattern phonePattern = Pattern.compile("(?:landlord|owner)(?:'s)?\\s+(?:phone|number)[:\\s]+(\\+?\\d[\\d\\s-]{7,})");
            Matcher phoneMatcher = phonePattern.matcher(message);

            if (phoneMatcher.find()) {
                appointment.setLandlordPhone(phoneMatcher.group(1).trim());
            }
        }

        // Extract tenant information
        Pattern tenantPattern = Pattern.compile("tenant[:\\s]+([^,]+)");
        Matcher tenantMatcher = tenantPattern.matcher(message);

        if (tenantMatcher.find()) {
            appointment.setTenantName(tenantMatcher.group(1).trim());

            // Try to extract tenant email
            Pattern emailPattern = Pattern.compile("tenant(?:'s)?\\s+email[:\\s]+([\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,})");
            Matcher emailMatcher = emailPattern.matcher(message);

            if (emailMatcher.find()) {
                appointment.setTenantEmail(emailMatcher.group(1).trim());
            }

            // Try to extract tenant phone
            Pattern phonePattern = Pattern.compile("tenant(?:'s)?\\s+(?:phone|number)[:\\s]+(\\+?\\d[\\d\\s-]{7,})");
            Matcher phoneMatcher = phonePattern.matcher(message);

            if (phoneMatcher.find()) {
                appointment.setTenantPhone(phoneMatcher.group(1).trim());
            }
        }
    }

    public static boolean isAppointmentInfoComplete(Appointment appointment) {
        return appointment.getAppointmentDateTime() != null &&
                appointment.getPropertyAddress() != null && !appointment.getPropertyAddress().isEmpty() &&
                appointment.getLandlordName() != null && !appointment.getLandlordName().isEmpty() &&
                appointment.getLandlordEmail() != null && !appointment.getLandlordEmail().isEmpty() &&
                appointment.getTenantName() != null && !appointment.getTenantName().isEmpty() &&
                appointment.getTenantEmail() != null && !appointment.getTenantEmail().isEmpty();
    }

}
