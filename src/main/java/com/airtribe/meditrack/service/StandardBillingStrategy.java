package com.airtribe.meditrack.service;

/**
 * No discount — the patient pays the bill's calculated total as-is.
 */
public class StandardBillingStrategy implements BillingStrategy {

    @Override
    public double applyDiscount(double amount) {
        return amount;
    }
}
