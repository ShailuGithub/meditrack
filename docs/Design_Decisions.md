# Design Decisions

Assumptions, package-layout reasoning, and how each requirement maps onto
the actual code. Written for a reviewer comparing this repo against the
brief.

## Deviations from the brief's file tree, and why

The brief's "Expected Project Structure" is a skeleton, not an exhaustive
list — a few names and a few extra classes had to change or get added for
the project to actually compile and to satisfy the *prose* requirements
(which are more detailed than the tree). Every deviation is listed here.

| Brief says | This repo does | Why |
|---|---|---|
| Package `interface/` (`Searchable.java`, `Payable.java`) | Package **`interfaces/`** | `interface` is a reserved keyword in Java — `package interface;` is a compile error (`<identifier> expected`). This is the one deviation that isn't a judgment call; the literal name from the brief cannot compile. |
| `entity/` lists only `Person, Doctor, Patient, Appointment, Bill, BillSummary` | Adds **`entity/MedicalEntity.java`** (abstract) | The requirements prose separately asks for "Abstract class `MedicalEntity` for common behavior," distinct from the `Person → Doctor, Patient` inheritance chain. `MedicalEntity` sits *above* `Person` and is also extended directly by `Appointment` and `Bill` — none of those are people, but all three need a shared id + timestamp + `describe()` contract, which is exactly the "common behavior" the requirement asks for. |
| `entity/Bill.java` (singular) | `Bill` is **abstract**, with **`ConsultationBill`** and **`ProcedureBill`** subclasses | The grading rubric explicitly calls for "Overriding: `generateBill()` behavior in appropriate classes" and Bonus B calls for "Factory — bill creation (refined factory returning different Bill types)." Both only make sense if there's more than one concrete `Bill` type to override behavior in / for a factory to choose between. |
| No `factory`/`observer`/`pattern` package in the tree | `BillFactory`, `AppointmentObserver`, `ConsoleReminderObserver`, `BillingStrategy` + implementations live in **`service/`** | The brief's tree doesn't reserve a package for design-pattern classes, and the chosen Bonus feature (**B — Design Patterns**) needs somewhere for them to live. They're placed in `service/` rather than inventing a new top-level package, since each one exists specifically to support `AppointmentService`/`BillingService` and isn't reusable outside that context. |
| `service/` lists only `DoctorService, PatientService, AppointmentService` | Adds **`service/BillingService.java`** | The Application Logic requirement explicitly calls for "Billing: `Bill` object, taxes, multiple billing strategies" as its own concern, separate from appointment scheduling — bundling billing into `AppointmentService` would have made it responsible for two unrelated things. |
| `util/` lists `IdGenerator.java` (singular) | Adds **`util/AppConfig.java`** | Bonus B explicitly asks for "Singleton — App configuration / IdGenerator (**eager & lazy examples**)" — i.e. two singletons demonstrating the two initialization styles. `IdGenerator` is the eager one (instance built at class-load time); `AppConfig` is the lazy one (built on first `getInstance()` call, via double-checked locking). |
| `util/AIHelper.java` (optional) | Not implemented | Marked optional in the brief and belongs to Bonus C (AI Feature), which wasn't the bonus chosen for this submission (see below). |

## Bonus feature chosen

Of the four bonus options, this submission implements **B — Design
Patterns**:

- **Singleton** — `util/IdGenerator.java` (eager) and `util/AppConfig.java`
  (lazy, double-checked locking).
- **Factory** — `service/BillFactory.java`, returning `ConsultationBill` or
  `ProcedureBill` based on a `BillFactory.BillType`.
- **Observer** — `service/AppointmentObserver.java` +
  `service/ConsoleReminderObserver.java`; `AppointmentService` notifies
  every registered observer on booking and cancellation.

Strategy (`service/BillingStrategy.java` + `StandardBillingStrategy` /
`InsuranceBillingStrategy`) is also implemented, since the Application
Logic section calls for "multiple billing strategies" as a core (not
bonus) requirement.

Bonus A (File I/O), C (AI recommendation), and D (Streams as a
standalone bonus) were not the chosen bonus, though `util/CSVUtil.java`
exists per the required package structure and streams are used
throughout the service layer (`DoctorService.search`,
`DoctorService.findBySpecialization`, `DoctorService.averageFee`,
`AppointmentService.appointmentsPerDoctor`) as good practice, not as a
bonus claim.

