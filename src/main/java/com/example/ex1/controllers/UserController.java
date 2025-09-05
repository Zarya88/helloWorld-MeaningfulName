package com.example.ex1.controllers;

import com.example.ex1.dto.CreateUserRequest;
import com.example.ex1.model.User;
import com.example.ex1.repo.PollManager;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class UserController {
    private final PollManager manager;

    public UserController() {
        this.manager = new PollManager();
    }

    @PostMapping("/user/create")
    public User createUser(@RequestBody CreateUserRequest r) {
        return manager.createUser(r.username, r.email);
    }

    @GetMapping("/user/all")
    public HashMap<Integer, User> showAllUsers() {
        return manager.showAllUsers();
    }

    @GetMapping("/user/{id}")
    public User showUser(@PathVariable int id) {
        return manager.getUser(id);
    }
}
