package com.airtribe.meditrack.util;

import com.airtribe.meditrack.exception.InvalidDataException;

/**
 * Centralized field validation used by every service, instead of each
 * service re-implementing its own ad hoc checks.
 */
public final class Validator {

    private Validator() {
    }

    public static void requireNonBlank(String value, String fieldName) throws InvalidDataException {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(fieldName + " must not be blank");
        }
    }

    public static void requirePositive(double value, String fieldName) throws InvalidDataException {
        if (value <= 0) {
            throw new InvalidDataException(fieldName + " must be positive, got " + value);
        }
    }

    public static void requireRange(int value, int min, int max, String fieldName) throws InvalidDataException {
        if (value < min || value > max) {
            throw new InvalidDataException(fieldName + " must be between " + min + " and " + max + ", got " + value);
        }
    }

    public static void requireValidPhone(String phone) throws InvalidDataException {
        if (phone == null || !phone.matches("\\d{10}")) {
            throw new InvalidDataException("Contact number must be exactly 10 digits");
        }
    }
}
