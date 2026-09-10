package com.airtribe.meditrack.service;

import com.airtribe.meditrack.exception.InvalidDataException;

/**
 * Insurance covers a fixed percentage of the bill; the patient pays the
 * remainder.
 */
public class InsuranceBillingStrategy implements BillingStrategy {

    private final double coveragePercent;

    public InsuranceBillingStrategy(double coveragePercent) throws InvalidDataException {
        if (coveragePercent < 0 || coveragePercent > 1) {
            throw new InvalidDataException("Coverage percent must be between 0.0 and 1.0, got " + coveragePercent);
        }
        this.coveragePercent = coveragePercent;
    }

    @Override
    public double applyDiscount(double amount) {
        return amount * (1 - coveragePercent);
    }
}
