package org.example.testtaskprivatbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.testtaskprivatbank.db.DatabaseErrorHandler;
import org.example.testtaskprivatbank.db.RoutingDataSource;
import org.example.testtaskprivatbank.model.Task;
import org.example.testtaskprivatbank.model.TaskStatus;
import org.example.testtaskprivatbank.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    // Both mocks must be present, without them, all tests fail
    @MockBean
    private RoutingDataSource routingDataSource;

    @MockBean
    private DatabaseErrorHandler databaseErrorHandler;

    @Test
    public void testCreateTask() throws Exception {
        Task task = new Task();
        task.setTitle("New Task");
        task.setDescription("Description of the task");
        task.setDeadline(LocalDateTime.now().plusDays(1));
        when(taskService.createTask(any(Task.class))).thenReturn(1L);

        mockMvc.perform(post("/api/v1/task/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(content().string("Task created with id: 1"));
    }

    @Test
    public void testCreateTask_throwsException_whenTitleIsEmpty() throws Exception {
        Task task = new Task();
        task.setTitle("");  // Invalid input
        task.setDescription("Description of the task");

        mockMvc.perform(post("/api/v1/task/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteTask() throws Exception {
        when(taskService.deleteTask(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/api/v1/task/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Task successfully deleted."));
    }

    @Test
    public void testDeleteTask_notFound() throws Exception {
        when(taskService.deleteTask(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/api/v1/task/delete/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task not found."));
    }

    @Test
    public void testUpdateTaskStatus() throws Exception {
        Task task = new Task();
        task.setStatus(TaskStatus.COMPLETED);
        when(taskService.updateTaskStatus(anyLong(), any(TaskStatus.class))).thenReturn(task);

        Map<String, String> status = new HashMap<>();
        status.put("status", "COMPLETED");

        mockMvc.perform(put("/api/v1/task/update-status/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(status)))
                .andExpect(status().isOk())
                .andExpect(content().string("Task status updated. New status: COMPLETED"));
    }

    @Test
    public void testUpdateTaskStatus_withoutStatusField() throws Exception {
        Map<String, String> status = new HashMap<>();

        mockMvc.perform(put("/api/v1/task/update-status/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(status)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Status field is required."));
    }

    @Test
    public void testUpdateTaskStatus_withInvalidValue() throws Exception {
        Map<String, String> status = new HashMap<>();
        status.put("status", "INVALID_STATUS");

        mockMvc.perform(put("/api/v1/task/update-status/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(status)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid status value."));
    }

    @Test
    public void testUpdateTaskFields() throws Exception {
        Task task = new Task();
        task.setTitle("Updated Title");
        when(taskService.updateTaskFields(anyLong(), any(Map.class))).thenReturn(task);

        Map<String, Object> taskDetails = new HashMap<>();
        taskDetails.put("title", "Updated Title");

        mockMvc.perform(patch("/api/v1/task/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("Task successfully updated."));
    }

    @Test
    public void testUpdateTaskFields_invalidDeadlineFormat() throws Exception {
        Long taskId = 1L;
        String invalidDeadline = "2023-12-31"; // Invalid format

        Map<String, Object> taskDetails = new HashMap<>();
        taskDetails.put("deadline", invalidDeadline);

        mockMvc.perform(patch("/api/v1/task/update/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid deadline format. Use ISO_LOCAL_DATE_TIME."));
    }

    @Test
    public void testGetAllTasks() throws Exception {
        Task task = new Task();
        task.setTitle("Task 1");
        when(taskService.getAllTasks()).thenReturn(Collections.singletonList(task));

        mockMvc.perform(get("/api/v1/task/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task 1"));
    }

    @Test
    public void testGetAllTasks_emptyList() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/task/get-all"))
                .andExpect(status().isNoContent());
    }
}
