package com.developer.test.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatsResponse {
    @JsonProperty("users")
    private UsersStats users;
    
    @JsonProperty("tasks")
    private TasksStats tasks;

    public StatsResponse() {
        this.users = new UsersStats();
        this.tasks = new TasksStats();
    }

    public UsersStats getUsers() {
        return users;
    }

    public void setUsers(UsersStats users) {
        this.users = users;
    }

    public TasksStats getTasks() {
        return tasks;
    }

    public void setTasks(TasksStats tasks) {
        this.tasks = tasks;
    }

    public static class UsersStats {
        @JsonProperty("total")
        private int total;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    public static class TasksStats {
        @JsonProperty("total")
        private int total;
        
        @JsonProperty("pending")
        private int pending;
        
        @JsonProperty("inProgress")
        private int inProgress;
        
        @JsonProperty("completed")
        private int completed;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPending() {
            return pending;
        }

        public void setPending(int pending) {
            this.pending = pending;
        }

        public int getInProgress() {
            return inProgress;
        }

        public void setInProgress(int inProgress) {
            this.inProgress = inProgress;
        }

        public int getCompleted() {
            return completed;
        }

        public void setCompleted(int completed) {
            this.completed = completed;
        }
    }
}
