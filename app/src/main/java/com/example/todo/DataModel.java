package com.example.todo;

import java.time.LocalDateTime;
import java.util.Date;

public class DataModel {
    private String name;
    private boolean checked;
    private String type;
    private LocalDateTime date;

    public DataModel(String name, boolean checked, String type, LocalDateTime date) {
        this.name = name;
        this.checked = checked;
        this.type = type;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getType() { return this.type; }

    public void setType(String type) { this.type = type; }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

}