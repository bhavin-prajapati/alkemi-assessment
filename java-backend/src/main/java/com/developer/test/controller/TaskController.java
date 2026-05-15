package com.developer.test.controller;

import com.developer.test.dto.TasksResponse;
import com.developer.test.model.Task;
import com.developer.test.service.DataStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private static final Set<String> VALID_STATUSES = Set.of("pending", "in-progress", "completed");
    
    private final DataStore dataStore;
    
    public TaskController(DataStore dataStore) {
        this.dataStore = dataStore;
    }
    
    @GetMapping
    public ResponseEntity<TasksResponse> getTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userId) {
        List<Task> tasks = dataStore.getTasks(status, userId);
        TasksResponse response = new TasksResponse(tasks, tasks.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        if (task == null
                || task.getTitle() == null || task.getTitle().trim().isEmpty()
                || task.getStatus() == null || task.getStatus().trim().isEmpty()
                || !VALID_STATUSES.contains(task.getStatus().trim())
                || task.getUserId() <= 0
                || dataStore.getUserById(task.getUserId()) == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            task.setId(0);
            dataStore.addTask(task);
            URI location = URI.create(String.format("/api/tasks/%d", task.getId()));
            return ResponseEntity.created(location).body(task);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable int id, @RequestBody Task task) {
        if (task == null) {
            return ResponseEntity.badRequest().build();
        }

        Task existing = dataStore.getTaskById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        String title = task.getTitle();
        String status = task.getStatus();
        int userId = task.getUserId();

        if (title != null) {
            if (title.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            existing.setTitle(title.trim());
        }

        if (status != null) {
            if (status.trim().isEmpty() || !VALID_STATUSES.contains(status.trim())) {
                return ResponseEntity.badRequest().build();
            }
            existing.setStatus(status.trim());
        }

        if (userId != 0) {
            if (userId <= 0 || dataStore.getUserById(userId) == null) {
                return ResponseEntity.badRequest().build();
            }
            existing.setUserId(userId);
        }

        try {
            dataStore.addTask(existing);
            return ResponseEntity.ok(existing);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
