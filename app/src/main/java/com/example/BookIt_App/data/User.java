package com.example.BookIt_App.data;

/*
* User data type, stores the name of the user, the email and the type of account they have. The main purposes is for the
* class to be used when displaying employee/manager accounts
* in the restaurant account page*/

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
