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

import java.time.LocalDateTime;
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
        Task task1 = new Task(1L, "Task 1", "Description 1", "TO_DO", null, null);
        Task task2 = new Task(2L, "Task 2", "Description 2", "IN_PROGRESS", null, null);

        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        List<TaskResponse> tasks = taskService.getAllTasks();

        assertEquals(2, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitle());
        assertEquals("Task 2", tasks.get(1).getTitle());
    }

    @Test
    void testGetTaskById_Found() {
        Task task = new Task(1L, "Task 1", "Description 1", "TO_DO", null, null);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getTaskById(1L);

        assertNotNull(response);
        assertEquals("Task 1", response.getTitle());
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    void testCreateTask() {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("New Task");
        taskRequest.setDescription("New Description");
        taskRequest.setStatus("TO_DO");

        Task savedTask = new Task(1L, "New Task", "New Description", "TO_DO", null, null);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskResponse response = taskService.createTask(taskRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("New Task", response.getTitle());
    }

    @Test
    void testUpdateTask_Success() {

        Long taskId = 1L;
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("Updated Task");
        taskRequest.setDescription("Updated Description");
        taskRequest.setStatus("IN_PROGRESS");

        Task existingTask = new Task(1L, "Old Task", "Old Description", "TO_DO", null, null);
        Task updatedTask = new Task(1L, "Updated Task", "Updated Description", "IN_PROGRESS", null, LocalDateTime.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(updatedTask);

        TaskResponse response = taskService.updateTask(taskId, taskRequest);

        assertNotNull(response);
        assertEquals("Updated Task", response.getTitle());
        assertEquals("Updated Description", response.getDescription());
        assertEquals("IN_PROGRESS", response.getStatus());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    void testUpdateTask_NotFound() {
        Long taskId = 1L;
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("Updated Task");
        taskRequest.setDescription("Updated Description");
        taskRequest.setStatus("IN_PROGRESS");

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(taskId, taskRequest));
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testDeleteTask_Success() {
        Long taskId = 1L;
        Task existingTask = new Task(1L, "Task to Delete", "Description", "TO_DO", null, null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        doNothing().when(taskRepository).deleteById(taskId);

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void testDeleteTask_NotFound() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(taskId));
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).deleteById(taskId);
    }


}