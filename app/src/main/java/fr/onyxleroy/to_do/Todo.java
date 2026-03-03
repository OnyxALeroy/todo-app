package fr.onyxleroy.to_do;

import java.io.Serializable;
import java.util.UUID;

public class Todo implements Serializable {

    public enum RepeatType {
        NONE(0),
        HOURLY(1),
        DAILY(2),
        WEEKLY(3),
        MONTHLY(4),
        YEARLY(5);

        private final int value;

        RepeatType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static RepeatType fromValue(int value) {
            for (RepeatType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            return NONE;
        }
    }

    private String id = UUID.randomUUID().toString();
    private String title;
    private String description;
    private long dateTimeMillis;
    private boolean completed = false;
    private RepeatType repeatType = RepeatType.NONE;

    public Todo() {
    }

    public Todo(String title, String description, long dateTimeMillis) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.dateTimeMillis = dateTimeMillis;
        this.completed = false;
        this.repeatType = RepeatType.NONE;
    }

    public Todo(String title, String description, long dateTimeMillis, RepeatType repeatType) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.dateTimeMillis = dateTimeMillis;
        this.completed = false;
        this.repeatType = repeatType;
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

    public RepeatType getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(RepeatType repeatType) {
        this.repeatType = repeatType;
    }

    public boolean isRepeating() {
        return repeatType != null && repeatType != RepeatType.NONE;
    }
}
