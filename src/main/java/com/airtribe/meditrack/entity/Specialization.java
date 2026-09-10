package com.airtribe.meditrack.entity;

/**
 * Fixed set of clinic specializations. An enum instead of a free-text
 * String so a doctor's specialization can't silently be "Cardiologist"
 * in one place and "cardiology" in another.
 */
public enum Specialization {
    CARDIOLOGY,
    DERMATOLOGY,
    GENERAL_MEDICINE,
    PEDIATRICS,
    ORTHOPEDICS,
    NEUROLOGY,
    ENT
}
