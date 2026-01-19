package com.vacation.model;

public class Destination {
    private int id;
    private String name;
    private String description;
    private int duration;
    private String activity1;
    private String activity2;
    private double budget;

    public Destination() {
    }

    public Destination(int id, String name, String description, int duration,
                      String activity1, String activity2, double budget) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.activity1 = activity1;
        this.activity2 = activity2;
        this.budget = budget;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getActivity1() {
        return activity1;
    }

    public void setActivity1(String activity1) {
        this.activity1 = activity1;
    }

    public String getActivity2() {
        return activity2;
    }

    public void setActivity2(String activity2) {
        this.activity2 = activity2;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", activities=[" + activity1 + ", " + activity2 + ']' +
                ", budget=" + budget +
                '}';
    }
}
