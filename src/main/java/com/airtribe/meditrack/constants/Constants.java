package com.airtribe.meditrack.constants;

/**
 * Application-wide constants. Not instantiable.
 */
public final class Constants {

    private Constants() {
    }

    public static final double TAX_RATE = 0.05;
    public static final double PROCEDURE_TAX_MULTIPLIER = 1.5;

    public static final String DATA_DIR = "data";
    public static final String PATIENTS_FILE = DATA_DIR + "/patients.csv";
    public static final String DOCTORS_FILE = DATA_DIR + "/doctors.csv";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public static final int MIN_PATIENT_AGE = 0;
    public static final int MAX_PATIENT_AGE = 120;
    public static final int MIN_DOCTOR_AGE = 21;
    public static final int MAX_DOCTOR_AGE = 90;
}
