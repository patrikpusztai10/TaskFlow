package com.example.productivitytracker_gui;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    @Test
    public void test() {

       Session s=new Session();
       assertNotNull(s,"The object session is not null");

    }
    @Test
    public void testTimeElapsedCalculation() {
        // Random values for session start and end time
        LocalDateTime sessionStart = LocalDateTime.of(2023, 1, 1, 9, 0, 0);
        LocalDateTime sessionEnd = LocalDateTime.of(2023, 1, 1, 10, 30, 15);

        Duration duration = Duration.between(sessionStart, sessionEnd);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        String timeElapsed = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        assertEquals("01:30:15", timeElapsed, "Elapsed time should match the expected value");
    }
}