package com.example.productivitytracker_gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TaskManager");
        Label welcomeLabel = new Label("Welcome to Task Manager");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-family:Lucida Sans Unicode; -fx-font-weight: bold;");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Teamleader", "Employee", "Admin");
        roleComboBox.setPromptText("Select your role");

        Button loginButton = new Button("Login");
        loginButton.setDisable(true);

        roleComboBox.setOnAction(e -> loginButton.setDisable(false));


        loginButton.setOnAction(e -> {
            String selectedRole = roleComboBox.getValue();
            if (selectedRole != null) {
                switch (selectedRole) {
                    case "Teamleader" -> handleTeamLeaderLogin();
                    case "Employee" -> handleEmployeeLogin();
                    case "Admin" -> handleAdminLogin();
                    default -> showAlert(Alert.AlertType.ERROR, "Error", "Invalid option.");
                }
            }
        });

        VBox root = new VBox(15, welcomeLabel, roleComboBox, loginButton);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f4f4f4;");
        Scene scene = new Scene(root, 300, 200);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleTeamLeaderLogin() {
        showAlert(Alert.AlertType.INFORMATION, "Login", "Logging in as: Teamleader");
        TeamLeaderGUI t=new TeamLeaderGUI();
        t.start(new Stage());
    }

    private void handleEmployeeLogin() {
        showAlert(Alert.AlertType.INFORMATION, "Login", "Logging in as: Employee");
        EmployeeGUI e=new EmployeeGUI();
            e.start(new Stage());
    }

    private void handleAdminLogin() {
        showAlert(Alert.AlertType.INFORMATION, "Login", "Logging in as: Admin");
        AdminGUI a=new AdminGUI();
        a.start(new Stage());
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
