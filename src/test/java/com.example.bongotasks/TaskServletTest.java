package com.example.bongotasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class TaskServletTest {
    private String SQL_URL = "jdbc:mysql://127.0.0.1:3306/?user=root";
    private String SQL_USER = "root";
    private String SQL_PASSWORD = "admin";
    private TaskServlet taskServlet;
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;
    private RequestDispatcher requestDispatcher;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private MockedStatic<DriverManager> driverManager;
    ResultSet resultSet;
    private Task taskToUpdate;

    @BeforeEach
    void setUp() {
        taskServlet = new TaskServlet();
        httpRequest = mock(HttpServletRequest.class);
        httpResponse = mock(HttpServletResponse.class);
        requestDispatcher = mock(RequestDispatcher.class);
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        driverManager = mockStatic(DriverManager.class);
        resultSet = mock(ResultSet.class);
        taskToUpdate = spy(TaskServlet.taskToUpdate);
    }

    @AfterEach
    void tearDown() {
        driverManager.close();

    }


    @Test
    void getConnection() {
        // Arrange
        driverManager.when(() -> DriverManager.getConnection(SQL_URL, SQL_USER, SQL_PASSWORD)).thenReturn(connection);

        // Act
        Connection newConnection = taskServlet.getConnection();

        // Assert
        assertEquals(connection, newConnection);
    }

    @Test
    void getTasks() throws ServletException, SQLException, IOException {
        // Arrange
        when(httpRequest.getRequestDispatcher("/taskDashboard.jsp")).thenReturn(requestDispatcher);

        when(DriverManager.getConnection(SQL_URL, SQL_USER, SQL_PASSWORD)).thenReturn(connection);
        when(connection.prepareStatement("SELECT * FROM bongotasks.tasks")).thenReturn(preparedStatement);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("id")).thenReturn("1", "2");
        when(resultSet.getString("name")).thenReturn("Buy milk", "Buy eggs");
        when(resultSet.getString("description")).thenReturn("Go to the supermarket and buy Meji Milk", "Go to the supermarket and buy 12 eggs");
        when(resultSet.getBoolean("status")).thenReturn(false, false);

        // Act
        List<Task> newTaskList = taskServlet.getTasks(httpRequest, httpResponse);

        // Assert
        verify(httpRequest, atLeastOnce()).getRequestDispatcher("/taskDashboard.jsp");
        verify(requestDispatcher, atLeastOnce()).forward(httpRequest, httpResponse);
        verify(httpRequest, atLeastOnce()).setAttribute("taskList", newTaskList);
    }

    @Test
    void addTask() throws ServletException, SQLException, IOException {
        // Arrange
        when(httpRequest.getParameter("taskName")).thenReturn("Test Task");
        when(httpRequest.getParameter("taskDesc")).thenReturn("Test Task Description");
        when(httpRequest.getRequestDispatcher("/confirmCreate.jsp")).thenReturn(requestDispatcher);

        when(DriverManager.getConnection(SQL_URL, SQL_USER, SQL_PASSWORD)).thenReturn(connection);
        when(connection.prepareStatement("INSERT INTO bongotasks.tasks (name, description, status) VALUES (?, ?, ?)")).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        Map<String, String> newTask = taskServlet.addTask(httpRequest, httpResponse);
        System.out.println(newTask);

        // Assert
        verify(httpRequest, atLeastOnce()).getParameter("taskName");
        verify(httpRequest, atLeastOnce()).getParameter("taskDesc");
        verify(httpRequest, atLeastOnce()).getRequestDispatcher("/confirmCreate.jsp");
        verify(httpRequest, atLeastOnce()).setAttribute("newTask", newTask);
        verify(requestDispatcher, atLeastOnce()).forward(httpRequest, httpResponse);
    }

    @Test
    void fillTask() throws SQLException, ServletException, IOException {
        String editId = "1";
        when(httpRequest.getParameter("edit-id")).thenReturn(editId);
        when(httpRequest.getRequestDispatcher("/editTaskForm.jsp")).thenReturn(requestDispatcher);

        when(DriverManager.getConnection(SQL_URL, SQL_USER, SQL_PASSWORD)).thenReturn(connection);
        when(connection.prepareStatement("SELECT * FROM bongotasks.tasks WHERE id = " + editId)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("id")).thenReturn("1");
        when(resultSet.getString("name")).thenReturn("Buy milk");
        when(resultSet.getString("description")).thenReturn("Go to the supermarket and buy Meiji Milk");
        when(resultSet.getBoolean("status")).thenReturn(false);

        // Act
        Task fetchedTask = taskServlet.fillTask(httpRequest, httpResponse);

        // Assert
        verify(httpRequest, atLeastOnce()).getParameter("edit-id");
        verify(httpRequest, atLeastOnce()).setAttribute("task", fetchedTask);
        verify(httpRequest, atLeastOnce()).getRequestDispatcher("/editTaskForm.jsp");
        verify(requestDispatcher, atLeastOnce()).forward(httpRequest, httpResponse);

    }

    @Test
    void updateTask() throws SQLException, ServletException, IOException {
        String taskName = "Buy Eggs";
        String taskDesc = "Go to the supermarket and buy 12 eggs";

        // Arrange
        when(httpRequest.getParameter("taskName")).thenReturn(taskName);
        when(httpRequest.getParameter("taskDesc")).thenReturn(taskDesc);
        when(httpRequest.getRequestDispatcher("/confirmUpdate.jsp")).thenReturn(requestDispatcher);

        when(DriverManager.getConnection(SQL_URL, SQL_USER, SQL_PASSWORD)).thenReturn(connection);
        when(connection.prepareStatement("UPDATE bongotasks.tasks SET name = ?, description = ? WHERE id = ?")).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        Task oldTask = taskServlet.updateTask(httpRequest, httpResponse);

        // Assert
        verify(httpRequest, atLeastOnce()).getParameter("taskName");
        verify(httpRequest, atLeastOnce()).getParameter("taskDesc");

        verify(httpRequest, atLeastOnce()).setAttribute("taskName", taskName);
        verify(httpRequest, atLeastOnce()).setAttribute("taskDesc", taskDesc);
        verify(httpRequest, atLeastOnce()).setAttribute("newtaskName", taskToUpdate.getName());
        verify(httpRequest, atLeastOnce()).setAttribute("newtaskDesc", taskToUpdate.getDescription());

        verify(httpRequest, atLeastOnce()).getRequestDispatcher("/confirmUpdate.jsp");
        verify(requestDispatcher, atLeastOnce()).forward(httpRequest, httpResponse);

        assertEquals(oldTask.getName(), taskToUpdate.getName());
        assertEquals(oldTask.getDescription(), taskToUpdate.getDescription());
    }

    @Test
    void updateStatus() throws SQLException, IOException, ServletException {
        String taskId = "0";
        String taskStatus = "true";
        // Arrange
        when(httpRequest.getParameter("id")).thenReturn(taskId);
        when(httpRequest.getParameter("status")).thenReturn(taskStatus);

        when(DriverManager.getConnection(SQL_URL, SQL_USER, SQL_PASSWORD)).thenReturn(connection);
        when(connection.prepareStatement("UPDATE bongotasks.tasks SET status = " + taskStatus + " WHERE id = " + taskId)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        taskServlet.updateStatus(httpRequest, httpResponse);

        // Assert
        verify(httpRequest, atLeastOnce()).getParameter("id");
        verify(httpRequest, atLeastOnce()).getParameter("status");
        verify(httpResponse, atLeastOnce()).sendRedirect(httpRequest.getContextPath() + "/");
    }

    @Test
    void deleteTask() throws ServletException, SQLException, IOException {
        String deleteId = "1";

        // Arrange
        when(httpRequest.getParameter("delete-id")).thenReturn(deleteId);
        when(httpRequest.getRequestDispatcher("/confirmDelete.jsp")).thenReturn(requestDispatcher);

        when(DriverManager.getConnection(SQL_URL, SQL_USER, SQL_PASSWORD)).thenReturn(connection);
        when(connection.prepareStatement("DELETE FROM bongotasks.tasks WHERE id = " + deleteId)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        taskServlet.deleteTask(httpRequest, httpResponse);

        // Assert
        verify(httpRequest, atLeastOnce()).getParameter("delete-id");
        verify(httpRequest, atLeastOnce()).getRequestDispatcher("/confirmDelete.jsp");
        verify(requestDispatcher, atLeastOnce()).forward(httpRequest, httpResponse);
    }

    @Test
    void doGet() throws ServletException, IOException {
        // Arrange
        when(httpRequest.getServletPath()).thenReturn("/");

        // Act
        taskServlet.doGet(httpRequest, httpResponse);

        // Assert
        verify(httpRequest, atLeastOnce()).getServletPath();
    }

    @Test
    void doPost() throws ServletException, IOException {
        // Arrange
        when(httpRequest.getServletPath()).thenReturn("/");

        // Act
        taskServlet.doPost(httpRequest, httpResponse);

        // Assert
        verify(httpRequest, atLeastOnce()).getServletPath();
    }
}