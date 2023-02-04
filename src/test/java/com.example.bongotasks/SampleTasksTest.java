package com.example.bongotasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleTasksTest {
    private SampleTasks sampleTasks;
    private Task task;

    @BeforeEach
    public void setUp() {
        // Arrange
        sampleTasks = new SampleTasks();
        task = new Task("04", "Buy Bread", "Buy some bread", false);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void getTaskList() {
        assertEquals(3, sampleTasks.getTaskList().size());
    }

    @Test
    public void addTask() {
        int currentSize = sampleTasks.getTaskList().size();
        // Act
//        sampleTasks.addTask(task);
        // Assert
        assertEquals(currentSize + 1, sampleTasks.getTaskList().size());
    }
}