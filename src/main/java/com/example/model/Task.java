package com.example.model;

import java.time.LocalDateTime;

public class Task {
    private final String id;
    private final String description;
    private final LocalDateTime createdAt;

    public Task(String id, String description) {
        this.id = id;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
} 