package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.DateUtil;
import com.airtribe.meditrack.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * Create/view/cancel appointments. Acts as the Observer <em>subject</em>:
 * registered {@link AppointmentObserver}s are notified whenever an
 * appointment is created or cancelled.
 *
 * <p>Also demonstrates the concurrency learning objective beyond
 * {@code IdGenerator}'s {@code AtomicInteger}: each booking schedules a
 * background reminder via {@link TimerTask} instead of blocking the caller.
 */
public class AppointmentService {

    private final DataStore<Appointment> appointments = new DataStore<>(Appointment::getId);
    private final List<AppointmentObserver> observers = new ArrayList<>();
    private final Timer reminderTimer = new Timer("MediTrack-Reminder-Timer", true);

    public void addObserver(AppointmentObserver observer) {
        observers.add(observer);
    }

    public Appointment bookAppointment(Patient patient, Doctor doctor, LocalDateTime scheduledAt) throws InvalidDataException {
        if (patient == null || doctor == null) {
            throw new InvalidDataException("Both patient and doctor are required to book an appointment");
        }
        if (!DateUtil.isFuture(scheduledAt)) {
            throw new InvalidDataException("Appointment time must be in the future");
        }

        Appointment appointment = new Appointment(IdGenerator.getInstance().nextAppointmentId(), patient, doctor, scheduledAt);
        appointment.confirm();
        appointments.save(appointment);

        notifyCreated(appointment);
        scheduleReminder(appointment);
        return appointment;
    }

    public Appointment cancelAppointment(String appointmentId) throws AppointmentNotFoundException {
        Appointment appointment = appointments.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("No appointment found with id " + appointmentId));
        appointment.cancel();
        notifyCancelled(appointment);
        return appointment;
    }

    public List<Appointment> findAll() {
        return appointments.findAll();
    }

    public Appointment findById(String id) {
        return appointments.findById(id).orElse(null);
    }

    /** Releases the background reminder timer's thread. Call on shutdown. */
    public void shutdown() {
        reminderTimer.cancel();
    }

    /** Streams: appointment count grouped by doctor name. */
    public Map<String, Long> appointmentsPerDoctor() {
        return appointments.findAll().stream()
                .collect(Collectors.groupingBy(a -> a.getDoctor().getName(), Collectors.counting()));
    }

    private void notifyCreated(Appointment appointment) {
        for (AppointmentObserver observer : observers) {
            observer.onAppointmentCreated(appointment);
        }
    }

    private void notifyCancelled(Appointment appointment) {
        for (AppointmentObserver observer : observers) {
            observer.onAppointmentCancelled(appointment);
        }
    }

    private void scheduleReminder(Appointment appointment) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("[TimerTask] Reminder: appointment " + appointment.getId()
                        + " for " + appointment.getPatient().getName() + " is coming up.");
            }
        };
        // Demo-only fixed delay in place of scheduling relative to the real
        // appointment time, so the reminder is visible during a short console session.
        reminderTimer.schedule(task, 3000);
    }
}
