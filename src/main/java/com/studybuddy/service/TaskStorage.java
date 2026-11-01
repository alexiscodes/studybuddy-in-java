package com.studybuddy.service;

import com.studybuddy.model.Task;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TaskStorage {

    private static final String FILE_PATH = "data/tasks.txt";

    public static void saveTasks(List<Task> tasks) {
        try {
            File file = new File(FILE_PATH);
            file.getParentFile().mkdirs();

            try (PrintWriter writer = new PrintWriter(file)) {
                for (Task task : tasks) {
                    writer.println(
                        task.getTitle() + "|" + task.isCompleted()
                    );
                }
            }

        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    public static List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return tasks;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);

                if (parts.length == 2) {
                    Task task = new Task(parts[0]);

                    if (Boolean.parseBoolean(parts[1])) {
                        task.markCompleted();
                    }

                    tasks.add(task);
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }

        return tasks;
    }
}