package com.airtribe.meditrack.service;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Specialization;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.interfaces.Searchable;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;
import com.airtribe.meditrack.util.Validator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CRUD + search over doctors.
 */
public class DoctorService implements Searchable<Doctor> {

    private final DataStore<Doctor> doctors = new DataStore<>(Doctor::getId);

    public Doctor registerDoctor(String name, int age, String contactNumber,
                                  Specialization specialization, double consultationFee) throws InvalidDataException {
        Validator.requireNonBlank(name, "Doctor name");
        Validator.requireRange(age, Constants.MIN_DOCTOR_AGE, Constants.MAX_DOCTOR_AGE, "Doctor age");
        Validator.requireValidPhone(contactNumber);
        Validator.requirePositive(consultationFee, "Consultation fee");

        Doctor doctor = new Doctor(IdGenerator.getInstance().nextDoctorId(), name, age, contactNumber,
                specialization, consultationFee);
        doctors.save(doctor);
        return doctor;
    }

    public Doctor findById(String id) {
        return doctors.findById(id).orElse(null);
    }

    public List<Doctor> findAll() {
        return doctors.findAll();
    }

    public boolean removeDoctor(String id) {
        return doctors.deleteById(id);
    }

    @Override
    public List<Doctor> search(String query) {
        String q = query.toLowerCase();
        return doctors.findAll().stream()
                .filter(d -> d.getName().toLowerCase().contains(q)
                        || d.getId().equalsIgnoreCase(query)
                        || d.getSpecialization().name().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    /** Streams: filter doctors by specialization. */
    public List<Doctor> findBySpecialization(Specialization specialization) {
        return doctors.findAll().stream()
                .filter(d -> d.getSpecialization() == specialization)
                .collect(Collectors.toList());
    }

    /** Streams: average consultation fee across all doctors. */
    public double averageFee() {
        return doctors.findAll().stream()
                .mapToDouble(Doctor::getConsultationFee)
                .average()
                .orElse(0.0);
    }
}
