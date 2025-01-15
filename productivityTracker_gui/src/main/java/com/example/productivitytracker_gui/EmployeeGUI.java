package com.example.productivitytracker_gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeGUI extends Application {
    private static String name;
    private static String password;
    public EmployeeGUI() {
    }
    public EmployeeGUI(String name, String password) {
        EmployeeGUI.name = name;
        EmployeeGUI.password = password;
    }
    @Override
    public void start(Stage primaryStage) {
        showLoginScreen(primaryStage);
    }

    private void showLoginScreen(Stage stage) {

        Label nameLabel = new Label("Username:");
        TextField nameField = new TextField();
        User user=new User();
        String dateStr=user.date_formatter();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Login");

        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.getChildren().addAll(nameLabel, nameField, passwordLabel, passwordField, loginButton);

        Scene loginScene = new Scene(loginLayout, 400, 300);
        stage.setTitle("Employee Login");
        stage.setScene(loginScene);
        stage.show();

        loginButton.setOnAction(_ -> {
            name = nameField.getText().trim();
            password = passwordField.getText().trim();

            if (name.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter both username and password.");
                return;
            }
            List<String> users= User.getUsersfromDB("readonly_user", "readonly_password");
            boolean logged_in=false;
            for(String userDB:users){
                String [] user_content=userDB.replace("[", "").replace("]", "").split(",");
                if(user_content[0].trim().equals(name)&&user_content[1].trim().equals(password)){
                    showMenuScreen(stage);
                    name=user_content[0].trim();
                    logged_in=true;
                }
            }
            if(!logged_in)
            {
                User.addAuthenDatatoDB(dateStr+",E101,"+name,"writeonly_user","writeonly_password");
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        });
    }

    public void showMenuScreen(Stage stage) {
        Button viewTasksButton = new Button("View Tasks for Today");
        Button startSessionButton = new Button("Begin Working Session");
        Button feedbackButton = new Button("Feedback");
        Button viewSessionsButton = new Button("View Past Sessions");
        Button statisticsButton = new Button("View Performance Statistics");
        Button endShiftButton = new Button("End Shift");
        Button exitButton = new Button("Exit");

        VBox menuLayout = new VBox(10);
        menuLayout.setPadding(new Insets(20));
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.getChildren().addAll(viewTasksButton, startSessionButton, viewSessionsButton,  statisticsButton, endShiftButton,feedbackButton, exitButton);

        Scene menuScene = new Scene(menuLayout, 400, 300);
        stage.setTitle("Employee Menu");
        stage.setScene(menuScene);

        viewTasksButton.setOnAction(_ -> showTasksScreen(stage));
        startSessionButton.setOnAction(_ -> {
            Session.beginNewSession(stage,name);
        });
        viewSessionsButton.setOnAction(_ -> showSessionsScreen(stage));
        endShiftButton.setOnAction(_ -> endShift(stage));
        statisticsButton.setOnAction(_ ->showStatisticsScreen(stage));
        feedbackButton.setOnAction(_ ->addFeedback(stage));
        exitButton.setOnAction(_ -> stage.close());
    }
    private void addFeedback(Stage stage){
        stage.setTitle("Feedback");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        Label feedbackLabel = new Label("Feedback:");
        TextArea feedbackArea= new TextArea();
        feedbackArea.setPrefHeight(200);
        feedbackArea.setWrapText(true);
        Button submitButton = new Button("Submit Feedback");
        TextField statusField= new TextField ();
        statusField.setEditable(false);
        Button backButton=new Button("Back");
        backButton.setOnAction(_ -> showMenuScreen(stage));
        submitButton.setOnAction(e -> {
            String feedbackText = feedbackArea.getText().trim();

            if (feedbackText.isEmpty()) {
                statusField.appendText("Feedback cannnot be empty.\n");
                return;
            }
            User user=new User();
            String dateStr = user.date_formatter();
            String feedbackEntry = dateStr + "," + feedbackText;

            User.writeToFile("feedback.txt", feedbackEntry, false);

            statusField.appendText("Feedback submitted on " + dateStr + ".\n");
            feedbackArea.clear();
        });
        layout.getChildren().addAll( feedbackLabel, feedbackArea, submitButton, statusField , backButton);
        Scene scene = new Scene(layout, 400, 400);
        stage.setScene(scene);
        stage.show();
    }
    private void showTasksScreen(Stage stage) {
        User user = new User();
        String dateStr = user.date_formatter();
        List<String[]> tasks = new ArrayList<>();
        List<String> lines = User.getTasksfromDB("limited_user", "limited_password");
        String employeename=null;
        for (String line : lines) {
            String[] credentials = line.replace("[", "").replace("]", "").split(",");
            if (name.equals(credentials[1].trim())) {
                tasks.add(new String[]{credentials[2].trim(), credentials[3].trim()});
                if(employeename==null)
                {
                    employeename=credentials[1].trim();
                }
            }

        }

        VBox tasksLayout = new VBox(10);
        tasksLayout.setPadding(new Insets(20));
        tasksLayout.setAlignment(Pos.TOP_LEFT);

        Label titleLabel = new Label("Tasks for Today (" + dateStr + "):");
        tasksLayout.getChildren().add(titleLabel);

        for (int i = 0; i < tasks.size(); i++) {
            HBox taskBox = new HBox(10);
            taskBox.setAlignment(Pos.CENTER_LEFT);

            String taskName = tasks.get(i)[0];
            String importance = tasks.get(i)[1];

            Label taskLabel = new Label((i + 1) + ". " + taskName + " (Importance: " + importance + ")");
            Button completedButton = new Button("Completed");

            String finalEmployeename = employeename;
            completedButton.setOnAction(e -> {
                tasksLayout.getChildren().remove(taskBox);
                deleteFinishedTaskfromDB(dateStr, finalEmployeename,taskName,Integer.parseInt(importance));
                User.addFinishedTasktoDB(dateStr, finalEmployeename,taskName,Integer.parseInt(importance));
                User.getFinishedTasksfromDB();
            });

            taskBox.getChildren().addAll(taskLabel, completedButton);
            tasksLayout.getChildren().add(taskBox);
        }

        Button backButton = new Button("Back");
        tasksLayout.getChildren().add(backButton);

        Scene tasksScene = new Scene(tasksLayout, 400, 300);
        stage.setTitle("Tasks");
        stage.setScene(tasksScene);

        backButton.setOnAction(_ -> showMenuScreen(stage));
    }

    private void showStatisticsScreen(Stage stage) {
        stage.setTitle("Performance");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        Button backButton = new Button("Back");
        List<Double> stats = Statistics.statisticsForEmployees(name);

        double generalPerformance = stats.get(0); // General
        Label generalLabel = new Label("General Performance: ");
        Label generalValue = createColoredLabel(String.format("%.2f", generalPerformance) + "%", generalPerformance);
        HBox generalBox = new HBox(5, generalLabel, generalValue);
        layout.getChildren().add(generalBox);

        double monthlyPerformance = stats.get(2); // Monthly
        Label monthlyLabel = new Label("Monthly Performance: ");
        Label monthlyValue = createColoredLabel(String.format("%.2f", monthlyPerformance) + "%", monthlyPerformance);
        HBox monthlyBox = new HBox(5, monthlyLabel, monthlyValue);
        backButton.setOnAction(_ -> showMenuScreen(stage));
        layout.getChildren().add(monthlyBox);
        layout.getChildren().add(backButton);

        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private Label createColoredLabel(String text, double percentage) {
        Label label = new Label(text);
        label.setStyle("-fx-background-color: " + getPerformanceColor(percentage) + "; -fx-text-fill: black; -fx-padding: 5;");
        return label;
    }

    private String getPerformanceColor(double percentage) {
        if (percentage >= 80) {
            return "lightgreen";
        } else if (percentage >= 50) {
            return "yellow";
        } else {
            return "red";
        }
    }


    private void showSessionsScreen(Stage stage) {
        User user = new User();
        String dateStr = user.date_formatter();
        List<String> lines = User.readFromFile("sessions.txt");

        VBox sessionsLayout = new VBox(10);
        sessionsLayout.setPadding(new Insets(20));
        sessionsLayout.setAlignment(Pos.TOP_LEFT);

        Label titleLabel = new Label("Sessions for Today (" + dateStr + "):");
        sessionsLayout.getChildren().add(titleLabel);

        for (String line : lines) {
            String[] info = line.split(",");
            if (name.equals(info[1]) && dateStr.equals(info[0])) {
                sessionsLayout.getChildren().add(new Label("Task: " + info[2] + " | Duration: " + info[3]));
            }
        }

        Button backButton = new Button("Back");
        sessionsLayout.getChildren().add(backButton);

        Scene sessionsScene = new Scene(sessionsLayout, 400, 300);
        stage.setTitle("Sessions");
        stage.setScene(sessionsScene);

        backButton.setOnAction(_ -> showMenuScreen(stage));
    }

    private void endShift(Stage stage) {

        List<String> tasks= User.getTasksfromDB("limited_user","limited_password");
        for (String task : tasks) {
            String[] credentials = task.replace("[", "").replace("]", "").split(",");
            if (name.equals(credentials[1].trim())) {
                showAlert(Alert.AlertType.WARNING, "Tasks left", "You still have uncompleted tasks today");
                return;
            }

        }
        showAlert(Alert.AlertType.INFORMATION, "Shift Ended", "Shift ended successfully.");
        stage.close();

    }
    private void deleteFinishedTaskfromDB(String date, String employeename,String taskName, int importance) {
        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/taskmanagerp3",
                    "limited_user",
                    "limited_password");

            String query = "DELETE FROM TASKS WHERE employeename = ? AND taskname=? AND importance=? ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, employeename);
            preparedStatement.setString(2, taskName);
            preparedStatement.setInt(3, importance);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Task successfully deleted");
            } else {
                System.out.println("Failed to delete the task.");
            }

            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting employee", e);
        }
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static String getNameField() {
        return name;
    }

    public static String getPasswordField() {
        return password;
    }

}
