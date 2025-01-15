package com.example.productivitytracker_gui;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.io.InputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.FileNotFoundException;

public class User implements DateInterface {
    static String name;
    static String password;
    public User(){

    }
    public User(String name, String password){
        this.name = name;
        this.password = password;
    }
    @Override
    public String date_formatter()
    {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return date.format(dateFormatter);
    }

    public static List<String> readFromFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (InputStream inputStream = User.class.getClassLoader().getResourceAsStream(filename)) {
            if (inputStream == null) {
                throw new FileNotFoundException("File not found: " + filename);
            }
            Scanner fileReader = new Scanner(inputStream);
            while (fileReader.hasNextLine()) {
                lines.add(fileReader.nextLine());
            }
            fileReader.close();
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
        return lines;
    }


    public static void writeToFile(String filename, String content, boolean append) {
        try {

            String filePath = "src/main/resources/" + filename;
            FileWriter fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
    public static String getNameField() {
        return name;
    }

    public static String getPasswordField() {
        return password;
    }
    public static List<String> getUsersfromDB(String currentUsername, String currentPassword) {
        List<String> userCredentials=new ArrayList<>();
        List<String> user=new ArrayList<>();
        Connection connection;
        try {

            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/taskmanagerp3",
                    currentUsername,
                    currentPassword);

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM USERS");

            while(resultSet.next())
            {
                user.add(resultSet.getString("username"));
                user.add(resultSet.getString("password"));
                userCredentials.add(user.toString());
                user.clear();
            }

            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching users from the database", e);
        }
        System.out.println(userCredentials);

        return userCredentials;
    }
    public static List<String> getTasksfromDB(String username, String password) {
        List<String> tasks = new ArrayList<>();
        String dbUrl = "jdbc:mysql://127.0.0.1:3306/taskmanagerp3";

        try (Connection connection = DriverManager.getConnection(dbUrl, username.trim(), password.trim());
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM TASKS")) {

            while (resultSet.next()) {
                List<String> task = new ArrayList<>();
                String date = resultSet.getString("date");
                String employeename = resultSet.getString("employeename");
                String taskname = resultSet.getString("taskname");
                int importance = resultSet.getInt("importance");

                task.add(date);
                task.add(employeename);
                task.add(taskname);
                task.add(String.valueOf(importance));
                tasks.add(task.toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching tasks from the database", e);
        }

        System.out.println(tasks);
        return tasks;
    }

    public static List<String> getFinishedTasksfromDB() {
        List<String> finishedtasks= new ArrayList<>();
        List<String> task=new ArrayList<>();
        Connection connection;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/taskmanagerp3",
                    "root",
                    "1234567");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM FINISHEDTASKS");

            while (resultSet.next()) {
                String date = resultSet.getString("date");
                String employeename = resultSet.getString("employeename");
                String taskname = resultSet.getString("taskname");
                int importance = resultSet.getInt("importance");

                task.add(date);
                task.add(employeename);
                task.add(taskname);
                task.add(String.valueOf(importance));
                finishedtasks.add(task.toString());
                task.clear();
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching tasks from the database", e);
        }
        System.out.println(finishedtasks);
        return finishedtasks;
    }

    public static boolean isUsernameAvailable(String username) {
        Connection connection;
        boolean isAvailable = true;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/taskmanagerp3",
                    "root",
                    "1234567");

            String query = "SELECT COUNT(*) AS count FROM USERS WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                if (count > 0) {
                    isAvailable = false;
                }
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error while checking username availability", e);
        }

        return isAvailable;
    }


    public static void addUsertoDB(String username, String password, String currentUsername, String currentPassword) {
        Connection connection;
        try {

            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/taskmanagerp3",
                    currentUsername,
                    currentPassword);

            // Only 'tina' should have access to add users
            if (!"tina".equals(currentUsername) || !"12345".equals(currentPassword)) {
                System.out.println("Access Denied: You do not have permission to add users.");
                return;
            }

            String query = "INSERT INTO USERS (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User successfully added to the database!");
            } else {
                System.out.println("Failed to add the user.");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error while adding user to the database", e);
        }
    }

    public static void addTasktoDB(String date, String employeename, String taskname, int importance, String username, String password) {
        String dbUrl = "jdbc:mysql://127.0.0.1:3306/taskmanagerp3";
        String query = "INSERT INTO TASKS (date, employeename, taskname, importance) VALUES (?, ?, ?, ?)";

        // Only 'tina' should be allowed to add tasks
        if (!"tina".equals(username.trim()) || !"12345".equals(password.trim())) {
            System.out.println("Access Denied: You do not have permission to add tasks.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, date);
            preparedStatement.setString(2, employeename);
            preparedStatement.setString(3, taskname);
            preparedStatement.setInt(4, importance);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Task successfully added to the database!");
            } else {
                System.out.println("Failed to add the task.");
            }
        } catch (SQLException e) {
            System.err.println("Error while adding task to the database:");
            e.printStackTrace();
        }
    }

    public static void addFinishedTasktoDB(String date, String employeename, String taskname, int importance) {
        String dbUrl = "jdbc:mysql://127.0.0.1:3306/taskmanagerp3";
        String dbUsername = "root";
        String dbPassword = "1234567";
        String query = "INSERT INTO FINISHEDTASKS (date, employeename, taskname, importance) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {


            preparedStatement.setString(1, date);
            preparedStatement.setString(2, employeename);
            preparedStatement.setString(3, taskname);
            preparedStatement.setInt(4, importance);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Task successfully added to the database!");
            } else {
                System.out.println("Failed to add the task.");
            }
        } catch (SQLException e) {
            System.err.println("Error while adding task to the database:");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void AddErrorToDB(String errortype) {
        String dbUrl = "jdbc:mysql://127.0.0.1:3306/authendata";
        String dbUsername = "admin";
        String dbPassword = "your_admin_password";
        String query = "INSERT INTO AUTHENDATA (errortype) VALUES (?)";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, errortype);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Error successfully added to the database!");
            } else {
                System.out.println("Failed to add the error.");
            }
        } catch (SQLException e) {
            System.out.println("Access Denied or Error Occurred: " + e.getMessage());
        }
    }
    public static void addAuthenDatatoDB(String errortype,String username, String password) {
        Connection connection;
        try {

            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/taskmanagerp3",
                    username,
                    password);

            if ("admin".equals(username) || "12345".equals(password)) {
                System.out.println("Access Denied.");
                return;
            }

            String query = "INSERT INTO AUTHENDATA (errortype) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, errortype);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Authentification data successfully added to the database!");
            } else {
                System.out.println("Failed to add the authentification data .");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error while adding authentification data  to the database", e);
        }
    }


}
