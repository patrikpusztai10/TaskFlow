package com.example.productivitytracker_gui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Statistics {

    public static List<Double> statisticsForEmployees(String name) {
        if(User.isUsernameAvailable(name))
            return null;
        List<Double> empStats = new ArrayList<>();
        List<String> unfinishedTasksDB = User.getTasksfromDB("limited_user", "limited_password");
        List<String> finishedTasksDB = User.getFinishedTasksfromDB();

        List<String> unfinishedTasks = new ArrayList<>();
        List<String> monthlyUnfinishedTasks = new ArrayList<>();
        List<String> finishedTasks = new ArrayList<>();
        List<String> monthlyFinishedTasks = new ArrayList<>();

        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-yyyy"));

        // Process unfinished tasks
        for (String task : unfinishedTasksDB) {
            String[] credentials = parseTask(task);
            if (credentials != null && credentials[1].trim().equals(name)) {
                unfinishedTasks.add(task);
                if (credentials[0].trim().endsWith(currentMonth)) {
                    monthlyUnfinishedTasks.add(task);
                }
            }
        }

        for (String task : finishedTasksDB) {
            String[] credentials = parseTask(task);
            if (credentials != null && credentials[1].trim().equals(name)) {
                finishedTasks.add(task);
                if (credentials[0].trim().endsWith(currentMonth)) {
                    monthlyFinishedTasks.add(task);
                }
            }
        }
        int totalTasks = unfinishedTasks.size() + finishedTasks.size();
        if (totalTasks == 0) {
            empStats.add(0.0);
            empStats.add(0.0);
            empStats.add(0.0);
            empStats.add(0.0);
            return empStats;
        }

        double percentFinishedTasks = (double) finishedTasks.size() * 100 / totalTasks;
        double percentUnfinishedTasks = (double) unfinishedTasks.size() * 100 / totalTasks;

        empStats.add(percentFinishedTasks);
        empStats.add(percentUnfinishedTasks);

        int monthlyTotalTasks = monthlyUnfinishedTasks.size() + monthlyFinishedTasks.size();
        if (monthlyTotalTasks > 0) {
            double monthlyPercentFinished = (double) monthlyFinishedTasks.size() * 100 / monthlyTotalTasks;
            double monthlyPercentUnfinished = (double) monthlyUnfinishedTasks.size() * 100 / monthlyTotalTasks;
            empStats.add(monthlyPercentFinished);
            empStats.add(monthlyPercentUnfinished);
        } else {
            empStats.add(0.0);
            empStats.add(0.0);
        }

        System.out.println(empStats);
        return empStats;
    }

    private static String[] parseTask(String task) {
        try {
            return task.replace("[", "").replace("]", "").split(",");
        } catch (Exception e) {
            System.err.println("Error parsing task: " + task);
            return null;
        }
    }
}
