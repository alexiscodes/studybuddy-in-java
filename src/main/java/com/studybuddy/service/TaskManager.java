package com.studybuddy.service;

import com.studybuddy.model.Task;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private final List<Task> tasks = new ArrayList<>();

    // loads previously saved tasks 
    public TaskManager() {
        tasks.addAll(TaskStorage.loadTasks());
    }

    public void addTask(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("task title cannot be emptyy!");
        }

        tasks.add(new Task(title.trim()));
        TaskStorage.saveTasks(tasks);
    }

    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    public boolean completeTask(int index) {
        if (index<0 || index >= tasks.size()) {
            return false;
        }

        tasks.get(index).markCompleted();
        TaskStorage.saveTasks(tasks);
        return true;
    }

    public boolean removeTask(int index) {
        if (index < 0 || index >= tasks.size()) {
            return false;
        }

        tasks.remove(index);
        TaskStorage.saveTasks(tasks);
        return true;
    }
}