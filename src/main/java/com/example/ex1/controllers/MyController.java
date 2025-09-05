package com.example.ex1.controllers;
import org.springframework.web.bind.annotation.*;

@RestController
public class MyController {
        @GetMapping("/hello") //fetch
        public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
            return String.format("Hello %s!", name);
        }

        @PostMapping //create
        public String post() {
                return "Hello world";
        }

        /*
        @PutMapping //updating
        public String put() {
            //todo
        }

        @DeleteMapping //delete
        public String delete() {
            //todo
        }*/


}
