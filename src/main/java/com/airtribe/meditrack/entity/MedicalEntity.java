package com.airtribe.meditrack.entity;

import java.time.LocalDateTime;

/**
 * Common behavior shared by every record MediTrack tracks: a unique id, a
 * creation timestamp, and a human-readable description. {@code Person}
 * (and therefore {@code Doctor}/{@code Patient}) as well as
 * {@code Appointment} and {@code Bill} all extend this — none of those are
 * "people", so the shared behavior lives one level above {@code Person}
 * rather than being duplicated or forced into it.
 */
public abstract class MedicalEntity {

    protected final String id;
    protected final LocalDateTime createdAt;

    protected MedicalEntity(String id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Human-readable one-line summary. Every concrete entity overrides this
     * differently (dynamic dispatch).
     */
    public abstract String describe();

    @Override
    public String toString() {
        return describe();
    }
}
