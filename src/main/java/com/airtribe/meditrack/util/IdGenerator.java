package com.airtribe.meditrack.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Singleton id generator — <b>eager</b> initialization variant (the
 * instance is created the moment the class loads, before any thread can
 * even reach {@link #getInstance()}). Compare with {@link AppConfig},
 * which is a <b>lazy</b> singleton.
 *
 * <p>Each sequence is an {@code AtomicInteger} rather than a plain
 * {@code int} so concurrent callers (e.g. two threads booking appointments
 * at the same time) can never receive the same id — see
 * {@code TestRunner#testConcurrentIdGenerationIsUnique()}.
 */
public final class IdGenerator {

    private static final IdGenerator INSTANCE = new IdGenerator();

    private final AtomicInteger patientSeq = new AtomicInteger(0);
    private final AtomicInteger doctorSeq = new AtomicInteger(0);
    private final AtomicInteger appointmentSeq = new AtomicInteger(0);
    private final AtomicInteger billSeq = new AtomicInteger(0);

    private IdGenerator() {
    }

    public static IdGenerator getInstance() {
        return INSTANCE;
    }

    public String nextPatientId() {
        return "PAT" + String.format("%04d", patientSeq.incrementAndGet());
    }

    public String nextDoctorId() {
        return "DOC" + String.format("%04d", doctorSeq.incrementAndGet());
    }

    public String nextAppointmentId() {
        return "APT" + String.format("%04d", appointmentSeq.incrementAndGet());
    }

    public String nextBillId() {
        return "BIL" + String.format("%04d", billSeq.incrementAndGet());
    }
}
