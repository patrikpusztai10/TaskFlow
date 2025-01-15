package com.example.productivitytracker_gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeamLeaderGUI extends Application {

    private static String currentUsername;
    private static String password;

    public TeamLeaderGUI() {

    }
    public TeamLeaderGUI(String username, String password) {
        currentUsername = username;
        this.password = password;
    }
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Team Leader Login");
        User user=new User();
        String dateStr=user.date_formatter();
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Login");
        Label loginStatusLabel = new Label();

        VBox loginLayout = new VBox(10);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setPadding(new Insets(20));
        loginLayout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton, loginStatusLabel);

        Scene loginScene = new Scene(loginLayout, 400, 300);
        primaryStage.setScene(loginScene);
        primaryStage.show();

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                loginStatusLabel.setText("Please fill in all fields.");
                return;
            }

            if (username.equals("tina")&&password.equals("12345")) {
                currentUsername = username;
                loginStatusLabel.setText("Login successful!");
                showMainMenu(primaryStage);
            } else {
                loginStatusLabel.setText("Incorrect username or password.");
                User.addAuthenDatatoDB(dateStr+",E101,"+username,"writeonly_user","writeonly_password");

            }
        });
    }

    private void showMainMenu(Stage stage) {
        stage.setTitle("Team Leader Menu");

        Button createAccountButton = new Button("Create Employee Account");
        Button viewEmployeesButton = new Button("View Employees");
        Button deleteEmployeeButton = new Button("Delete Employee");
        Button assignTasksButton = new Button("Assign Tasks");
        Button viewTasksButton = new Button("View Remaining Tasks");
        Button viewPerformanceStatistics= new Button("View Performance Statistics");
        Button viewFeedbacksButton = new Button("View Feedbacks");
        Button endShiftButton = new Button("End Shift");
        Button exitButton=new Button("Exit");


        VBox menuLayout = new VBox(10);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setPadding(new Insets(20));
        menuLayout.getChildren().addAll(
                createAccountButton, viewEmployeesButton, deleteEmployeeButton,
                assignTasksButton, viewTasksButton, viewPerformanceStatistics, viewFeedbacksButton,
                endShiftButton,exitButton
        );

        Scene menuScene = new Scene(menuLayout, 400, 400);
        stage.setScene(menuScene);

        createAccountButton.setOnAction(e -> showCreateAccount(stage));
        viewEmployeesButton.setOnAction(e -> viewEmployees(stage));
        deleteEmployeeButton.setOnAction(e -> showDeleteEmployee(stage));
        assignTasksButton.setOnAction(e -> showAssignTasks(stage));
        viewTasksButton.setOnAction(e -> viewTasks(stage));
        viewPerformanceStatistics.setOnAction(e-> showStatisticsScreen(stage));
        viewFeedbacksButton.setOnAction(e -> viewFeedbacks(stage));
        endShiftButton.setOnAction(e -> endShift(stage));
        exitButton.setOnAction(_ -> stage.close());
    }

    private void showCreateAccount(Stage stage) {
        stage.setTitle("Create Employee Account");

        Label nameLabel = new Label("Employee Username:");
        TextField nameField = new TextField();

        Label passwordLabel = new Label("Employee Password:");
        PasswordField passwordField = new PasswordField();

        Button createButton = new Button("Create Account");
        Button backButton = new Button("Back");

        Label statusLabel = new Label();

        VBox createLayout = new VBox(10);
        createLayout.setAlignment(Pos.CENTER);
        createLayout.setPadding(new Insets(20));
        createLayout.getChildren().addAll(nameLabel, nameField, passwordLabel, passwordField, createButton, backButton, statusLabel);

        Scene createScene = new Scene(createLayout, 400, 300);
        stage.setScene(createScene);

        createButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String password = passwordField.getText().trim();

            if (name.isEmpty() || password.isEmpty()) {
                statusLabel.setText("All fields must be filled.");
                return;
            }
            if(!User.isUsernameAvailable(name)) {
                statusLabel.setText("Username is already in use.");
                return;
            }
            if (password.length() < 2) {
                statusLabel.setText("Password must be at least 8 characters long.");
                return;
            }
            User.addUsertoDB(name,password,"tina","12345");
            statusLabel.setText("Account created for " + name);
        });

        backButton.setOnAction(e -> showMainMenu(stage));
    }

    private void viewEmployees(Stage stage) {
        stage.setTitle("View Employees");

        TextArea employeeListArea = new TextArea();
        employeeListArea.setEditable(false);

        Button backButton = new Button("Back");

        VBox viewLayout = new VBox(10);
        viewLayout.setAlignment(Pos.CENTER);
        viewLayout.setPadding(new Insets(20));
        viewLayout.getChildren().addAll(new Label("Employee List:"), employeeListArea, backButton);

        Scene viewScene = new Scene(viewLayout, 400, 300);
        stage.setScene(viewScene);

        List<String> users = User.getUsersfromDB("tina","12345");
        StringBuilder employeeList = new StringBuilder();

        for (String user : users) {
            String[] data = user.replace("[", "").replace("]", "").split(",");
            if (data.length > 0) {
                employeeList.append(data[0].trim()).append("\n");
            }
        }


        employeeListArea.setText(employeeList.toString());
        backButton.setOnAction(e -> showMainMenu(stage));

    }

    private void showDeleteEmployee(Stage stage) {
        stage.setTitle("Delete Employee");

        Label nameLabel = new Label("Employee Name:");
        TextField nameField = new TextField();

        Button deleteButton = new Button("Delete");
        Button backButton = new Button("Back");

        Label statusLabel = new Label();

        VBox deleteLayout = new VBox(10);
        deleteLayout.setAlignment(Pos.CENTER);
        deleteLayout.setPadding(new Insets(20));
        deleteLayout.getChildren().addAll(nameLabel, nameField, deleteButton, backButton, statusLabel);

        Scene deleteScene = new Scene(deleteLayout, 400, 300);
        stage.setScene(deleteScene);

        // Delete button logic
        deleteButton.setOnAction(e -> {
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                statusLabel.setText("Name field cannot be empty.");
                return;
            }

            boolean isDeleted = deleteEmployeeFromDB(name);
            if (isDeleted) {
                statusLabel.setText("Employee deleted: " + name);
            } else {
                statusLabel.setText("Employee not found: " + name);
            }
        });

        backButton.setOnAction(e -> showMainMenu(stage));
    }
    private void showStatisticsScreen(Stage stage) {
        stage.setTitle("Performance");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        Button backButton = new Button("Back");
        List<String> employeesDB=User.getUsersfromDB("tina","12345");
        List<String> employees= new ArrayList<>();
        for(String employee : employeesDB) {
            String[] data = employee.replace("[","").replace("]","").split(",");
            employees.add(data[0].trim());
        }
        for(String employee : employees) {
        List<Double> stats = Statistics.statisticsForEmployees(employee);

        double generalPerformance = stats.get(0); // General

            Label nameLabel = new Label(employee);
            nameLabel.setStyle("-fx-font-weight: bold;");

            Label generalLabel = new Label("General Performance: ");
            Label generalValue = createColoredLabel(String.format("%.2f", generalPerformance) + "%", generalPerformance);

            VBox performanceBox = new VBox(10, nameLabel, new HBox(10, generalLabel, generalValue));
            layout.getChildren().add(performanceBox);


            double monthlyPerformance = stats.get(2); // Monthly
        Label monthlyLabel = new Label("Monthly Performance: ");
        Label monthlyValue = createColoredLabel(String.format("%.2f", monthlyPerformance) + "%", monthlyPerformance);
        HBox monthlyBox = new HBox(5, monthlyLabel, monthlyValue);

        layout.getChildren().add(monthlyBox);

        }
        backButton.setOnAction(_ -> showMainMenu(stage));
        layout.getChildren().add(backButton);
        Scene scene = new Scene(layout, 400, 400);
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
    private boolean deleteEmployeeFromDB(String username) {
        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/taskmanagerp3",
                    "tina",
                    "12345");

            String query = "DELETE FROM USERS WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            int rowsAffected = preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting employee", e);
        }
    }


    private void showAssignTasks(Stage stage) {
        stage.setTitle("Assign Tasks");
        Button backButton = new Button("Back");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label employeeLabel = new Label("Enter Employee Name:");
        TextField employeeField = new TextField();

        Label taskLabel = new Label("Enter Task Name:");
        TextField taskField = new TextField();

        Label importanceLabel = new Label("Enter Task Importance [1-5]:");
        Spinner<Integer> importanceSpinner = new Spinner<>(1, 5, 1);
        Button assignButton = new Button("Assign Task");
        TextArea statusArea = new TextArea();
        statusArea.setEditable(false);

        assignButton.setOnAction(e -> {
            String employeeName = employeeField.getText().trim();
            String taskName = taskField.getText().trim();
            int importance = importanceSpinner.getValue();

            if (employeeName.isEmpty() || taskName.isEmpty()) {
                statusArea.appendText("Employee name and task name must not be empty.\n");
                return;
            }

            List<String> employeeDB = User.getUsersfromDB("tina", "12345");

            boolean employee_found = false;
            for(String employee : employeeDB) {
                String[] data = employee.replace("[","").replace("]","").split(",");
                if(data[0].equals(employeeName)) {
                    employee_found = true;
                }
            }
            if(!employee_found) {
                statusArea.appendText("Employee not found: " + employeeName + "\n");
                return;
            }

            User user=new User();
            String dateStr = user.date_formatter();
            User.addTasktoDB(dateStr, employeeName, taskName, importance,"tina","12345");


            statusArea.appendText("Task assigned to " + employeeName + ": " + taskName + " (Importance: " + importance + ")\n");

            employeeField.clear();
            taskField.clear();
            importanceSpinner.getValueFactory().setValue(1);
        });


        backButton.setOnAction(e -> showMainMenu(stage));
        layout.getChildren().addAll(employeeLabel, employeeField, taskLabel, taskField, importanceLabel, importanceSpinner, assignButton, statusArea,backButton);

        Scene scene = new Scene(layout, 400, 400);
        stage.setScene(scene);
        stage.show();

    }

    private void viewTasks(Stage stage) {
        stage.setTitle("View Tasks");
        VBox layout = new VBox();
        Label header = new Label("Assigned Tasks for Today:");
        TextArea tasksArea = new TextArea();
        tasksArea.setEditable(false);
        User user=new User();
        String currentDate = user.date_formatter();
        List<String> tasks = User.getTasksfromDB("tina","12345");
        for (String task : tasks) {
            String[] task_content= task.replace("[", "").replace("]", "").split(",");
            if(task_content[0].equals(currentDate)) {
                tasksArea.appendText(task_content[1] + " : "+ task_content[2]+"(Importance:"+task_content[3]+")"+"\n");
            }
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showMainMenu(stage));

        layout.getChildren().addAll(header, tasksArea, backButton);
        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void viewFeedbacks(Stage stage) {
        stage.setTitle("View Feedbacks");
        VBox layout = new VBox();
        Label header = new Label("Feedbacks:");
        TextArea feedbacksArea = new TextArea();
        feedbacksArea.setEditable(false);

        List<String> feedbacks = User.readFromFile("feedback.txt");
        for (String feedback : feedbacks) {
            feedbacksArea.appendText(feedback + "\n");
        }
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showMainMenu(stage));
        layout.getChildren().addAll(header, feedbacksArea,backButton);
        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void endShift(Stage stage) {

        List<String> tasks= User.getTasksfromDB("limited_user","limited_password");
        if(!tasks.isEmpty())
            showAlert(Alert.AlertType.WARNING, "Remaining tasks", "There are uncompleted tasks today");
        else
        {showAlert(Alert.AlertType.INFORMATION, "Shift Ended", "Shift ended successfully.");
            stage.close();}

    }

    public static String getNameField() {
        return currentUsername;
    }

    public static String getPasswordField() {
        return password;
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
