package com.example.ex1;
import java.util.HashMap;

public class PollManager {
    public int counter;
    public HashMap<Integer, User> listUsers  = new HashMap<>();

    public PollManager() {
        this.counter = 1;
    }


    public void addNewUser(String username, String email) {
        listUsers.put(this.counter, new User(this.counter, username, email));
        this.counter++;
    }

    public void deleteUser(int userId) {
        listUsers.remove(userId);
    }

    public HashMap<Integer, User> showAllUsers(){
        return listUsers;
    }
}
