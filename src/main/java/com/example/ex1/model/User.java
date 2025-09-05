package com.example.ex1.model;

import java.util.ArrayList;

public class User {
    public int userId;
    public String username;
    public String password;
    public String email;
    public ArrayList<Poll> polls;
    //list of polls created by user

    public User(int userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }



}
