package com.example.ex1;

import org.springframework.web.bind.annotation.*;

public class User {
    public int userId;
    public String username;
    public String email;

    public User(int userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }



}