## Assumptions

- **In-memory only.** Like the reference Library Management / Course
  Management projects this track has produced before, MediTrack keeps all
  state in `DataStore<T>` instances for the lifetime of one run — no
  database, no default persistence. `CSVUtil` exists and is usable, but
  nothing wires it into startup/shutdown, since File I/O wasn't the
  chosen bonus.
- **Appointments confirm immediately.** `bookAppointment` moves a new
  appointment straight to `CONFIRMED` rather than leaving it `PENDING`,
  since there's no approval workflow in scope — `PENDING` and `COMPLETED`
  exist on `AppointmentStatus` for completeness and future use.
- **Reminder timing is demo-scaled.** `AppointmentService` schedules its
  `TimerTask` reminder a fixed 3 seconds after booking rather than
  relative to the real appointment time, so the concurrency feature is
  actually observable during a short console session instead of firing
  hours or days later.
- **Doctors are shared, not owned.** `Appointment.clone()` deep-copies its
  nested `Patient` but keeps the same `Doctor` reference — doctors are
  catalog entities managed by `DoctorService`, so cloning one would just
  desync the appointment from the doctor roster rather than protecting
  anything.

## Package Structure

```
com.airtribe.meditrack
├── Main                 Menu-driven console entry point
├── constants  Constants
├── entity     MedicalEntity, Person, Doctor, Patient, Specialization,
│              AppointmentStatus, Appointment, Bill, ConsultationBill,
│              ProcedureBill, BillSummary (immutable)
├── service    DoctorService, PatientService, AppointmentService,
│              BillingService, BillFactory, AppointmentObserver,
│              ConsoleReminderObserver, BillingStrategy,
│              StandardBillingStrategy, InsuranceBillingStrategy
├── util       Validator, DateUtil, CSVUtil, IdGenerator, AppConfig,
│              DataStore<T>
├── exception  AppointmentNotFoundException, InvalidDataException
├── interfaces Searchable<T>, Payable
└── test       TestRunner (manual, no JUnit)
```

## Where each grading criterion lives

| Requirement | Where |
|---|---|
| Encapsulation | Private fields + getters/setters across `entity/`; validation centralized in `util/Validator.java` |
| Inheritance (`super`, `this`, constructor chaining) | `MedicalEntity → Person → Doctor`/`Patient`; `MedicalEntity → Appointment`/`Bill`; `Bill → ConsultationBill`/`ProcedureBill` |
| Overloading | `PatientService.searchPatient(String)` / `(String, boolean)` / `(int)`; `BillingService.generateBill(Appointment, BillFactory.BillType, double)` / `(Appointment)` |
| Overriding / dynamic dispatch | `Bill.calculateTotal()` overridden differently in `ConsultationBill` vs `ProcedureBill`; `MedicalEntity.describe()` overridden in every concrete entity |
| Abstraction & interfaces | Abstract `MedicalEntity`, `Person`, `Bill`; `Searchable<T>` and `Payable` interfaces with default methods (`exists`, `printReceipt`) |
| Deep vs. shallow copy | `Patient.clone()` / `Appointment.clone()` — see `TestRunner.testDeepCloneOfPatient` |
| Immutable class | `entity/BillSummary.java` — all `final` fields, no setters |
| Enums | `Specialization`, `AppointmentStatus` |
| Static blocks / initialization | `AppConfig`'s static block; `IdGenerator`'s per-entity `AtomicInteger` sequences |
| Collections/generics/comparators | `DataStore<T>` (generic), `List`/`Map` throughout services |
| Custom exceptions + chaining | `InvalidDataException`, `AppointmentNotFoundException` (both carry a `(message, cause)` constructor) |
| Concurrency (threads, sync, `AtomicInteger`, `TimerTask`) | `IdGenerator` (`AtomicInteger`), `DataStore` (`synchronized`), `AppointmentService` (`TimerTask` reminder); exercised directly by `TestRunner.testConcurrentIdGenerationIsUnique` and `testConcurrentDataStoreWritesAreSynchronized` |
| Streams & lambdas | `DoctorService.search/findBySpecialization/averageFee`, `AppointmentService.appointmentsPerDoctor` |
| Manual testing | `test/TestRunner.java` — 13 checks, run via `java ... test.TestRunner` or menu option 9 |
