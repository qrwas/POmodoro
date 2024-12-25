package com.pomodoro.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.pomodoro.model.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TaskStorage {
    private static final String TASKS_FILE = "tasks.json";
    private final ObjectMapper mapper = new ObjectMapper();

    public void saveTasks(List<Task> tasks) {
        try {
            mapper.writeValue(new File(TASKS_FILE), tasks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Task> loadTasks() {
        try {
            return mapper.readValue(new File(TASKS_FILE),
                TypeFactory.defaultInstance().constructCollectionType(List.class, Task.class));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
