package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.entity.BillSummary;
import com.airtribe.meditrack.util.DataStore;

import java.util.List;

/**
 * Generates and stores bills, using {@link BillFactory} to construct the
 * right {@code Bill} subtype and a {@link BillingStrategy} to work out what
 * the patient actually owes after any discount/coverage.
 */
public class BillingService {

    private final DataStore<Bill> bills = new DataStore<>(Bill::getId);

    /**
     * Overload 1/2: full control over bill type, procedure cost, and
     * billing strategy.
     */
    public Bill generateBill(Appointment appointment, BillFactory.BillType type, double procedureCost) {
        Bill bill = BillFactory.createBill(type, appointment, procedureCost);
        bills.save(bill);
        return bill;
    }

    /**
     * Overload 2/2: the common case — a plain consultation bill, no
     * procedure cost.
     */
    public Bill generateBill(Appointment appointment) {
        return generateBill(appointment, BillFactory.BillType.CONSULTATION, 0.0);
    }

    public BillSummary summarize(Bill bill, BillingStrategy strategy) {
        double payable = strategy.applyDiscount(bill.calculateTotal());
        return new BillSummary(bill.getId(), bill.getAppointment().getPatient().getName(), payable);
    }

    public List<Bill> findAll() {
        return bills.findAll();
    }
}
