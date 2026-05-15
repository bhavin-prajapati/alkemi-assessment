package com.developer.test.controller;

import com.developer.test.dto.StatsResponse;
import com.developer.test.service.DataStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatsController {
    
    private final DataStore dataStore;
    
    public StatsController(DataStore dataStore) {
        this.dataStore = dataStore;
    }
    
    @GetMapping
    public ResponseEntity<StatsResponse> getStats() {
        StatsResponse stats = dataStore.getStats();
        return ResponseEntity.ok(stats);
    }
}
