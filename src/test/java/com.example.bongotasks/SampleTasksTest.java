package com.example.bongotasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SampleTasksTest {
    private SampleTasks sampleTasks;
    private Task task;

    @BeforeEach
    void setUp() {
        // Arrange
        sampleTasks = new SampleTasks();
        task = new Task("04", "Buy Bread", "Buy some more bread", false);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getTaskList() {
        assertEquals(3, sampleTasks.getTaskList().size());
    }

    @Test
    void addTask() {
        int currentSize = sampleTasks.getTaskList().size();
        // Act
        sampleTasks.addTask(task);
        // Assert
        assertEquals(currentSize + 1, sampleTasks.getTaskList().size());
    }
}