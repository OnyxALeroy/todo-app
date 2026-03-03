package fr.onyxleroy.to_do;

import java.io.Serializable;
import java.util.UUID;

public class Todo implements Serializable {
    private String id = UUID.randomUUID().toString();
    private String title;
    private String description;
    private long dateTimeMillis;
    private boolean completed = false;

    public Todo() {
    }

    public Todo(String title, String description, long dateTimeMillis) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.dateTimeMillis = dateTimeMillis;
        this.completed = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDateTimeMillis() {
        return dateTimeMillis;
    }

    public void setDateTimeMillis(long dateTimeMillis) {
        this.dateTimeMillis = dateTimeMillis;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
