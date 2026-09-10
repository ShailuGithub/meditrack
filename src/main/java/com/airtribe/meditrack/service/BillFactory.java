package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.entity.ConsultationBill;
import com.airtribe.meditrack.entity.ProcedureBill;
import com.airtribe.meditrack.util.IdGenerator;

/**
 * Factory pattern: the single place that knows how to construct each
 * concrete {@code Bill} subtype and stamp it with a generated id, so
 * callers ask for a {@code BillType} instead of knowing which
 * {@code Bill} subclass to {@code new} up themselves.
 */
public final class BillFactory {

    private BillFactory() {
    }

    public enum BillType {
        CONSULTATION,
        PROCEDURE
    }

    public static Bill createBill(BillType type, Appointment appointment, double procedureCost) {
        String billId = IdGenerator.getInstance().nextBillId();
        switch (type) {
            case CONSULTATION:
                return new ConsultationBill(billId, appointment);
            case PROCEDURE:
                return new ProcedureBill(billId, appointment, procedureCost);
            default:
                throw new IllegalArgumentException("Unknown bill type: " + type);
        }
    }
}
