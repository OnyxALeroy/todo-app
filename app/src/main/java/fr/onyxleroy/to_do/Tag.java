package fr.onyxleroy.to_do;

import java.io.Serializable;
import java.util.UUID;

public class Tag implements Serializable {

    private String id = UUID.randomUUID().toString();
    private String name;
    private int color;

    public Tag() {
    }

    public Tag(String name, int color) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tag tag = (Tag) obj;
        return id != null && id.equals(tag.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
