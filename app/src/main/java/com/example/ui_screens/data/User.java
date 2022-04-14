package com.example.ui_screens.data;

public class User {

    private String name;
    private String email;
    private String type;


    public User (String name, String email, String type) {
        this.name = name;
        this.email = email;
        this.type = type;

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

}
