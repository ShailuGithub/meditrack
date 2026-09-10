# Setup Instructions

## JDK Version Used

This project was built and tested with:

```
javac 17.0.12
java version "17.0.12" 2024-07-16 LTS
```

JDK 17 (LTS) was chosen because it is a long-term support release and is
widely available. The code only uses language features available since
Java 8 (no records, switch expressions, pattern matching, etc.), so it
will also compile and run on JDK 8+.

## Installing the JDK

1. Download a JDK 17 build (e.g. from [Adoptium/Temurin](https://adoptium.net/)
   or Oracle).
2. Run the installer.
3. Confirm the install by opening a terminal and running:
   ```
   java -version
   javac -version
   ```
   Both commands should print a version number. If they don't, add the
   JDK's `bin` folder to your `PATH` environment variable.

   > If you have more than one JDK installed (common on a machine that's
   > also used for other coursework), `java -version` and `javac -version`
   > can silently point at *different* installs via `PATH`. If you ever
   > see `UnsupportedClassVersionError` when running compiled classes,
   > that's the cause — call the JDK 17 `java` binary explicitly, e.g.
   > `"C:\Program Files\Java\jdk-17\bin\java.exe" -cp out com.airtribe.meditrack.Main`.

## "Hello World" Verification

A minimal sanity check before building the full project:

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

Compile and run it:

```
javac HelloWorld.java
java HelloWorld
```

Expected output:

```
Hello, World!
```

This confirms the JDK (compiler `javac` + runtime `java`) is correctly
installed and on the `PATH` before working with the larger MediTrack
codebase.

## Running MediTrack

See the root [`README.md`](../README.md) for the exact compile/run
commands and sample output for this project.
