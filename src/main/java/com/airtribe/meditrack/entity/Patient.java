package com.airtribe.meditrack.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A clinic patient. Implements {@code Cloneable} to support deep-copying a
 * patient (e.g. before handing a record to a report generator that might
 * mutate it) without the copy and the original sharing the same
 * {@code medicalHistory} list.
 */
public class Patient extends Person implements Cloneable {

    private List<String> medicalHistory;

    public Patient(String id, String name, int age, String contactNumber) {
        super(id, name, age, contactNumber);
        this.medicalHistory = new ArrayList<>();
    }

    public List<String> getMedicalHistory() {
        return medicalHistory;
    }

    public void addHistoryEntry(String entry) {
        medicalHistory.add(entry);
    }

    /**
     * Deep copy: {@code Object.clone()} only shallow-copies fields, so the
     * cloned patient would otherwise share (and silently mutate) the same
     * {@code medicalHistory} list as the original. We re-point the clone at
     * a fresh list holding copies of the same entries.
     */
    @Override
    public Patient clone() {
        try {
            Patient copy = (Patient) super.clone();
            copy.medicalHistory = new ArrayList<>(this.medicalHistory);
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Patient declares Cloneable, this can't happen", e);
        }
    }

    @Override
    public String describe() {
        return String.format("Patient %s [%s], age %d, contact %s", name, id, age, contactNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Patient)) {
            return false;
        }
        Patient other = (Patient) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
