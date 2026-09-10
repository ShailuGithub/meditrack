package com.airtribe.meditrack.util;

import com.airtribe.meditrack.constants.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Formatting/parsing helpers around {@code LocalDateTime}, sharing one
 * canonical pattern ({@code Constants.DATE_TIME_FORMAT}) so appointment
 * timestamps display consistently everywhere.
 */
public final class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);

    private DateUtil() {
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }

    public static LocalDateTime parse(String text) {
        return LocalDateTime.parse(text, FORMATTER);
    }

    public static boolean isFuture(LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.now());
    }
}
