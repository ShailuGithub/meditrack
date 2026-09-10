package com.airtribe.meditrack.service;

/**
 * Strategy pattern: how a bill's calculated total gets adjusted before
 * payment (no discount, insurance coverage, ...) without
 * {@code BillingService} branching on a payer type internally.
 */
public interface BillingStrategy {

    double applyDiscount(double amount);
}
