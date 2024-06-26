//package org.example.testtaskprivatbank.service;
//
//import org.example.testtaskprivatbank.model.Task;
//import org.example.testtaskprivatbank.model.TaskStatus;
//import org.example.testtaskprivatbank.repository.TaskRepository;
//import org.example.testtaskprivatbank.service.impl.TaskServiceImpl;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.stubbing.Answer;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//public class TaskServiceTest {
//
//    @Mock
//    private TaskRepository taskRepository;
//
//    @InjectMocks
//    private TaskServiceImpl taskService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testCreateTask() {
//        Task task = new Task();
//        task.setTitle("Sample Task");
//        task.setDescription("Description of the task");
//        task.setDeadline(LocalDateTime.parse("2022-12-31T23:59:59"));
//
//        when(taskRepository.existsByTitle("Sample Task")).thenReturn(false);
//        when(taskRepository.save(any(Task.class))).thenAnswer((Answer<Task>) invocation -> {
//            Task taskToSave = invocation.getArgument(0);
//            taskToSave.setId(1L); // Setting a mock id here, replace with actual logic if needed
//            return taskToSave;
//        });
//
//        Long id = taskService.createTask(task);
//
//        assertThat(id).isNotNull();
//        assertThat(task.getId()).isNotNull(); // Ensure that task's id is set after creation
//        verify(taskRepository, times(1)).save(task);
//    }
//
//    @Test
//    public void testCreateTask_throwsException_whenTitleIsEmpty() {
//        Task task = new Task();
//        task.setTitle("");
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            taskService.createTask(task);
//        });
//
//        assertThat(exception.getMessage()).isEqualTo("Task title cannot be empty.");
//    }
//
//    @Test
//    public void testCreateTask_throwsException_whenMaxTasksReached() {
//        List<Task> tasks = new ArrayList<>();
//        for (int i = 1; i <= 100; i++) {
//            Task task = new Task();
//            task.setId((long) i);
//            task.setTitle("Task " + i);
//            tasks.add(task);
//        }
//        when(taskRepository.count()).thenReturn(100L);
//
//        Task newTask = new Task();
//        newTask.setTitle("New Task");
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            taskService.createTask(newTask);
//        });
//
//        assertThat(exception.getMessage()).isEqualTo("Maximum number of tasks reached.");
//        verify(taskRepository, never()).save(any(Task.class));
//    }
//
//    @Test
//    public void testDeleteTask() {
//        Task task = new Task();
//        task.setId(1L);
//
//        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
//
//        boolean result = taskService.deleteTask(1L);
//
//        assertThat(result).isTrue();
//        verify(taskRepository, times(1)).deleteById(1L);
//    }
//
//    @Test
//    public void testDeleteTask_notFound() {
//        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
//
//        boolean result = taskService.deleteTask(1L);
//
//        assertThat(result).isFalse();
//        verify(taskRepository, never()).deleteById(anyLong());
//    }
//
//    @Test
//    public void testUpdateTaskStatus() {
//        Task task = new Task();
//        task.setId(1L);
//        task.setStatus(TaskStatus.TODO);
//
//        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
//        when(taskRepository.save(any(Task.class))).thenReturn(task);
//
//        Task updatedTask = taskService.updateTaskStatus(1L, TaskStatus.COMPLETED);
//
//        assertThat(updatedTask.getStatus()).isEqualTo(TaskStatus.COMPLETED);
//    }
//
//    @Test
//    public void testUpdateTaskStatus_notFound() {
//        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
//
//        Task updatedTask = taskService.updateTaskStatus(1L, TaskStatus.COMPLETED);
//
//        assertThat(updatedTask).isNull();
//        verify(taskRepository, never()).save(any(Task.class));
//    }
//
//    @Test
//    public void testUpdateTaskFields() {
//        Task task = new Task();
//        task.setId(1L);
//        task.setTitle("Old Title");
//        task.setDescription("Old Description");
//
//        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
//        when(taskRepository.save(any(Task.class))).thenReturn(task);
//
//        Map<String, Object> updatedFields = Map.of(
//                "title", "New Title",
//                "description", "New Description"
//        );
//
//        Task updatedTask = taskService.updateTaskFields(1L, updatedFields);
//
//        assertThat(updatedTask.getTitle()).isEqualTo("New Title");
//        assertThat(updatedTask.getDescription()).isEqualTo("New Description");
//    }
//
//    @Test
//    public void testUpdateTaskFields_notFound() {
//        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
//
//        Map<String, Object> updatedFields = Map.of(
//                "title", "New Title",
//                "description", "New Description"
//        );
//
//        Task updatedTask = taskService.updateTaskFields(1L, updatedFields);
//
//        assertThat(updatedTask).isNull();
//        verify(taskRepository, never()).save(any(Task.class));
//    }
//
//    @Test
//    public void testUpdateTaskFields_throwsException_whenTitleIsAlready() {
//        // Arrange
//        Long taskId = 1L;
//        String existingTitle = "Existing Task Title";
//        String newTitle = "New Task Title";
//
//        Task existingTask = new Task();
//        existingTask.setId(taskId);
//        existingTask.setTitle(existingTitle);
//
//        Map<String, Object> updatedFields = Map.of(
//                "title", newTitle
//                // Add other fields as needed
//        );
//
//        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
//        when(taskRepository.existsByTitle(newTitle)).thenReturn(true);
//
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            taskService.updateTaskFields(taskId, updatedFields);
//        });
//
//        assertThat(exception.getMessage()).isEqualTo("A task with the given title already exists.");
//        verify(taskRepository, never()).save(any(Task.class));
//    }
//
//
//    @Test
//    public void testGetAllTasks() {
//        List<Task> tasks = List.of(new Task(), new Task());
//
//        when(taskRepository.findAll()).thenReturn(tasks);
//
//        List<Task> result = taskService.getAllTasks();
//
//        assertThat(result).hasSize(2);
//    }
//
//    @Test
//    public void testGetAllTasks_emptyList() {
//        when(taskRepository.findAll()).thenReturn(List.of());
//
//        List<Task> result = taskService.getAllTasks();
//
//        assertThat(result).isEmpty();
//    }
//}
