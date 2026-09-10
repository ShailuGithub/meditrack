package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.constants.Constants;

/**
 * Bill for a plain doctor consultation: base fee plus standard tax.
 */
public class ConsultationBill extends Bill {

    public ConsultationBill(String id, Appointment appointment) {
        super(id, appointment, appointment.getDoctor().getConsultationFee());
    }

    @Override
    public double calculateTotal() {
        return baseAmount + (baseAmount * Constants.TAX_RATE);
    }
}
