package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.constants.Constants;

/**
 * Bill for a consultation that included a procedure: consultation fee plus
 * procedure cost, taxed at a higher rate than a plain consultation.
 */
public class ProcedureBill extends Bill {

    private final double procedureCost;

    public ProcedureBill(String id, Appointment appointment, double procedureCost) {
        super(id, appointment, appointment.getDoctor().getConsultationFee());
        this.procedureCost = procedureCost;
    }

    public double getProcedureCost() {
        return procedureCost;
    }

    @Override
    public double calculateTotal() {
        double subtotal = baseAmount + procedureCost;
        return subtotal + (subtotal * Constants.TAX_RATE * Constants.PROCEDURE_TAX_MULTIPLIER);
    }
}
