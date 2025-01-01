package com.example.team_flow.services;

import com.example.team_flow.dtos.request.TaskRequest;
import com.example.team_flow.dtos.response.TaskResponse;
import com.example.team_flow.entities.Task;
import com.example.team_flow.exceptions.TaskNotFoundException;
import com.example.team_flow.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTasks() {
        // Mock data
        Task task1 = new Task(1L, "Task 1", "Description 1", "TO_DO", null, null);
        Task task2 = new Task(2L, "Task 2", "Description 2", "IN_PROGRESS", null, null);

        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        // Call the method
        List<TaskResponse> tasks = taskService.getAllTasks();

        // Assertions
        assertEquals(2, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitle());
        assertEquals("Task 2", tasks.get(1).getTitle());
    }

    @Test
    void testGetTaskById_Found() {
        // Mock data
        Task task = new Task(1L, "Task 1", "Description 1", "TO_DO", null, null);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Call the method
        TaskResponse response = taskService.getTaskById(1L);

        // Assertions
        assertNotNull(response);
        assertEquals("Task 1", response.getTitle());
    }

    @Test
    void testGetTaskById_NotFound() {
        // Mock behavior
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Assertions
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    void testCreateTask() {
        // Mock data
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("New Task");
        taskRequest.setDescription("New Description");
        taskRequest.setStatus("TO_DO");

        Task task = new Task(null, "New Task", "New Description", "TO_DO", null, null);
        Task savedTask = new Task(1L, "New Task", "New Description", "TO_DO", null, null);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Call the method
        TaskResponse response = taskService.createTask(taskRequest);

        // Assertions
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("New Task", response.getTitle());
    }
}
