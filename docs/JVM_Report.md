# JVM Report

How MediTrack actually runs, from `javac` to `java com.airtribe.meditrack.Main`.

## Class Loader

When `java com.airtribe.meditrack.Main` starts, the JVM's class loading
subsystem locates and loads `.class` files on demand — not all at once.
Three loaders work together, in a delegation hierarchy:

- **Bootstrap Class Loader** — loads the core JDK classes MediTrack relies
  on constantly (`java.lang.*`, `java.util.*`, `java.time.*`,
  `java.util.concurrent.atomic.AtomicInteger`, `java.util.Timer`, ...).
- **Platform Class Loader** — loads other JDK modules.
- **Application (System) Class Loader** — loads MediTrack's own classes
  (everything under `com.airtribe.meditrack`) from `out/` on the
  classpath, plus any third-party dependencies (none, here — this project
  is deliberately dependency-free Core Java).

Delegation means the Application loader asks its parent first before
trying to load a class itself, which is why `java.util.HashMap` always
resolves to the trusted JDK implementation and never to something a
project could accidentally shadow.

Loading happens lazily: `Main.class` loads first, and a class like
`com.airtribe.meditrack.util.AppConfig` only loads (running its `static`
block — see the console output `[AppConfig] static block: ...` the first
time `AppConfig.getInstance()` runs) the first time something actually
references it, not at JVM startup.

## Runtime Data Areas

| Area | What lives there in MediTrack |
|---|---|
| **Heap** | Every object created with `new` — every `Doctor`, `Patient`, `Appointment`, `Bill`, the `HashMap`/`LinkedHashMap` inside each `DataStore<T>`, `String`s built by `String.format(...)`. Shared across all threads; garbage-collected once unreachable (e.g. a cloned `Patient` returned by `PatientService.getDeepCopy` that the caller discards). |
| **Stack** | One per thread. Each method call — `Main.main`, `AppointmentService.bookAppointment`, `Validator.requireRange`, and so on — pushes a frame holding its local variables and return address. The concurrency tests in `TestRunner` (`testConcurrentIdGenerationIsUnique`, `testConcurrentDataStoreWritesAreSynchronized`) each spin up dozens of threads, each with its own stack, all sharing the one heap. |
| **Method Area** (part of Metaspace since Java 8) | Per-class metadata: the structure of `Doctor`, `Appointment`, etc., their method bytecode, static fields such as `IdGenerator`'s `AtomicInteger` sequences and `AppConfig`'s `INSTANCE` reference. |
| **PC Register** | One per thread; holds the address of the JVM instruction that thread is currently executing. When a thread is inside native code (not MediTrack's case in practice) this is undefined. |
| **Native Method Stacks** | Support native (non-Java) method calls the JVM itself makes internally; MediTrack's own code never calls native methods directly. |

## Execution Engine

The Execution Engine is what actually runs the bytecode `javac` produced
in `out/com/airtribe/meditrack/**/*.class`:

1. The **Interpreter** reads bytecode instruction-by-instruction and
   executes it directly. This is how every method starts out running.
2. The **JIT (Just-In-Time) Compiler** watches which methods run often
   ("hot" methods) — in MediTrack, `DataStore.findById`/`save` and
   `IdGenerator.next*Id()` are called on essentially every operation — and
   compiles *those* straight to native machine code, which then runs
   directly on the CPU instead of being re-interpreted every call.
3. The **Garbage Collector** (part of the execution engine) reclaims heap
   objects nothing references anymore, e.g. an `Appointment.clone()`'s
   intermediate deep-copied `Patient` once a test's local variable for it
   goes out of scope.

## JIT Compiler vs. Interpreter

| | Interpreter | JIT Compiler |
|---|---|---|
| Startup cost | None — starts executing immediately | Compilation itself takes time |
| Steady-state speed | Slower — re-decodes bytecode every call | Fast — runs as native machine code |
| When it's used | Every method, initially, and for code that only runs once or twice (e.g. `Main.seedDemoData`, run once at startup) | Methods called repeatedly (e.g. `DataStore.save`/`findById`, hit on every registration and lookup) |

The JVM starts with pure interpretation so the program is responsive
immediately, then progressively JIT-compiles the methods that turn out to
matter, rather than paying compilation cost upfront for code (like
one-time setup) that will never benefit from it.

## "Write Once, Run Anywhere"

`javac` compiles MediTrack's `.java` source into platform-independent
**bytecode** (`.class` files), not machine code for a specific OS/CPU. Any
machine with a compatible JVM installed — Windows, macOS, Linux — can run
that same bytecode unchanged, because the JVM (not the bytecode) is the
part that's platform-specific and handles translating to the actual
machine's native instructions. That's what let this project be compiled
once with `javac` and run identically whether invoked from Windows
PowerShell or a Unix-style shell, without touching the source.
