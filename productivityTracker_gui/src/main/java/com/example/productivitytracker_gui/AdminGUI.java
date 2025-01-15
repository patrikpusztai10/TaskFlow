package com.example.productivitytracker_gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminGUI extends Application {
    private static String name="";
    private static String password="";
    @Override
    public void start(Stage primaryStage) {
        showLoginScreen(primaryStage);
    }
    public AdminGUI(){

    }
    public AdminGUI(String name,String password){
        this.name=name;
        this.password=password;
    }

    public void showLoginScreen(Stage stage) {

        Label nameLabel = new Label("Username:");
        TextField nameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Login");

        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.getChildren().addAll(nameLabel, nameField, passwordLabel, passwordField, loginButton);

        Scene loginScene = new Scene(loginLayout, 400, 300);
        stage.setTitle("Admin Login");
        stage.setScene(loginScene);
        stage.show();

        loginButton.setOnAction(_ -> {
            name = nameField.getText().trim();
            password = passwordField.getText().trim();

            if (name.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter both username and password.");
                return;
            }
            else if(!(name.equals("admin")&& password.equals("12345"))){
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Incorrect username and password combination.");
            }
            else {
                showMenuScreen(stage);
            }



        });
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showMenuScreen(Stage stage) {
        Button suspiciousLoginsButton = new Button("Suspicious logins");
        Button exitButton = new Button("Exit");

        VBox menuLayout = new VBox(10);
        menuLayout.setPadding(new Insets(20));
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.getChildren().addAll(suspiciousLoginsButton, exitButton);

        Scene menuScene = new Scene(menuLayout, 400, 300);
        stage.setTitle("Admin Menu");
        stage.setScene(menuScene);

        suspiciousLoginsButton.setOnAction(_ -> showSuspiciousLogins(stage));
        exitButton.setOnAction(_ -> stage.close());
    }
    private void showSuspiciousLogins(Stage stage) {
        VBox root = new VBox();
        root.setSpacing(10);

        Label label = new Label("Suspicious Logins:");
        ListView<String> listView = new ListView<>();
        Button goBackButton = new Button("Go Back");

        List<String> allLines = getAuthenDatafromDB();

        List<String> suspiciousLogins = new ArrayList<>();
        for (String line : allLines) {
            String[] content=line.split(",");
            if (content[1].equals("E101")) {
                suspiciousLogins.add(content[0].replace("[","")+": Invalid username or password entered by "+content[2].replace("]",""));
            }
        }


        listView.getItems().addAll(suspiciousLogins);
        goBackButton.setOnAction(e -> showMenuScreen(stage));

        root.getChildren().addAll(label, listView, goBackButton);
        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Suspicious Logins");
        stage.setScene(scene);
        stage.show();
    }
    public static String getNameField() {
        return name;
    }

    public static String getPasswordField() {
        return password;
    }
    public static List<String> getAuthenDatafromDB() {
        List<String> userCredentials=new ArrayList<>();
        List<String> user=new ArrayList<>();
        Connection connection;
        try {

            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/taskmanagerp3",
                    "admin",
                    "12345");

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM AUTHENDATA");

            while(resultSet.next())
            {
                user.add(resultSet.getString("errortype"));
                userCredentials.add(user.toString());
                user.clear();
            }

            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching authentification data from the database", e);
        }
        System.out.println(userCredentials);

        return userCredentials;
    }


}
