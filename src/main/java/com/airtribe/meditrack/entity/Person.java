package com.airtribe.meditrack.entity;

/**
 * Abstract base for {@code Doctor} and {@code Patient}. Holds the fields
 * every person in the clinic has; each subclass adds its own role-specific
 * state and constructor-chains back here with {@code super(...)}.
 */
public abstract class Person extends MedicalEntity {

    protected String name;
    protected int age;
    protected String contactNumber;

    protected Person(String id, String name, int age, String contactNumber) {
        super(id);
        this.name = name;
        this.age = age;
        this.contactNumber = contactNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
