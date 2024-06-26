package org.example.testtaskprivatbank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.testtaskprivatbank.model.Task;
import org.example.testtaskprivatbank.model.TaskStatus;
import org.example.testtaskprivatbank.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/task")
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<String> createTask(@Valid @RequestBody Task task) {
        try {
            Long id = taskService.createTask(task);
            return ResponseEntity.ok("Task created with id: " + id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        if (taskService.deleteTask(id)) {
            return ResponseEntity.ok("Task successfully deleted.");
        } else {
            return ResponseEntity.status(404).body("Task not found.");
        }
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<String> updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, Object> status) {
        if (!status.containsKey("status")) {
            return ResponseEntity.badRequest().body("Status field is required.");
        }
        try {
            TaskStatus taskStatus = TaskStatus.valueOf((String) status.get("status"));
            Task updatedTask = taskService.updateTaskStatus(id, taskStatus);
            if (updatedTask != null) {
                return ResponseEntity.ok("Task status updated. New status: " + updatedTask.getStatus());
            } else {
                return ResponseEntity.status(404).body("Task not found.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value.");
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<String> updateTaskFields(@PathVariable Long id, @RequestBody Map<String, Object> taskDetails) {
        // Validate fields
        if (taskDetails.containsKey("title")) {
            String title = (String) taskDetails.get("title");
            if (title == null || title.isEmpty()) {
                return ResponseEntity.badRequest().body("Title cannot be empty.");
            }
        }
        if (taskDetails.containsKey("description")) {
            String description = (String) taskDetails.get("description");
            if (description == null || description.isEmpty()) {
                return ResponseEntity.badRequest().body("Description cannot be empty.");
            }
        }
        if (taskDetails.containsKey("status")) {
            String status = (String) taskDetails.get("status");
            if (status == null || status.isEmpty()) {
                return ResponseEntity.badRequest().body("Status cannot be empty.");
            }
            try {
                TaskStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid status value.");
            }
        }
        if (taskDetails.containsKey("deadline")) {
            String deadlineStr = (String) taskDetails.get("deadline");
            if (deadlineStr == null || deadlineStr.isEmpty()) {
                return ResponseEntity.badRequest().body("Deadline cannot be empty.");
            }
            try {
                LocalDateTime.parse(deadlineStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Invalid deadline format. Use ISO_LOCAL_DATE_TIME.");
            }
        }

        Task updatedTask = taskService.updateTaskFields(id, taskDetails);
        if (updatedTask != null) {
            return ResponseEntity.ok("Task successfully updated.");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(tasks);
        }
    }
}
