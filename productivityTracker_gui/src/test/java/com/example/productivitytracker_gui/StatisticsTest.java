package com.example.productivitytracker_gui;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsTest {
    @Test
    public void test() {

        Statistics s=new Statistics();
        assertNotNull(s,"The object session is not null");
        List<Double> statistics=Statistics.statisticsForEmployees("patrik");
        assertNotNull(statistics,"The statistics for patrik is null");
        assertEquals(statistics.size(),4,"The statistics for patrik is empty");
        List<Double> stat_other_user=Statistics.statisticsForEmployees("alexandra");
        assertNotNull(stat_other_user,"The user Alexandra doesn't exist");

    }
}