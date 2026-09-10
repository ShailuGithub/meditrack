package com.airtribe.meditrack.entity;

/**
 * A clinic doctor. Extends {@code Person} and adds specialization and fee.
 */
public class Doctor extends Person {

    private Specialization specialization;
    private double consultationFee;

    public Doctor(String id, String name, int age, String contactNumber,
                  Specialization specialization, double consultationFee) {
        super(id, name, age, contactNumber);
        this.specialization = specialization;
        this.consultationFee = consultationFee;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    @Override
    public String describe() {
        return String.format("Dr. %s [%s] - %s - Fee: %.2f", name, id, specialization, consultationFee);
    }
}
