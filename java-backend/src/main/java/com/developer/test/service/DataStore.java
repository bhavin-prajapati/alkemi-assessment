package com.developer.test.service;

import com.developer.test.model.Task;
import com.developer.test.model.User;
import com.developer.test.dto.StatsResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class DataStore {


    private final ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicInteger nextUserId = new AtomicInteger(1);
    private final AtomicInteger nextTaskId = new AtomicInteger(1);

    public DataStore() {
        // Initialize with sample data
        users.put(1, new User(1, "John Doe", "john@example.com", "developer"));
        users.put(2, new User(2, "Jane Smith", "jane@example.com", "designer"));
        users.put(3, new User(3, "Bob Johnson", "bob@example.com", "manager"));
        
        tasks.put(1, new Task(1, "Implement authentication", "pending", 1));
        tasks.put(2, new Task(2, "Design user interface", "in-progress", 2));
        tasks.put(3, new Task(3, "Review code changes", "completed", 3));
        
        nextUserId.set(4);
        nextTaskId.set(4);
    }

    /**
     * Set/Replace all users.
     * Since 'users' is final, we clear and addAll.
     */
    public void setUsers(List<User> userList) {
        this.users.clear();
        if (userList != null) {
            userList.forEach(user -> this.users.put(user.getId(), user));
            // Sync the atomic ID to avoid collisions
            int maxId = userList.stream().mapToInt(User::getId).max().orElse(0);
            nextUserId.set(maxId + 1);
        }
    }

    /**
     * Add or Update a single user
     */
    public void addUser(User user) {
        if (user.getId() == 0) {
            int maxId = users.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
            int nextId = Math.max(nextUserId.get(), maxId + 1);
            user.setId(nextId);
            nextUserId.set(nextId + 1);
        }
        this.users.put(user.getId(), user);
    }

    /**
     * Set/Replace all tasks.
     */
    public void setTasks(List<Task> taskList) {
        this.tasks.clear();
        if (taskList != null) {
            taskList.forEach(task -> this.tasks.put(task.getId(), task));
            int maxId = taskList.stream().mapToInt(Task::getId).max().orElse(0);
            nextTaskId.set(maxId + 1);
        }
    }

    /**
     * Add or Update a single task
     */
    public void addTask(Task task) {
        if (task.getId() == 0) {
            task.setId(nextTaskId.getAndIncrement());
        }
        this.tasks.put(task.getId(), task);
    }

    public ConcurrentHashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public AtomicInteger getNextUserId() {
        return nextUserId;
    }

    public AtomicInteger getNextTaskId() {
        return nextTaskId;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(int id) {
        return users.get(id);
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public List<Task> getTasks(String status, String userId) {
        List<Task> allTasks = new ArrayList<>(tasks.values());
        
        return allTasks.stream()
                .filter(task -> {
                    boolean matchStatus = status == null || status.isEmpty() || task.getStatus().equals(status);
                    boolean matchUserId = userId == null || userId.isEmpty() || 
                            task.getUserId() == Integer.parseInt(userId);
                    return matchStatus && matchUserId;
                })
                .collect(Collectors.toList());
    }

    public StatsResponse getStats() {
        StatsResponse stats = new StatsResponse();
        stats.getUsers().setTotal(users.size());
        stats.getTasks().setTotal(tasks.size());
        
        for (Task task : tasks.values()) {
            switch (task.getStatus()) {
                case "pending":
                    stats.getTasks().setPending(stats.getTasks().getPending() + 1);
                    break;
                case "in-progress":
                    stats.getTasks().setInProgress(stats.getTasks().getInProgress() + 1);
                    break;
                case "completed":
                    stats.getTasks().setCompleted(stats.getTasks().getCompleted() + 1);
                    break;
            }
        }
        
        return stats;
    }
}
