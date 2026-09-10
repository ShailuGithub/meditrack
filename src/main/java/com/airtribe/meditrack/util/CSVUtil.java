package com.airtribe.meditrack.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Minimal CSV read/write helper. Every method uses try-with-resources so
 * the underlying file handle is always closed, even if reading/writing
 * throws partway through.
 */
public final class CSVUtil {

    private CSVUtil() {
    }

    /**
     * Writes one line per row, comma-joining each row's fields.
     */
    public static void writeRows(String path, List<String[]> rows) throws IOException {
        Path file = Paths.get(path);
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (String[] row : rows) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        }
    }

    /**
     * Reads every non-blank line and splits it on commas. Returns an empty
     * list (not an error) when the file doesn't exist yet, since that's the
     * normal state before anything has ever been saved.
     */
    public static List<String[]> readRows(String path) throws IOException {
        Path file = Paths.get(path);
        if (!Files.exists(file)) {
            return Collections.emptyList();
        }
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                rows.add(line.split(","));
            }
        }
        return rows;
    }
}
