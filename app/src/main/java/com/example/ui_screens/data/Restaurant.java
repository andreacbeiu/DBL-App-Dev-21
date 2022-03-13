package com.example.ui_screens.data;

import java.util.List;

public class Restaurant {
    private String id;
    private String name;
    private List<Table> tables;

    public Restaurant(String id, String name, List<Table> tables){
        this.id = id;
        this.name = name;
        this.tables = tables;
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

    public List<Table> getTables() {
        return tables;
    }
}
