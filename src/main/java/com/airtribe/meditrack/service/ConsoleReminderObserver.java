package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Appointment;

/**
 * Concrete {@code AppointmentObserver} that just prints to the console —
 * the simplest possible notification channel, standing in for a future
 * email/SMS observer.
 */
public class ConsoleReminderObserver implements AppointmentObserver {

    @Override
    public void onAppointmentCreated(Appointment appointment) {
        System.out.println("[Reminder] New appointment booked: " + appointment.describe());
    }

    @Override
    public void onAppointmentCancelled(Appointment appointment) {
        System.out.println("[Reminder] Appointment cancelled: " + appointment.describe());
    }
}
