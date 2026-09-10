package com.airtribe.meditrack.interfaces;

/**
 * Implemented by anything that produces a payable amount (currently just
 * {@code Bill} and its subclasses).
 */
public interface Payable {

    double calculateTotal();

    /**
     * Default method: a simple console receipt any {@code Payable} gets for
     * free, built only from {@link #calculateTotal()}.
     */
    default void printReceipt() {
        System.out.printf("Amount payable: %.2f%n", calculateTotal());
    }
}
