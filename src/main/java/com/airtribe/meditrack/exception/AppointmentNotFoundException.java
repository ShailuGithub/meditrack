package com.airtribe.meditrack.exception;

/**
 * Thrown when an appointment lookup (cancel, complete, bill) is requested
 * for an id that does not exist in the {@code AppointmentService}.
 */
public class AppointmentNotFoundException extends Exception {

    public AppointmentNotFoundException(String message) {
        super(message);
    }

    public AppointmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
