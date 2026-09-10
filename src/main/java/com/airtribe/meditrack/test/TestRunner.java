package com.airtribe.meditrack.test;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.AppointmentStatus;
import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.entity.Specialization;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.BillingService;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.util.AppConfig;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Manual test runner (no JUnit, per the brief) — a plain {@code main}
 * method that exercises the core flows and prints PASS/FAIL per check.
 * Reachable both standalone ({@code java ... test.TestRunner}) and from
 * the console menu in {@code Main}.
 */
public class TestRunner {

    private int passed = 0;
    private int failed = 0;

    public static void main(String[] args) {
        new TestRunner().runAll();
    }

    public void runAll() {
        testPatientRegistrationAndSearch();
        testDeepCloneOfPatient();
        testAppointmentBookingAndCancellation();
        testBillingCalculatesTax();
        testSingletonReturnsSameInstance();
        testInvalidDataThrows();
        testConcurrentIdGenerationIsUnique();
        testConcurrentDataStoreWritesAreSynchronized();

        System.out.println();
        System.out.println("Results: " + passed + " passed, " + failed + " failed");
    }

    private void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    private void testPatientRegistrationAndSearch() {
        try {
            PatientService service = new PatientService();
            Patient p = service.registerPatient("Asha Rao", 30, "9876543210");
            check("registerPatient stores the patient", service.searchPatient(p.getId()) != null);
            check("searchPatient(name) overload finds it", !service.searchPatient("Asha Rao", true).isEmpty());
            check("searchPatient(age) overload finds it", !service.searchPatient(30).isEmpty());
        } catch (Exception e) {
            check("testPatientRegistrationAndSearch threw unexpectedly: " + e.getMessage(), false);
        }
    }

    private void testDeepCloneOfPatient() {
        try {
            PatientService service = new PatientService();
            Patient original = service.registerPatient("Kiran Shah", 45, "9123456789");
            original.addHistoryEntry("Diagnosed with hypertension");

            Patient copy = original.clone();
            copy.addHistoryEntry("Follow-up scheduled");

            check("clone() returns a distinct object", copy != original);
            check("clone() deep-copies medicalHistory (original unaffected)",
                    original.getMedicalHistory().size() == 1 && copy.getMedicalHistory().size() == 2);
        } catch (Exception e) {
            check("testDeepCloneOfPatient threw unexpectedly: " + e.getMessage(), false);
        }
    }

    private void testAppointmentBookingAndCancellation() {
        AppointmentService appointmentService = new AppointmentService();
        try {
            PatientService patientService = new PatientService();
            DoctorService doctorService = new DoctorService();

            Patient patient = patientService.registerPatient("Meera Nair", 28, "9988776655");
            Doctor doctor = doctorService.registerDoctor("Vivek Iyer", 40, "9090909090",
                    Specialization.CARDIOLOGY, 800);

            Appointment appt = appointmentService.bookAppointment(patient, doctor, LocalDateTime.now().plusDays(1));
            check("booked appointment is CONFIRMED", appt.getStatus() == AppointmentStatus.CONFIRMED);

            appointmentService.cancelAppointment(appt.getId());
            check("cancelled appointment is CANCELLED", appt.getStatus() == AppointmentStatus.CANCELLED);
        } catch (Exception e) {
            check("testAppointmentBookingAndCancellation threw unexpectedly: " + e.getMessage(), false);
        } finally {
            appointmentService.shutdown();
        }
    }

    private void testBillingCalculatesTax() {
        try {
            PatientService patientService = new PatientService();
            DoctorService doctorService = new DoctorService();
            Patient patient = patientService.registerPatient("Ritu Verma", 33, "9871234560");
            Doctor doctor = doctorService.registerDoctor("Sameer Joshi", 50, "9871111111",
                    Specialization.GENERAL_MEDICINE, 500);
            Appointment appt = new Appointment("APT-TEST", patient, doctor, LocalDateTime.now().plusHours(2));

            BillingService billingService = new BillingService();
            Bill bill = billingService.generateBill(appt);
            check("consultation bill total includes tax (> base fee)", bill.calculateTotal() > 500);
        } catch (Exception e) {
            check("testBillingCalculatesTax threw unexpectedly: " + e.getMessage(), false);
        }
    }

    private void testSingletonReturnsSameInstance() {
        AppConfig a = AppConfig.getInstance();
        AppConfig b = AppConfig.getInstance();
        check("AppConfig (lazy singleton) always returns the same instance", a == b);

        IdGenerator ida = IdGenerator.getInstance();
        IdGenerator idb = IdGenerator.getInstance();
        check("IdGenerator (eager singleton) always returns the same instance", ida == idb);
    }

    private void testInvalidDataThrows() {
        PatientService service = new PatientService();
        boolean threw = false;
        try {
            service.registerPatient("", -5, "abc");
        } catch (InvalidDataException e) {
            threw = true;
        }
        check("registerPatient rejects invalid data", threw);
    }

    /**
     * Fires {@code threadCount} threads at {@code IdGenerator} concurrently
     * and confirms every generated id is unique — proving the
     * {@code AtomicInteger} sequence prevents the race condition a plain
     * {@code int++} would allow.
     */
    private void testConcurrentIdGenerationIsUnique() {
        int threadCount = 20;
        Set<String> ids = java.util.Collections.synchronizedSet(new HashSet<>());
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    ids.add(IdGenerator.getInstance().nextPatientId());
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        try {
            latch.await();
            check("concurrent IdGenerator calls produce " + threadCount + " unique ids", ids.size() == threadCount);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            check("testConcurrentIdGenerationIsUnique interrupted", false);
        }
    }

    /**
     * Fires many threads writing into a shared {@code DataStore}
     * concurrently and confirms every write lands — proving the
     * {@code synchronized} methods on {@code DataStore} prevent lost
     * updates under concurrent access.
     */
    private void testConcurrentDataStoreWritesAreSynchronized() {
        int writerCount = 50;
        DataStore<String> store = new DataStore<>(s -> s);
        CountDownLatch latch = new CountDownLatch(writerCount);

        for (int i = 0; i < writerCount; i++) {
            int index = i;
            new Thread(() -> {
                try {
                    store.save("item-" + index);
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        try {
            latch.await();
            check("concurrent synchronized DataStore writes are all retained", store.count() == writerCount);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            check("testConcurrentDataStoreWritesAreSynchronized interrupted", false);
        }
    }
}
