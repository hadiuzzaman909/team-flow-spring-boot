package com.example.team_flow.services;

import com.example.team_flow.dtos.request.TaskRequest;
import com.example.team_flow.dtos.response.TaskResponse;
import com.example.team_flow.entities.Task;
import com.example.team_flow.exceptions.TaskBadRequestException;
import com.example.team_flow.exceptions.TaskNotFoundException;
import com.example.team_flow.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Page<TaskResponse> getPaginatedTasks(String searchQuery, String columnName, String order, int page, int size) {
        Sort sort = order.equalsIgnoreCase("desc")
                ? Sort.by(columnName).descending()
                : Sort.by(columnName).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> taskPage = (searchQuery != null && !searchQuery.isEmpty())
                ? taskRepository.findByTitleContainingIgnoreCase(searchQuery, pageable)
                : taskRepository.findAll(pageable);

        return taskPage.map(this::convertToResponse);
    }

    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found."));
        return convertToResponse(task);
    }

    public List<TaskResponse> createTasks(List<TaskRequest> taskRequests) {
        List<Task> tasks = taskRequests.stream().map(request -> {
            Task task = new Task();
            task.setTitle(request.getTitle());
            task.setDescription(request.getDescription());
            task.setStatus(request.getStatus());
            task.setCreatedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            return task;
        }).collect(Collectors.toList());

        List<Task> savedTasks = taskRepository.saveAll(tasks);

        return savedTasks.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public TaskResponse createTask(TaskRequest taskRequest) {
        if (taskRequest.getTitle() == null || taskRequest.getTitle().isEmpty()) {
            throw new TaskBadRequestException("Task title cannot be empty.");
        }
        Task task = convertToEntity(taskRequest);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        Task savedTask = taskRepository.save(task);
        return convertToResponse(savedTask);
    }

    public TaskResponse updateTask(Long id, TaskRequest taskRequest) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found."));
        existingTask.setTitle(taskRequest.getTitle());
        existingTask.setDescription(taskRequest.getDescription());
        existingTask.setStatus(taskRequest.getStatus());
        existingTask.setUpdatedAt(LocalDateTime.now());
        Task updatedTask = taskRepository.save(existingTask);
        return convertToResponse(updatedTask);
    }

    public void deleteTask(Long id) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found."));
        taskRepository.deleteById(id);
    }

    // Conversion Methods
    private TaskResponse convertToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        return response;
    }

    private Task convertToEntity(TaskRequest request) {
        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .build();
    }
}