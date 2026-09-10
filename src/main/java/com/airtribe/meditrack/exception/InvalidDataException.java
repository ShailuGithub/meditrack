package com.airtribe.meditrack.exception;

/**
 * Thrown when data supplied to the system (registration fields, appointment
 * times, billing amounts, ...) fails validation.
 */
public class InvalidDataException extends Exception {

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
