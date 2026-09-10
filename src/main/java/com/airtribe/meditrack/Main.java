package com.airtribe.meditrack;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.entity.BillSummary;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.entity.Specialization;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.BillingService;
import com.airtribe.meditrack.service.ConsoleReminderObserver;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.service.StandardBillingStrategy;
import com.airtribe.meditrack.test.TestRunner;
import com.airtribe.meditrack.util.AppConfig;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Menu-driven console entry point wiring every service together.
 */
public class Main {

    private static final PatientService patientService = new PatientService();
    private static final DoctorService doctorService = new DoctorService();
    private static final AppointmentService appointmentService = new AppointmentService();
    private static final BillingService billingService = new BillingService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        AppConfig config = AppConfig.getInstance();
        System.out.println("Welcome to " + config.getAppName() + " v" + config.getVersion());
        appointmentService.addObserver(new ConsoleReminderObserver());
        seedDemoData();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        registerPatient();
                        break;
                    case "2":
                        registerDoctor();
                        break;
                    case "3":
                        bookAppointment();
                        break;
                    case "4":
                        cancelAppointment();
                        break;
                    case "5":
                        viewAppointments();
                        break;
                    case "6":
                        generateBill();
                        break;
                    case "7":
                        searchMenu();
                        break;
                    case "8":
                        viewAnalytics();
                        break;
                    case "9":
                        new TestRunner().runAll();
                        break;
                    case "0":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option, try again.");
                }
            } catch (InvalidDataException | AppointmentNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }

        appointmentService.shutdown();
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("==== MediTrack Menu ====");
        System.out.println("1. Register Patient");
        System.out.println("2. Register Doctor");
        System.out.println("3. Book Appointment");
        System.out.println("4. Cancel Appointment");
        System.out.println("5. View Appointments");
        System.out.println("6. Generate Bill");
        System.out.println("7. Search Doctor / Patient");
        System.out.println("8. View Analytics");
        System.out.println("9. Run Tests");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private static void registerPatient() throws InvalidDataException {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Age: ");
        int age = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Contact (10 digits): ");
        String contact = scanner.nextLine();
        Patient patient = patientService.registerPatient(name, age, contact);
        System.out.println("Registered: " + patient.describe());
    }

    private static void registerDoctor() throws InvalidDataException {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Age: ");
        int age = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Contact (10 digits): ");
        String contact = scanner.nextLine();
        System.out.println("Specializations: " + Arrays.toString(Specialization.values()));
        System.out.print("Specialization: ");
        Specialization specialization = Specialization.valueOf(scanner.nextLine().trim().toUpperCase());
        System.out.print("Consultation fee: ");
        double fee = Double.parseDouble(scanner.nextLine().trim());
        Doctor doctor = doctorService.registerDoctor(name, age, contact, specialization, fee);
        System.out.println("Registered: " + doctor.describe());
    }

    private static void bookAppointment() throws InvalidDataException {
        System.out.print("Patient ID: ");
        Patient patient = patientService.searchPatient(scanner.nextLine().trim());
        System.out.print("Doctor ID: ");
        Doctor doctor = doctorService.findById(scanner.nextLine().trim());
        if (patient == null || doctor == null) {
            System.out.println("Patient or doctor not found.");
            return;
        }
        System.out.print("Hours from now: ");
        int hours = Integer.parseInt(scanner.nextLine().trim());
        Appointment appointment = appointmentService.bookAppointment(patient, doctor, LocalDateTime.now().plusHours(hours));
        System.out.println("Booked: " + appointment.describe());
    }

    private static void cancelAppointment() throws AppointmentNotFoundException {
        System.out.print("Appointment ID: ");
        String id = scanner.nextLine().trim();
        Appointment appointment = appointmentService.cancelAppointment(id);
        System.out.println("Cancelled: " + appointment.describe());
    }

    private static void viewAppointments() {
        List<Appointment> all = appointmentService.findAll();
        if (all.isEmpty()) {
            System.out.println("No appointments yet.");
            return;
        }
        all.forEach(a -> System.out.println(a.describe()));
    }

    private static void generateBill() {
        System.out.print("Appointment ID: ");
        String id = scanner.nextLine().trim();
        Appointment appointment = appointmentService.findById(id);
        if (appointment == null) {
            System.out.println("Appointment not found.");
            return;
        }
        Bill bill = billingService.generateBill(appointment);
        BillSummary summary = billingService.summarize(bill, new StandardBillingStrategy());
        System.out.println(summary);
    }

    private static void searchMenu() {
        System.out.print("Search doctors or patients? (d/p): ");
        String type = scanner.nextLine().trim().toLowerCase();
        System.out.print("Query: ");
        String query = scanner.nextLine().trim();
        if (type.equals("d")) {
            doctorService.search(query).forEach(d -> System.out.println(d.describe()));
        } else {
            patientService.search(query).forEach(p -> System.out.println(p.describe()));
        }
    }

    private static void viewAnalytics() {
        System.out.printf("Average consultation fee: %.2f%n", doctorService.averageFee());
        System.out.println("Appointments per doctor: " + appointmentService.appointmentsPerDoctor());
        for (Specialization specialization : Specialization.values()) {
            List<Doctor> doctors = doctorService.findBySpecialization(specialization);
            if (!doctors.isEmpty()) {
                System.out.println(specialization + ": " + doctors.size() + " doctor(s)");
            }
        }
    }

    private static void seedDemoData() {
        try {
            Patient patient = patientService.registerPatient("Asha Rao", 30, "9876543210");
            Doctor doctor = doctorService.registerDoctor("Vivek Iyer", 40, "9090909090",
                    Specialization.CARDIOLOGY, 800);
            appointmentService.bookAppointment(patient, doctor, LocalDateTime.now().plusDays(1));
        } catch (InvalidDataException e) {
            System.out.println("Demo seed failed: " + e.getMessage());
        }
    }
}
