package com.example.ui_screens.data;

import java.util.List;

public class Restaurant {
    private String id;
    private String name;
    private List<Table> tables;
    private String description;
    private float rating;

    public Restaurant(String id, String name, String description, float rating, List<Table> tables){
        this.id = id;
        this.name = name;
        this.tables = tables;
        this.description = description;
        this.rating = rating;
        for(Table table: tables){
            table.setRestaurant(this);
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
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

    public float getRating() {
        return rating;
    }

    public List<Table> getTables() {
        return tables;
    }
}
