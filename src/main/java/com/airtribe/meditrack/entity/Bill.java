package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.interfaces.Payable;

/**
 * Abstract base for a clinic bill. {@code calculateTotal()} is where each
 * bill type's "generate bill" behavior diverges (polymorphism /
 * overriding) — a plain consultation and a procedure are taxed
 * differently, and new bill types (e.g. a lab-test bill) only need to
 * supply their own {@code calculateTotal()}.
 */
public abstract class Bill extends MedicalEntity implements Payable {

    protected final Appointment appointment;
    protected final double baseAmount;
    protected boolean paid;

    protected Bill(String id, Appointment appointment, double baseAmount) {
        super(id);
        this.appointment = appointment;
        this.baseAmount = baseAmount;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void markPaid() {
        this.paid = true;
    }

    @Override
    public abstract double calculateTotal();

    @Override
    public String describe() {
        return String.format("Bill[%s] for %s - Total: %.2f - %s",
                id, appointment.getPatient().getName(), calculateTotal(), paid ? "PAID" : "UNPAID");
    }
}
