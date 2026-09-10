package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Appointment;

/**
 * Observer pattern: anything that wants to react to appointment lifecycle
 * events (console reminders today; email/SMS notifiers could implement
 * this the same way tomorrow) without {@code AppointmentService} needing
 * to know about any specific notification channel.
 */
public interface AppointmentObserver {

    void onAppointmentCreated(Appointment appointment);

    void onAppointmentCancelled(Appointment appointment);
}
