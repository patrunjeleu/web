package com.vacation.model;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private int availability;
    private String preferredActivity;
    private double budget;

    public User() {
    }

    public User(int id, String firstName, String lastName, int availability,
                String preferredActivity, double budget) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.availability = availability;
        this.preferredActivity = preferredActivity;
        this.budget = budget;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public String getPreferredActivity() {
        return preferredActivity;
    }

    public void setPreferredActivity(String preferredActivity) {
        this.preferredActivity = preferredActivity;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", availability=" + availability +
                ", preferredActivity='" + preferredActivity + '\'' +
                ", budget=" + budget +
                '}';
    }
}
