package com.example.projekt;

import javafx.beans.property.*;

public class Task {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty priority = new SimpleStringProperty();
    private final StringProperty date = new SimpleStringProperty();
    private final StringProperty comment = new SimpleStringProperty();

    public Task(String name, String status, String priority, String date) {
        this.name.set(name);
        this.status.set(status);
        this.priority.set(priority);
        this.date.set(date);
        this.comment.set("");
    }

    // Gettery
    public String getName() { return name.get(); }
    public String getStatus() { return status.get(); }
    public String getPriority() { return priority.get(); }
    public String getDate() { return date.get(); }
    public String getComment() { return comment.get(); }

    // Settery
    public void setName(String name) { this.name.set(name); }
    public void setStatus(String status) { this.status.set(status); }
    public void setPriority(String priority) { this.priority.set(priority); }
    public void setDate(String date) { this.date.set(date); }
    public void setComment(String comment) { this.comment.set(comment); }

    // Właściwości
    public StringProperty nameProperty() { return name; }
    public StringProperty statusProperty() { return status; }
    public StringProperty priorityProperty() { return priority; }
    public StringProperty dateProperty() { return date; }
    public StringProperty commentProperty() { return comment; }

    @Override
    public String toString() {
        return name.get() + " (" + status.get() + ")";
    }
}
