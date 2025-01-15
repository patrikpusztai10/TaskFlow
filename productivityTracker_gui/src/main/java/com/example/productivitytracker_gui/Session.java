package com.example.productivitytracker_gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class Session extends Application {
    private static LocalDateTime sessionStart;
    private static String selectedTask;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    }

    public static void beginNewSession(Stage stage, String name) {
        User user= new User();
        String initDate = user.date_formatter();
        Button backButton=new Button("Back");
        EmployeeGUI emp = new EmployeeGUI();
        backButton.setOnAction(_ -> emp.showMenuScreen(stage));

        List<String> nameOfTasks = new ArrayList<>();
        List<String> lines = User.getTasksfromDB("limited_user", "limited_password");
        for (String line : lines) {
            String[] credentials = line.replace("[", "").replace("]", "").split(",");
            if (name.equals(credentials[1].trim())) {
                nameOfTasks.add(credentials[2].trim().replace("[", "").replace("]", ""));

            }

        }
        if (nameOfTasks.isEmpty()) {
            showAlert("Info", "No tasks available for today.", Alert.AlertType.INFORMATION);
            return;
        }

        VBox taskLayout = new VBox(10);
        taskLayout.setPadding(new Insets(15));

        Label taskLabel = new Label("Select a task to work on:");
        ListView<String> taskListView = new ListView<>();
        for (String task : nameOfTasks) {
            String formattedTask = task.replace("[", "").replace("]", "");
            taskListView.getItems().add(formattedTask);
        }

        Button selectTaskButton = new Button("Start Task");
        selectTaskButton.setOnAction(e -> {
            selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask == null) {
                showAlert("Error", "Please select a task.", Alert.AlertType.ERROR);
                return;
            }
            sessionStart = LocalDateTime.now();
            showEndSessionScene(stage, name, initDate);
        });

        taskLayout.getChildren().addAll(taskLabel, taskListView, selectTaskButton,backButton);

        Scene taskScene = new Scene(taskLayout, 400, 300);
        stage.setScene(taskScene);
    }

    private static void showEndSessionScene(Stage stage, String name, String initDate) {
        VBox endLayout = new VBox(10);
        endLayout.setPadding(new Insets(15));

        Label endLabel = new Label("Task in progress: " + selectedTask);
        Button endButton = new Button("End Session");

        endButton.setOnAction(e -> {
            LocalDateTime sessionEnd = LocalDateTime.now();
            Duration duration = Duration.between(sessionStart, sessionEnd);
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            long seconds = duration.toSecondsPart();

            String timeElapsed = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            User.writeToFile("sessions.txt", initDate + ',' + name + ',' + selectedTask + ',' + timeElapsed+'\n',true);
            showAlert("Success", "Session ended. Time elapsed: " + timeElapsed, Alert.AlertType.INFORMATION);
            EmployeeGUI emp=new EmployeeGUI();
                    emp.showMenuScreen(stage);
        });

        endLayout.getChildren().addAll(endLabel, endButton);

        Scene endScene = new Scene(endLayout, 300, 150);
        stage.setScene(endScene);
    }



    private static void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
