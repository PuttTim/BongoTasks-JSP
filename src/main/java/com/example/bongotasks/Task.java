package com.example.bongotasks;

public class Task {
    private String id;
    private String name;
    private String description;
    private boolean status;

    public Task(String id, String name, String description, boolean status) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isStatus() {
        return status;
    }
    

}
