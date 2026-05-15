package com.developer.test.controller;


import com.developer.test.model.Task;
import com.developer.test.model.User;
import com.developer.test.service.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.stream;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private final DataStore dataStore;

    public ApiController(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    // --- 2.1 USER ENDPOINTS ---

    public static class UserCreateRequest {
        @NotBlank(message = "Name is required") public String name;
        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") public String email;
        @NotBlank(message = "Role is required") public String role;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserCreateRequest request) {
        AtomicInteger newId = dataStore.getNextUserId();
        User newUser = new User(newId.get(), request.name, request.email, request.role);
        dataStore.addUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }



    // --- 2.2 & 2.3 TASK ENDPOINTS ---

    public static class TaskCreateRequest {
        @NotBlank(message = "Title is required") public String title;
        @NotBlank(message = "Status is required") public String status;
        @NotNull(message = "UserId is required") public Long userId;
    }

    public static class TaskUpdateRequest {
        public String title;
        public String status;
        public Long userId;
    }

    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskCreateRequest request) {
        validateTaskStatus(request.status);
        validateUserExists(request.userId);

        AtomicInteger newId = dataStore.getNextTaskId();
        Task newTask = new Task(newId.get(), request.title, request.status, Math.toIntExact(request.userId));
        dataStore.addTask(newTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }



    @PutMapping("/tasks/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody TaskUpdateRequest request) {
        Task existingTask = dataStore.getTasks().values()
                .stream()
                .filter(t -> t.getId()==id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (request.title != null) {
            existingTask.setTitle(request.title);
        }
        if (request.status != null) {
            validateTaskStatus(request.status);
            existingTask.setStatus(request.status);
        }
        if (request.userId != null) {
            validateUserExists(request.userId);
            existingTask.setUserId(Math.toIntExact(request.userId));
        }

        return ResponseEntity.ok(existingTask);
    }

    // --- Helper Validators ---

    private void validateTaskStatus(String status) {
        if (!status.equals("pending") && !status.equals("in-progress") && !status.equals("completed")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status must be 'pending', 'in-progress', or 'completed'");
        }
    }

    private void validateUserExists(Long userId) {
        boolean exists = dataStore.getUsers().stream().anyMatch(u -> u.getId() == userId);
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID does not exist");
        }
    }
}
