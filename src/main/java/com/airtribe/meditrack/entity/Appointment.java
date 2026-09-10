package com.airtribe.meditrack.entity;

import java.time.LocalDateTime;

/**
 * A booked slot between a {@code Patient} and a {@code Doctor}.
 *
 * <p>Implements {@code Cloneable} to demonstrate deep-copy semantics on a
 * more complex object graph than {@code Patient} alone: cloning an
 * appointment deep-copies the nested {@code Patient} (so editing the
 * clone's patient record doesn't leak back into the original appointment)
 * but deliberately keeps the {@code Doctor} reference shared — doctors are
 * catalog entities owned by {@code DoctorService}, not state that belongs
 * to any one appointment, so copying one would just desync it from the
 * doctor roster.
 */
public class Appointment extends MedicalEntity implements Cloneable {

    private Patient patient;
    private Doctor doctor;
    private LocalDateTime scheduledAt;
    private AppointmentStatus status;

    public Appointment(String id, Patient patient, Doctor doctor, LocalDateTime scheduledAt) {
        super(id);
        this.patient = patient;
        this.doctor = doctor;
        this.scheduledAt = scheduledAt;
        this.status = AppointmentStatus.PENDING;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void confirm() {
        this.status = AppointmentStatus.CONFIRMED;
    }

    public void cancel() {
        this.status = AppointmentStatus.CANCELLED;
    }

    public void complete() {
        this.status = AppointmentStatus.COMPLETED;
    }

    @Override
    public Appointment clone() {
        try {
            Appointment copy = (Appointment) super.clone();
            copy.patient = this.patient.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Appointment declares Cloneable, this can't happen", e);
        }
    }

    @Override
    public String describe() {
        return String.format("Appointment[%s] %s with %s at %s - %s",
                id, patient.getName(), doctor.getName(), scheduledAt, status);
    }
}
