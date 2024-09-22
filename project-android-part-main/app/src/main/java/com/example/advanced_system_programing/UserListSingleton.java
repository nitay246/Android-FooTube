package com.example.advanced_system_programing;

import java.util.ArrayList;
import java.util.List;

public class UserListSingleton {
    private static UserListSingleton instance;   // Singleton instance
    private final List<user> userList;           // List to store user objects
    private int startPointer;                    // Pointer to the start of the list

    private static final String INSTANCE_ID = "userList";  // Identifier for the instance

    // Private constructor to initialize the user list and start pointer
    private UserListSingleton() {
        userList = new ArrayList<>();
        startPointer = -1;  // Initialize start pointer to -1 (empty list)
    }

    // Singleton getInstance method to retrieve or create the instance
    public static synchronized UserListSingleton getInstance() {
        if (instance == null) {
            instance = new UserListSingleton();
        }
        return instance;
    }

    // Method to create an empty instance of UserListSingleton
    public static synchronized UserListSingleton createEmptyInstance() {
        instance = new UserListSingleton();
        return instance;
    }

    // Getter for the user list
    public List<user> getUserList() {
        return userList;
    }

    // Method to add a user to the user list
    public void addUser(user user) {
        userList.add(user);
        if (startPointer == -1) {
            startPointer = 0;  // Update start pointer if adding the first user
        }
    }

    // Method to get a user by username and password
    public user getUser(String username, String password) {
        for (user user : userList) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;  // Return user if found
            }
        }
        return null;  // Return null if user not found
    }

    // Method to remove a user from the user list
    public void removeUser(user user) {
        userList.remove(user);
        if (userList.isEmpty()) {
            startPointer = -1;  // Reset start pointer if list becomes empty
        }
    }

    // Method to check if a username is already taken
    public boolean isUsernameTaken(String username) {
        for (user u : userList) {
            if (u.getUsername().equals(username)) {
                return true;  // Return true if username already exists
            }
        }
        return false;  // Return false if username does not exist
    }

    // Method to get the start user in the list
    public user getStartUser() {
        if (startPointer >= 0 && startPointer < userList.size()) {
            return userList.get(startPointer);  // Return start user if within bounds
        } else {
            return null;  // Return null if start pointer is out of bounds
        }
    }

    // Method to check if a user exists based on username and password
    public boolean isUserExists(String username, String password) {
        for (user u : userList) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return true;  // Return true if user exists
            }
        }
        return false;  // Return false if user does not exist
    }
}
