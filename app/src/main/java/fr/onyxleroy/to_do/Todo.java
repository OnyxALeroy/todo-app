package fr.onyxleroy.to_do;

import java.io.Serializable;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Todo implements Serializable {
    private String id = UUID.randomUUID().toString();
    private String title;
    private String description;
    private long dateTimeMillis;
    private boolean completed = false;

    // Optional: custom constructor if you want to skip id and completed
    public Todo(String title, String description, long dateTimeMillis) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.dateTimeMillis = dateTimeMillis;
        this.completed = false;
    }
}
