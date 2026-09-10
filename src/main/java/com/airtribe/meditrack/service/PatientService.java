package com.airtribe.meditrack.service;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.interfaces.Searchable;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;
import com.airtribe.meditrack.util.Validator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CRUD + search over patients.
 */
public class PatientService implements Searchable<Patient> {

    private final DataStore<Patient> patients = new DataStore<>(Patient::getId);

    public Patient registerPatient(String name, int age, String contactNumber) throws InvalidDataException {
        Validator.requireNonBlank(name, "Patient name");
        Validator.requireRange(age, Constants.MIN_PATIENT_AGE, Constants.MAX_PATIENT_AGE, "Patient age");
        Validator.requireValidPhone(contactNumber);

        Patient patient = new Patient(IdGenerator.getInstance().nextPatientId(), name, age, contactNumber);
        patients.save(patient);
        return patient;
    }

    public List<Patient> findAll() {
        return patients.findAll();
    }

    /**
     * Overload 1/3: find the one patient with this exact id.
     */
    public Patient searchPatient(String id) {
        return patients.findById(id).orElse(null);
    }

    /**
     * Overload 2/3: find every patient with this exact name (names aren't
     * unique, so this returns a list rather than a single patient).
     */
    public List<Patient> searchPatient(String name, boolean byName) {
        return patients.findAll().stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    /**
     * Overload 3/3: find every patient of this exact age.
     */
    public List<Patient> searchPatient(int age) {
        return patients.findAll().stream()
                .filter(p -> p.getAge() == age)
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> search(String query) {
        String q = query.toLowerCase();
        return patients.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(q) || p.getId().equalsIgnoreCase(query))
                .collect(Collectors.toList());
    }

    /**
     * Returns a deep-cloned copy of a patient's record (see
     * {@code Patient#clone()}) so the caller can't accidentally mutate the
     * service's own copy.
     */
    public Patient getDeepCopy(String id) {
        Patient original = searchPatient(id);
        return original == null ? null : original.clone();
    }
}
