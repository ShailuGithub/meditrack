package com.airtribe.meditrack.entity;

import java.time.LocalDateTime;

/**
 * Immutable snapshot of a bill: every field is {@code final}, there are no
 * setters, and the one mutable-looking piece of state
 * ({@code generatedAt}) is fixed at construction time. Safe to hand to
 * multiple threads/callers without synchronization since it can never
 * change after creation.
 */
public final class BillSummary {

    private final String billId;
    private final String patientName;
    private final double totalAmount;
    private final LocalDateTime generatedAt;

    public BillSummary(String billId, String patientName, double totalAmount) {
        this.billId = billId;
        this.patientName = patientName;
        this.totalAmount = totalAmount;
        this.generatedAt = LocalDateTime.now();
    }

    public String getBillId() {
        return billId;
    }

    public String getPatientName() {
        return patientName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    @Override
    public String toString() {
        return String.format("BillSummary[%s] %s owes %.2f (generated %s)",
                billId, patientName, totalAmount, generatedAt);
    }
}
